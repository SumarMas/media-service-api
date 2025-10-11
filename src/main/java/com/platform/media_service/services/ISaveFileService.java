package com.platform.media_service.services;

import com.platform.media_service.dtos.FileDto;

import java.util.UUID;

/**
 * Interface for a service that handles file saving operations.
 * Provides a method to save a file
 * after verifying its integrity through optional hash values.
 */
public interface ISaveFileService {
    /**
     * Saves a file, verifying integrity with optional MD5 or SHA-256 hashes.
     * If the file exists (based on SHA-256), it won't be re-saved.
     * Otherwise, a new file is saved, and its metadata is stored.
     *
     * @param fileDto the {@link FileDto} containing the file and optional hashes.
     * @return the {@link UUID} of the saved or existing file.
     */
    UUID saveFile(FileDto fileDto);
}
