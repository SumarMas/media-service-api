package com.platform.media_service.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Interface for a service that handles hashing and hash validation for files.
 * Provides methods to generate and compare MD5
 * and SHA-256 hashes for file integrity checks.
 */
public interface IHashService {
    /**
     * Generates the MD5 hash of the given file.
     *
     * @param file the {@link MultipartFile} to hash.
     * @return the MD5 hash of the file as a string.
     * @throws IOException if an error occurs while reading the file.
     */
    String getMd5String(MultipartFile file) throws IOException;

    /**
     * Generates the SHA-256 hash of the given file.
     *
     * @param file the {@link MultipartFile} to hash.
     * @return the SHA-256 hash of the file as a string.
     * @throws IOException if an error occurs while reading the file.
     */
    String getSha256String(MultipartFile file) throws IOException;

    /**
     * Compares the provided MD5 hash with the calculated hash of the given file.
     *
     * @param file the {@link MultipartFile} to hash and compare.
     * @param hash the expected MD5 hash to compare against.
     * @return {@code true} if the hashes match, {@code false} otherwise.
     * @throws IOException if an error occurs while reading the file.
     */
    Boolean checkMd5Hash(MultipartFile file, String hash) throws IOException;

    /**
     * Compares the provided SHA-256 hash with the calculated hash of the given file.
     *
     * @param file the {@link MultipartFile} to hash and compare.
     * @param hash the expected SHA-256 hash to compare against.
     * @return {@code true} if the hashes match, {@code false} otherwise.
     * @throws IOException if an error occurs while reading the file.
     */
    Boolean checkSha256Hash(MultipartFile file, String hash) throws IOException;

    /**
     * Generates a SHA-256 hash from the provided byte array.
     *
     * @param file the byte array representing the file to be hashed.
     * @return the SHA-256 hash as a hexadecimal string.
     * @throws NoSuchAlgorithmException if the SHA-256 algorithm is not available.
     */
    String getSha256String(byte[] file) throws NoSuchAlgorithmException;

}
