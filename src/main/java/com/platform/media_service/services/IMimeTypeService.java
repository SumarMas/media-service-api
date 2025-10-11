package com.platform.media_service.services;

import java.io.File;
import java.io.IOException;

/**
 * Interface for a service that handles MIME type detection for files.
 * Provides a method to determine the MIME type of the given file.
 */
public interface IMimeTypeService {
    /**
     * Detects and returns the MIME type of the given file.
     * The MIME type identifies the file format,
     * such as "application/pdf" or "image/jpeg".
     *
     * @param file the {@link File} to determine the MIME type for.
     * @return the detected MIME type as a string.
     * @throws IOException if an error occurs while detecting the MIME type.
     */
    String getMimeType(File file) throws IOException;
}
