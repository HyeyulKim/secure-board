-- 마이페이지 / 소셜 로그인 대비 컬럼 추가
ALTER TABLE member ADD COLUMN profile_image VARCHAR(255) NULL;
ALTER TABLE member ADD COLUMN provider VARCHAR(20) NULL;      -- NULL: 자체가입, 'kakao', 'google'
ALTER TABLE member ADD COLUMN provider_id VARCHAR(100) NULL;  -- 소셜 서비스 측 고유 ID
ALTER TABLE member ADD COLUMN withdrawn TINYINT(1) DEFAULT 0 NOT NULL; -- 탈퇴 여부(소프트 삭제)
ALTER TABLE member ADD COLUMN withdrawn_at DATETIME NULL;

-- 자체가입 회원은 username 유니크, 소셜 회원은 (provider, provider_id) 조합으로 유니크
ALTER TABLE member ADD UNIQUE KEY uq_member_provider (provider, provider_id);
