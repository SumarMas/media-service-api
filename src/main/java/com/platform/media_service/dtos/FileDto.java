package com.platform.media_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * Data Transfer Object (DTO) for
 * handling file-related information,
 * including the file itself and its
 * associated MD5 and SHA-256 hash values.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {
    /**
     * The file being transferred as a
     * {@link MultipartFile}.
     */
    private MultipartFile file;

    /**
     * The MD5 hash of the file.
     */
    private String hashMd5;

    /**
     * The SHA-256 hash of the file.
     */
    private String hashSha256;
}
