package com.platform.media_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
/**
 * Data Transfer Object (DTO) for responding
 * with file-related information,
 * including file metadata such as UUID,
 * hash values, file name, MIME type, file extension,
 * and the actual file content represented as a byte array.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResponseDto {

    /**
     * Unique identifier (UUID) for the file.
     */
    private UUID uuid;

    /**
     * The sha256 hash of the file.
     */
    private String sha256;

    /**
     * The name of the file.
     */
    private String fileName;

    /**
     * The MIME type of the file.
     */
    private String mimeType;

    /**
     * The file extension (e.g., .txt, .pdf, .jpg).
     */
    private String extension;

    /**
     * The byte array representing the file's content.
     */
    private byte[] bytes;

    /**
     * The string base64 representing the file's content.
     */
    private String base64;
}
