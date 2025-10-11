package com.platform.media_service.services.implementations;

import com.platform.media_service.services.IMimeTypeService;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
/**
 * Service for detecting the MIME type of file using the Apache Tika library.
 * Used to verify file types for validation before storing or serving files.
 */
@Service
public class MimeTypeService implements IMimeTypeService {
    /** Logger for logging information, warnings, and errors. */
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(MimeTypeService.class);
    /**
     * Apache Tika instance used for MIME type detection.
     */
    private final Tika tika;
    /**
     * Default constructor that initializes the Apache Tika instance.
     */
    public MimeTypeService() {
        this.tika = new Tika();
    }

    /**
     * Detects the MIME type of the given file using Apache Tika.
     *
     * @param file the file for which the MIME type is detected.
     * @return the detected MIME type as a string.
     * @throws IOException if an error occurs during detection.
     */
    @Override
    public String getMimeType(File file) throws IOException {
        try {
            return tika.detect(file);
        } catch (IOException e) {
            LOG.error("Error detecting MIME type: {}", e.getMessage());
            throw new IOException("Error while detecting MIME type", e);
        }
    }
}
