package com.portfolio.board.mypage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Component
public class FileStorageService {

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "webp");

    private final Path uploadDir;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDirPath) {
        this.uploadDir = Paths.get(uploadDirPath);
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("업로드 디렉토리를 생성할 수 없습니다: " + uploadDirPath, e);
        }
    }

    /**
     * 파일을 저장하고, 프론트에서 접근 가능한 상대 URL(/uploads/xxx.png)을 반환한다.
     */
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "");
        String extension = extractExtension(originalName);

        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("이미지 파일(jpg, png, gif, webp)만 업로드할 수 있습니다.");
        }

        // 원본 파일명을 그대로 쓰지 않고 UUID로 저장 — 파일명 충돌 방지 + 원본 파일명에 담긴 정보 노출 방지
        String storedName = UUID.randomUUID() + "." + extension;

        try {
            Path target = uploadDir.resolve(storedName);
            file.transferTo(target);
        } catch (IOException e) {
            throw new IllegalStateException("파일 저장에 실패했습니다.", e);
        }

        return "/uploads/" + storedName;
    }

    private String extractExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filename.length() - 1) {
            throw new IllegalArgumentException("파일 확장자를 확인할 수 없습니다.");
        }
        return filename.substring(dotIndex + 1);
    }
}
