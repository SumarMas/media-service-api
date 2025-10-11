package com.platform.media_service.services.implementations;

import com.platform.media_service.services.IHashService;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class HashServiceTest {

    private IHashService hashService;
    private MultipartFile mockFile;
    private final String sampleMd5Hash = DigestUtils.md5Hex("test-content");
    private final String sampleSha256Hash = DigestUtils.sha256Hex("test-content");

    @BeforeEach
    public void setup() {
        hashService = new HashService();
        mockFile = Mockito.mock(MultipartFile.class);
    }

    @Test
    public void testGetMd5String_Success() throws Exception {
        // Mock the file content
        when(mockFile.getBytes()).thenReturn("test-content".getBytes());

        // Call the method
        String result = hashService.getMd5String(mockFile);

        // Validate the result
        assertEquals(sampleMd5Hash, result);
    }

    @Test
    public void testGetMd5String_FileReadError() throws Exception {
        // Mock an IOException
        when(mockFile.getBytes()).thenThrow(new IOException("Test IOException"));

        // Validate the exception
        IOException exception = assertThrows(IOException.class, () -> hashService.getMd5String(mockFile));
        assertEquals("Error processing file for MD5 hash", exception.getMessage());
    }

    @Test
    public void testGetSha256String_Success() throws Exception {
        // Mock the file content
        when(mockFile.getBytes()).thenReturn("test-content".getBytes());

        // Call the method
        String result = hashService.getSha256String(mockFile);

        // Validate the result
        assertEquals(sampleSha256Hash, result);
    }

    @Test
    public void testGetSha256String_FileReadError() throws Exception {
        // Mock an IOException
        when(mockFile.getBytes()).thenThrow(new IOException("Test IOException"));

        // Validate the exception
        IOException exception = assertThrows(IOException.class, () -> hashService.getSha256String(mockFile));
        assertEquals("Error processing file for SHA-256 hash", exception.getMessage());
    }

    @Test
    public void testCheckMd5Hash_Success() throws Exception {
        // Mock the file content
        when(mockFile.getBytes()).thenReturn("test-content".getBytes());

        // Call the method
        Boolean result = hashService.checkMd5Hash(mockFile, sampleMd5Hash);

        // Validate the result
        assertTrue(result);
    }

    @Test
    public void testCheckMd5Hash_Failure() throws Exception {
        // Mock the file content
        when(mockFile.getBytes()).thenReturn("different-content".getBytes());

        // Call the method
        Boolean result = hashService.checkMd5Hash(mockFile, sampleMd5Hash);

        // Validate the result
        assertFalse(result);
    }

    @Test
    public void testCheckSha256Hash_Success() throws Exception {
        // Mock the file content
        when(mockFile.getBytes()).thenReturn("test-content".getBytes());

        // Call the method
        Boolean result = hashService.checkSha256Hash(mockFile, sampleSha256Hash);

        // Validate the result
        assertTrue(result);
    }

    @Test
    public void testCheckSha256Hash_Failure() throws Exception {
        // Mock the file content
        when(mockFile.getBytes()).thenReturn("different-content".getBytes());

        // Call the method
        Boolean result = hashService.checkSha256Hash(mockFile, sampleSha256Hash);

        // Validate the result
        assertFalse(result);
    }

    @Test
    public void testGetSha256StringFromByteArray_Success() throws NoSuchAlgorithmException {
        byte[] fileBytes = "test-content".getBytes();

        // Call the method
        String result = hashService.getSha256String(fileBytes);

        // Validate the result
        assertEquals(sampleSha256Hash, result);
    }

    @Test
    public void testGetSha256StringFromByteArray_NullByteArray() {
        // Validate the exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> hashService.getSha256String((byte[]) null));
        assertEquals("Invalid file byte array", exception.getMessage());
    }
}