package com.platform.media_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents the metadata of an archived file,
 * including its ID, name, creation date, hash, path, MIME type, and extension.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArchiveModel {
    /**
     * Unique identifier for the archive.
     */
    private UUID id;

    /**
     * The name of the file.
     */
    private String name;

    /**
     * The date and time when the file was created.
     */
    private LocalDateTime creationDate;

    /**
     * The SHA-256 hash of the file.
     */
    private String hashSha256;

    /**
     * The path where the file is stored on the file system.
     */
    private String path;

    /**
     * The MIME type of the file (e.g., "application/pdf", "image/jpeg").
     */
    private String mimeType;

    /**
     * The file extension (e.g., .txt, .pdf, .jpg).
     */
    private String extension;
}
