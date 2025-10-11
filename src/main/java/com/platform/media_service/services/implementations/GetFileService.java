package com.platform.media_service.services.implementations;

import com.platform.media_service.dtos.FileResponseDto;
import com.platform.media_service.entities.ArchiveEntity;
import com.platform.media_service.controllers.manageExceptions.CustomException;
import com.platform.media_service.models.ArchiveModel;
import com.platform.media_service.repositories.ArchiveRepositoryJpa;
import com.platform.media_service.services.IGetFileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for retrieving file data by UUID,
 * checking file integrity, and returning metadata.
 * Interacts with the database to fetch metadata
 * and the file system to read the file.
 */
@Service
public class GetFileService implements IGetFileService {
    /** Logger for logging information, warnings, and errors. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(GetFileService.class);
    /**
     * Service for handling file hash logic (MD5, SHA-256).
     */
    private final HashService hashService;

    /**
     * Repository for accessing archive data stored in the database.
     */
    private final ArchiveRepositoryJpa archiveRepositoryJpa;

    /**
     * Service that provides model mapping between entity classes and DTOs/models.
     * Used to map {@link ArchiveEntity} to {@link ArchiveModel}.
     */
    private final ModelMapper modelMapper;

    /**
     * Service that handles file system operations, such as reading and writing files.
     * This is used to check file existence and read the file's content from disk.
     */
    private final FileSystemService fileSystemService;

    /**
     * Constructor for injecting dependencies.
     *
     * @param hashServiceParam service hashes
     * @param archiveRepositoryJpaParam repository archive from the database
     * @param modelMapperParam the model mapper
     * @param fileSystemServiceParam service file system
     */
    @Autowired
    public GetFileService(HashService hashServiceParam, ArchiveRepositoryJpa archiveRepositoryJpaParam,
                          @Qualifier("modelMapper") ModelMapper modelMapperParam,
                          FileSystemService fileSystemServiceParam) {
        this.hashService = hashServiceParam;
        this.archiveRepositoryJpa = archiveRepositoryJpaParam;
        this.modelMapper = modelMapperParam;
        this.fileSystemService = fileSystemServiceParam;
    }

    /**
     * Retrieves file data by UUID and checks its integrity.
     *
     * @param sUuid the UUID of the file.
     * @return a {@link FileResponseDto}
     * with file data (bytes, MIME type, name, hash).
     * @throws CustomException if the file is not found or an error occurs.
     */
    @Override
    public FileResponseDto getFileById(String sUuid) {
        LOG.trace("getFileById()");
        // Retrieve the archive metadata by UUID
        ArchiveModel archive;
        try {
            archive = getArchiveFileById(sUuid);
        } catch (IllegalArgumentException e) {
            LOG.error(e.getMessage());
            throw new CustomException("Invalid UUID", HttpStatus.BAD_REQUEST, e);
        }
        if (archive == null) {
            LOG.error("Archive with UUID {} not found", sUuid);
            throw new CustomException("File is not registered", HttpStatus.NOT_FOUND);
        }

        FileResponseDto result = new FileResponseDto();
        Path filePath = Paths.get(archive.getPath());

        // Check if the file exists at the specified path
        if (fileSystemService.exists(filePath)) {
            try {
                // Read file data and set the attributes in FileResponseDto
                result.setBytes(fileSystemService.readAllBytes(filePath));
                result.setMimeType(archive.getMimeType());

                // Set the original name or the saved name of the file
                //result.setFileName(filePath.getFileName().toString());
                result.setFileName(archive.getName());
                result.setExtension(archive.getExtension());
                result.setSha256(hashService.getSha256String(result.getBytes()));
                result.setUuid(archive.getId());
            } catch (Exception e) {
                LOG.error("Error reading file at path {}: {}", filePath, e.getMessage());
                // Throw a custom exception if an error occurs while reading the file
                throw new CustomException("An error occurred while retrieving the file", HttpStatus.INTERNAL_SERVER_ERROR, e);
            }
        } else {
            LOG.error("File not found at path {}", filePath);
            // Throw a custom exception if the file is not found
            throw new CustomException("File not found", HttpStatus.NOT_FOUND);
        }

        // Compare the computed hash with the stored hash for integrity verification
        if (!result.getSha256().equals(archive.getHashSha256())) {
            LOG.warn("File integrity conflict for UUID {}: computed hash {} does not match stored hash {}",
                    sUuid, result.getSha256(), archive.getHashSha256());
            throw new CustomException("File integrity conflict", HttpStatus.CONFLICT);
        }
        // Encoding byte arrays to string base64
        result.setBase64(fileSystemService.encodingBase64(result.getBytes()));

        return result;
    }

    /**
     * Retrieves an archive from the database based on its UUID.
     *
     * @param sUuid the UUID of the file to retrieve.
     * @return the corresponding ArchiveModel object if found, otherwise null.
     * @throws IllegalArgumentException if the UUID is not in the correct format.
     */
    private ArchiveModel getArchiveFileById(String sUuid) {
        ArchiveModel result = null;
        UUID uuid;
        try {
            uuid = UUID.fromString(sUuid);
        } catch (IllegalArgumentException e) {
            LOG.warn("UUID no format: {}", sUuid);
            throw new IllegalArgumentException("Invalid UUID", e);
        }
        Optional<ArchiveEntity> archiveEntity = archiveRepositoryJpa.findById(uuid);

        // If the archive is found, map it to an ArchiveModel object
        if (archiveEntity.isPresent()) {
            result = modelMapper.map(archiveEntity.get(), ArchiveModel.class);
        }

        return result;
    }
}
