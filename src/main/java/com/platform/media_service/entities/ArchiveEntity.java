package com.platform.media_service.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity class representing an archive in the system.
 * This class is mapped to the "archives"
 * table in the database and stores metadata
 * about the file, such as its unique identifier (UUID), name, creation date,
 * SHA-256 hash, file path, MIME type, and file extension.
 */
@Entity
@Table(name = "archives")
@Data
public class ArchiveEntity {
    /**
     * Unique identifier for the archive.
     */
    @Id
    private UUID id;

    /**
     * The name of the file.
     * <p>
     * Stored in the "file_name" column of the "archives" table.
     * </p>
     */
    @Column(nullable = false, name = "file_name")
    private String name;

    /**
     * The date and time when the file was created.
     * <p>
     * Stored in the "creation_datetime" column and cannot be null.
     * </p>
     */
    @Column(nullable = false, name = "creation_datetime")
    private LocalDateTime creationDate;

    /**
     * The SHA-256 hash of the file, which is unique for each file.
     * <p>
     * Stored in the "sha256" column, it is a unique and non-nullable value.
     * </p>
     */
    @Column(nullable = false, unique = true, name = "sha256")
    private String hashSha256;

    /**
     * The path where the file is stored on the file system.
     * <p>
     * Stored in the "file_path" column, it is a unique and non-nullable value.
     * </p>
     */
    @Column(nullable = false, unique = true, name = "file_path")
    private String path;

    /**
     * The MIME type of the file (e.g., "application/pdf", "image/jpeg").
     * <p>
     * Stored in the "file_mime_type" column and cannot be null.
     * </p>
     */
    @Column(nullable = false, name = "file_mime_type")
    private String mimeType;

    /**
     * The file extension (e.g., .txt, .pdf, .jpg).
     * <p>
     * Stored in the "file_extension" column and cannot be null.
     * </p>
     */
    @Column(nullable = false, name = "file_extension")
    private String extension;
}
