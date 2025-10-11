package com.platform.media_service.services.implementations;

import com.platform.media_service.dtos.FileDto;
import com.platform.media_service.entities.ArchiveEntity;
import com.platform.media_service.controllers.manageExceptions.CustomException;
import com.platform.media_service.models.ArchiveModel;
import com.platform.media_service.repositories.ArchiveRepositoryJpa;
import com.platform.media_service.services.IFileSystemService;
import com.platform.media_service.services.IHashService;
import com.platform.media_service.services.IMimeTypeService;
import com.platform.media_service.services.ISaveFileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for handling file saving logic,
 * including file integrity checks via hash functions (MD5, SHA-256).
 */
@Service
public class SaveFileService implements ISaveFileService {
    /** Logger for logging information, warnings, and errors. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SaveFileService.class);

    /**
     * Service responsible for file hash generation and validation.
     */
    private final IHashService hashService;

    /**
     * Repository for accessing and storing archive metadata in the database.
     */
    private final ArchiveRepositoryJpa archiveRepositoryJpa;

    /**
     * ModelMapper used to map between entity classes and model/DTO objects.
     */
    private final ModelMapper modelMapper;

    /**
     * Service responsible for file system operations such as writing
     * files and creating directories.
     */
    private final IFileSystemService fileSystemService;

    /**
     * Service responsible for determining the MIME type of file.
     */
    private final IMimeTypeService mimeTypeService;

    /**
     * Path to the folder where files are saved,
     * retrieved from the application's configuration.
     */
    @Value("${app.file-storage-path}")
    private String pathFolder;

    /**
     * Injects dependencies.
     *
     * @param hashServiceParam service for hashing files
     * @param archiveRepositoryJpaParam repository for archive metadata
     * @param modelMapperParam maps entities and models
     * @param fileSystemServiceParam service for file operations
     * @param mimeTypeServiceParam service for detecting MIME types
     */
    @Autowired
    public SaveFileService(IHashService hashServiceParam,
                           ArchiveRepositoryJpa archiveRepositoryJpaParam,
                           @Qualifier("modelMapper") ModelMapper modelMapperParam,
                           IFileSystemService fileSystemServiceParam,
                           IMimeTypeService mimeTypeServiceParam) {
        this.hashService = hashServiceParam;
        this.archiveRepositoryJpa = archiveRepositoryJpaParam;
        this.modelMapper = modelMapperParam;
        this.fileSystemService = fileSystemServiceParam;
        this.mimeTypeService = mimeTypeServiceParam;
    }

    /**
     * Saves a file and verifies its integrity using optional hashes.
     *
     * @param fileDto the file data with optional hashes
     * @return {@link UUID} of the saved or existing file
     * @throws CustomException if an error occurs
     */
    @Override
    public UUID saveFile(FileDto fileDto) {
        LOG.trace("saveFile()");
        // If it has MD5 check it
        if (fileDto.getHashMd5() != null && !fileDto.getHashMd5().isEmpty() && !checkHashMd5(fileDto.getHashMd5(), fileDto.getFile())) {
            LOG.error("Integrity check failed: MD5 mismatch");
            throw new CustomException("The integrity of the file is compromised (MD5 mismatch)", HttpStatus.CONFLICT);
        }
        // If it has SHA256 check it
        if (fileDto.getHashSha256() != null && !fileDto.getHashSha256().isEmpty()
                && !checkHashSha256(fileDto.getHashSha256(), fileDto.getFile())) {
            LOG.error("Integrity check failed: SHA-256 mismatch");
            throw new CustomException("The integrity of the file is compromised (SHA-256 mismatch)", HttpStatus.CONFLICT);
        }

        ArchiveModel archive;
        // If the file exists, retrieve it, otherwise create a new archive
        if (fileDto.getHashSha256() != null) {
            archive = getFileByHash(fileDto.getHashSha256());
        } else {
            try {
                archive = getFileByHash(hashService.getSha256String(fileDto.getFile()));
            } catch (IOException e) {
                LOG.error(e.getMessage());
                throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, e);
            }
        }


        if (archive == null) {
            archive = new ArchiveModel();
            generateArchive(fileDto, archive);
            // Map the Archive object to ArchiveEntity and save it in the database
            ArchiveEntity archiveEntity = modelMapper.map(archive, ArchiveEntity.class);
            archiveRepositoryJpa.save(archiveEntity);
        }

        return archive.getId();
    }

    /**
     * Generates an Archive object,
     * sets its metadata (name, hash, path), and saves the file.
     *
     * @param fileDto the file data transfer object
     * @param archive the Archive entity to populate
     */
    private void generateArchive(FileDto fileDto, ArchiveModel archive) {
        archive.setId(UUID.randomUUID());
        archive.setName(fileDto.getFile().getOriginalFilename());
        archive.setExtension(archive.getName().substring(archive.getName().lastIndexOf(".") + 1));
        try {
            archive.setHashSha256(hashService.getSha256String(fileDto.getFile()));
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
        if (pathFolder == null || pathFolder.isEmpty()) {
            LOG.warn("Empty path folder");
            throw new CustomException("Invalid path folder", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String filePath = pathFolder + archive.getId().toString() + "." + archive.getExtension();
        archive.setPath(filePath);
        Path path = Paths.get(filePath);
        try {
            fileSystemService.createDirectories(path.getParent());
        } catch (IOException e) {
            LOG.error("Error creating directories: {}", e.getMessage());
            throw new CustomException("Error while creating directories", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
        archive.setCreationDate(LocalDateTime.now());

        // Save the file to the specified path
        try {
            fileSystemService.write(path, fileDto.getFile().getBytes());
        } catch (Exception e) {
            LOG.error("Error saving file: {}", e.getMessage());
            throw new CustomException("Error while saving the file", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }

        // Set the MIME type of the file
        try {
            archive.setMimeType(mimeTypeService.getMimeType(path.toFile()));
        } catch (IOException e) {
            LOG.error("Error determining MIME type: {}", e.getMessage());
            throw new CustomException("Error while saving the file", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    /**
     * Verifies if the MD5 hash of the file matches the provided hash.
     *
     * @param hashMd5 the expected MD5 hash
     * @param file    the file to check
     * @return true if the hashes match, false otherwise
     */
    private Boolean checkHashMd5(String hashMd5, MultipartFile file) {
        try {
            return hashService.checkMd5Hash(file, hashMd5);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    /**
     * Verifies if the SHA-256 hash of the file matches the provided hash.
     *
     * @param hashSha256 the expected SHA-256 hash
     * @param file       the file to check
     * @return true if the hashes match, false otherwise
     */
    private Boolean checkHashSha256(String hashSha256, MultipartFile file) {
        try {
            return hashService.checkSha256Hash(file, hashSha256);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    /**
     * Retrieves an archive from the database based on its SHA-256 hash.
     *
     * @param hash the SHA-256 hash of the file to retrieve.
     * @return the corresponding Archive object if found, otherwise null.
     */
    private ArchiveModel getFileByHash(String hash) {
        ArchiveModel result = null;
        Optional<ArchiveEntity> archiveOptional = archiveRepositoryJpa.findByHashSha256(hash);

        // If the archive is found in the database, map it to Archive object
        if (archiveOptional.isPresent()) {
            result = modelMapper.map(archiveOptional.get(), ArchiveModel.class);
        }

        return result;
    }
}
