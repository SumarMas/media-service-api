package com.platform.media_service.services.implementations;


import com.platform.media_service.services.IFileSystemService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

/**
 * Service class for file system operations, such as checking existence, reading,
 * creating directories, and writing files.
 */
@Service
public class FileSystemService implements IFileSystemService {
    /**
     * Checks if a file or directory exists at the specified path.
     *
     * @param path The path to the file or directory.
     * @return {@code true} if the file or directory exists, {@code false} otherwise.
     */
    @Override
    public boolean exists(Path path) {
        return Files.exists(path);
    }

    /**
     * Reads all bytes from a file at the specified path.
     *
     * @param path The path to the file.
     * @return A byte array containing the contents of the file.
     * @throws IOException if an I/O error occurs reading from the file.
     */
    @Override
    public byte[] readAllBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    /**
     * Creates directories at the specified path,
     * including any necessary but nonexistent parent directories.
     *
     * @param path The path where directories should be created.
     * @throws IOException if an I/O error occurs
     * or the directories cannot be created.
     */
    @Override
    public void createDirectories(Path path) throws IOException {
        Files.createDirectories(path);
    }

    /**
     * Writes data to the file at the given path.
     *
     * @param path The path to the file.
     * @param bytes The data to write.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void write(Path path, byte[] bytes) throws IOException {
        Files.write(path, bytes);
    }
    /**
     * Return string Base64 representing the file's content.
     * @param bytes the byte array to encoding
     * @return string Base64 representing the file's content
     */
    @Override
    public String encodingBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
