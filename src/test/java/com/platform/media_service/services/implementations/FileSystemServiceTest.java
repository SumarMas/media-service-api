package com.platform.media_service.services.implementations;

import com.platform.media_service.services.IFileSystemService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class FileSystemServiceTest {
    private final IFileSystemService fileSystemService = new FileSystemService();

    private final Path testFilePath = Paths.get("src/test/resources/testFile.txt");
    private final Path nonExistentFilePath = Paths.get("src/test/resources/nonExistentFile.txt");
    private final Path testDirectoryPath = Paths.get("src/test/resources/testDirectory");

    @BeforeEach
    public void setup() throws IOException {
        Files.createDirectories(testFilePath.getParent()); // Asegurarse de que el directorio exista
        Files.write(testFilePath, "contenido de prueba".getBytes());
    }

    @AfterEach
    public void cleanup() throws IOException {
        Files.deleteIfExists(testFilePath);
        Files.deleteIfExists(nonExistentFilePath);
        Files.deleteIfExists(testDirectoryPath);
    }

    @Test
    public void testExists_FileExists() {
        assertTrue(fileSystemService.exists(testFilePath));
    }

    @Test
    public void testExists_FileDoesNotExist() {
        assertFalse(fileSystemService.exists(nonExistentFilePath));
    }

    @Test
    public void testReadAllBytes_FileExists() throws Exception {
        byte[] fileBytes = fileSystemService.readAllBytes(testFilePath);
        assertNotNull(fileBytes);
        assertTrue(fileBytes.length > 0);
    }

    @Test
    public void testReadAllBytes_FileDoesNotExist() {
        assertThrows(IOException.class, () -> fileSystemService.readAllBytes(nonExistentFilePath));
    }

    @Test
    public void testCreateDirectories_Success() throws IOException {
        fileSystemService.createDirectories(testDirectoryPath);

        assertTrue(Files.exists(testDirectoryPath));
        assertTrue(Files.isDirectory(testDirectoryPath));
    }

    @Test
    public void testWrite_Success() throws IOException {
        Path newFilePath = Paths.get("src/test/resources/testWriteFile.txt");
        byte[] data = "nuevo contenido".getBytes();

        fileSystemService.write(newFilePath, data);

        assertTrue(Files.exists(newFilePath));
        assertArrayEquals(data, Files.readAllBytes(newFilePath));

        // Limpiar después de la prueba
        Files.deleteIfExists(newFilePath);
    }

    @Test
    public void testWrite_Failure_InvalidPath() {
        Path invalidPath = Paths.get("/invalid/path/testFile.txt");

        assertThrows(IOException.class, () -> fileSystemService.write(invalidPath, "data".getBytes()));
    }
}
