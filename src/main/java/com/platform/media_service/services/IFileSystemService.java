package com.platform.media_service.services;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface for interacting with the file system.
 * Provides methods for checking file existence,
 * reading file content, creating directories, writing files
 * and get string base64 encoding.
 */
public interface IFileSystemService {

    /**
     * Checks if a file or directory exists at the specified path.
     *
     * @param path the path to the file or directory.
     * @return {@code true} if the file or directory exists, {@code false} otherwise.
     */
    boolean exists(Path path);

    /**
     * Reads all bytes from the file located at the specified path.
     *
     * @param path the path to the file.
     * @return a byte array containing the contents of the file.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    byte[] readAllBytes(Path path) throws IOException;

    /**
     * Creates directories at the specified path,
     * including any necessary but nonexistent parent directories.
     *
     * @param path the path where directories should be created.
     * @throws IOException if an I/O error occurs while creating the directories.
     */
    void createDirectories(Path path) throws IOException;

    /**
     * Writes the given byte array to the file at the specified path.
     * If the file doesn't exist, it will be created.
     *
     * @param path  the path to the file.
     * @param bytes the byte array to write.
     * @throws IOException if an I/O error occurs during writing.
     */
    void write(Path path, byte[] bytes) throws IOException;

    /**
     * Return string Base64 representing the file's content.
     * @param bytes the byte array to encoding
     * @return string Base64 representing the file's content
     */
    String encodingBase64(byte[] bytes);
}
