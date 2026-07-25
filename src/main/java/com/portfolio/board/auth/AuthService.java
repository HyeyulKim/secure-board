package com.portfolio.board.auth;

import com.portfolio.board.mapper.LoginHistoryMapper;
import com.portfolio.board.mapper.MemberMapper;
import com.portfolio.board.security.JwtTokenProvider;
import com.portfolio.board.vo.LoginHistoryVO;
import com.portfolio.board.vo.MemberVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
public class AuthService {

    // 브루트포스 방어 정책: 10분 내 5회 실패 시 계정 잠금
    private static final int MAX_FAIL_COUNT = 5;
    private static final Duration FAIL_WINDOW = Duration.ofMinutes(10);

    private final MemberMapper memberMapper;
    private final LoginHistoryMapper loginHistoryMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    public AuthService(MemberMapper memberMapper,
                       LoginHistoryMapper loginHistoryMapper,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       StringRedisTemplate redisTemplate) {
        this.memberMapper = memberMapper;
        this.loginHistoryMapper = loginHistoryMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public void signup(SignupRequest request) {
        if (memberMapper.findByUsername(request.getUsername()) != null) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        MemberVO member = new MemberVO();
        member.setUsername(request.getUsername());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setNickname(request.getNickname());
        memberMapper.insert(member);
    }

    @Transactional(noRollbackFor = {IllegalArgumentException.class, AccountLockedException.class})
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String username = request.getUsername();
        String ip = extractIp(httpRequest);
        String failKey = "login:fail:" + username;

        // 1. 브루트포스 체크 (Redis) — DB 조회보다 먼저 확인해서 불필요한 DB 접근 방지
        String failCountStr = redisTemplate.opsForValue().get(failKey);
        int failCount = failCountStr == null ? 0 : Integer.parseInt(failCountStr);
        if (failCount >= MAX_FAIL_COUNT) {
            recordHistory(username, ip, false);
            throw new AccountLockedException("로그인 실패 횟수를 초과해 계정이 잠겼습니다. 잠시 후 다시 시도해주세요.");
        }

        MemberVO member = memberMapper.findByUsername(username);
        boolean success = member != null
                && member.isEnabled()
                && passwordEncoder.matches(request.getPassword(), member.getPassword());

        recordHistory(username, ip, success);

        if (!success) {
            long updated = redisTemplate.opsForValue().increment(failKey);
            if (updated == 1) {
                redisTemplate.expire(failKey, FAIL_WINDOW);
            }
            if (updated >= MAX_FAIL_COUNT) {
                memberMapper.updateEnabled(username, false); // DB에도 잠금 상태 반영 (관리자가 확인 가능하도록)
            }
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 로그인 성공 시 실패 카운트 초기화
        redisTemplate.delete(failKey);

        String token = jwtTokenProvider.generateToken(member.getUsername(), member.getRole());
        return new LoginResponse(token, member.getUsername(), member.getNickname(), member.getRole());
    }

    private void recordHistory(String username, String ip, boolean success) {
        loginHistoryMapper.insert(new LoginHistoryVO(username, ip, success));
    }

    private String extractIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        return (ip != null && !ip.isBlank()) ? ip.split(",")[0] : request.getRemoteAddr();
    }
}