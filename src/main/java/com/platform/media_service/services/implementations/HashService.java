package com.platform.media_service.services.implementations;

import com.platform.media_service.services.IHashService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Service for generating and verifying MD5 and SHA-256 hashes of files.
 * Provides methods to hash files and compare
 * them with provided values for integrity checks.
 */
@Service
public class HashService implements IHashService {
    /** Logger for logging information, warnings, and errors. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(HashService.class);

    /**
     * Generates the MD5 hash of a given file.
     *
     * @param file the MultipartFile to hash
     * @return the MD5 hash of the file as a String
     * @throws RuntimeException if there is an error reading the file
     */
    @Override
    public String getMd5String(MultipartFile file) throws IOException {
        try {
            // Generate the MD5 hash of the file
            return DigestUtils.md5Hex(file.getBytes());
        } catch (IOException e) {
            LOG.error("Error generating MD5 hash: {}", e.getMessage());
            throw new IOException("Error processing file for MD5 hash", e);
        }
    }

    /**
     * Generates the SHA-256 hash of a given file.
     *
     * @param file the MultipartFile to hash
     * @return the SHA-256 hash of the file as a String
     * @throws RuntimeException if there is an error reading the file
     */
    @Override
    public String getSha256String(MultipartFile file) throws IOException {
        try {
            // Generate the SHA-256 hash of the file
            return DigestUtils.sha256Hex(file.getBytes());
        } catch (IOException e) {
            LOG.error("Error generating SHA-256 hash: {}", e.getMessage());
            throw new IOException("Error processing file for SHA-256 hash", e);
        }
    }

    /**
     * Compares the provided MD5 hash with the calculated hash of the given file.
     *
     * @param file the MultipartFile to hash and compare
     * @param hash the MD5 hash to compare against
     * @return true if the hashes match, false otherwise
     * @throws RuntimeException if there is an error reading the file
     */
    @Override
    public Boolean checkMd5Hash(MultipartFile file, String hash) throws IOException {
        try {
            // Compare the provided hash with the calculated MD5 hash of the file
            String calculatedHash = DigestUtils.md5Hex(file.getBytes());
            return calculatedHash.equals(hash);
        } catch (IOException e) {
            LOG.error("Error checking MD5 hash: {}", e.getMessage());
            throw new IOException("Error checking MD5 hash", e);
        }
    }

    /**
     * Compares the provided SHA-256 hash with the calculated hash of the given file.
     *
     * @param file the MultipartFile to hash and compare
     * @param hash the SHA-256 hash to compare against
     * @return true if the hashes match, false otherwise
     * @throws RuntimeException if there is an error reading the file
     */
    @Override
    public Boolean checkSha256Hash(MultipartFile file, String hash) throws IOException {
        try {
            // Compare the provided hash with the calculated SHA-256 hash of the file
            String calculatedHash = DigestUtils.sha256Hex(file.getBytes());
            return calculatedHash.equals(hash);
        } catch (IOException e) {
            LOG.error("Error checking SHA-256 hash: {}", e.getMessage());
            throw new IOException("Error checking SHA-256 hash", e);
        }
    }

    /**
     * Generates a SHA-256 hash from the provided byte array.
     *
     * @param file the byte array representing the file to be hashed.
     * @return the SHA-256 hash as a hexadecimal string.
     */
    @Override
    public String getSha256String(byte[] file) throws NoSuchAlgorithmException {
        try {
            // Ensure the file byte array is not null
            if (file == null) {
                LOG.warn("File is null");
                throw new IllegalArgumentException("The file byte array cannot be null");
            }

            // Create a MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(file); // Calculate the SHA-256 hash

            // Convert the byte array to a hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            // Handle the case when the SHA-256 algorithm is not available
            LOG.error("Error SHA-256 algorithm not available: {}", e.getMessage());
            throw new NoSuchAlgorithmException("SHA-256 algorithm not available", e);

        }   catch (IllegalArgumentException e) {
            // Handle the case when the byte array is null or invalid
            LOG.error("Invalid file byte array: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid file byte array", e);
        }
    }


}
