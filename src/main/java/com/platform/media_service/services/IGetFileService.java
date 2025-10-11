package com.platform.media_service.services;


import com.platform.media_service.dtos.FileResponseDto;


/**
 * Interface for retrieving file data by its UUID.
 * Provides a method to fetch file metadata and content.
 */
public interface IGetFileService {
    /**
     * Retrieves file data by UUID.
     * Locates the file, checks its integrity,
     * and returns its metadata (name, MIME type, extension, etc.).
     *
     * @param sUuid the UUID of the file to retrieve.
     * @return a {@link FileResponseDto} containing the file's metadata and content.
     */
    FileResponseDto getFileById(String sUuid);
}
