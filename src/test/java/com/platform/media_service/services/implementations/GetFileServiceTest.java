package com.platform.media_service.services.implementations;

import com.platform.media_service.dtos.FileResponseDto;
import com.platform.media_service.entities.ArchiveEntity;
import com.platform.media_service.controllers.manageExceptions.CustomException;
import com.platform.media_service.models.ArchiveModel;
import com.platform.media_service.repositories.ArchiveRepositoryJpa;
import com.platform.media_service.services.IFileSystemService;
import com.platform.media_service.services.IGetFileService;
import com.platform.media_service.services.IHashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetFileServiceTest {

    @Mock
    private HashService hashService;

    @Mock
    private ArchiveRepositoryJpa archiveRepositoryJpa;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private FileSystemService fileSystemService;

    @InjectMocks
    private GetFileService getFileService;

    private ArchiveEntity archiveEntity;
    private ArchiveModel archiveModel;
    private final String suuid = UUID.randomUUID().toString();
    private final Path filePath = Paths.get("test/path/to/file");

    @BeforeEach
    public void setup() {

        archiveEntity = new ArchiveEntity();
        archiveEntity.setId(UUID.fromString(suuid));
        archiveEntity.setPath(filePath.toString());
        archiveEntity.setHashSha256("validHash");

        archiveModel = new ArchiveModel();
        archiveModel.setId(UUID.fromString(suuid));
        archiveModel.setPath(filePath.toString());
        archiveModel.setMimeType("application/pdf");
        archiveModel.setName("testFile.pdf");
        archiveModel.setExtension(".pdf");
        archiveModel.setHashSha256("validHash");
    }

    @Test
    public void testGetFileById_Success() throws Exception {

        when(archiveRepositoryJpa.findById(any(UUID.class))).thenReturn(Optional.of(archiveEntity));
        when(modelMapper.map(any(ArchiveEntity.class), eq(ArchiveModel.class))).thenReturn(archiveModel);
        when(fileSystemService.exists(filePath)).thenReturn(true);
        when(fileSystemService.readAllBytes(filePath)).thenReturn(new byte[]{1, 2, 3});
        when(hashService.getSha256String(any(byte[].class))).thenReturn("validHash");


        FileResponseDto response = getFileService.getFileById(suuid);


        assertNotNull(response);
        assertEquals("testFile.pdf", response.getFileName());
        assertEquals("application/pdf", response.getMimeType());
        assertEquals(suuid, response.getUuid().toString());
        assertArrayEquals(new byte[]{1, 2, 3}, response.getBytes());
    }

    @Test
    public void testGetFileById_FileNotRegistered() {

        when(archiveRepositoryJpa.findById(any(UUID.class))).thenReturn(Optional.empty());


        CustomException exception = assertThrows(CustomException.class, () -> getFileService.getFileById(suuid));
        assertEquals("File is not registered", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void testGetFileById_FileNotFoundOnDisk() {

        when(archiveRepositoryJpa.findById(any(UUID.class))).thenReturn(Optional.of(archiveEntity));
        when(modelMapper.map(any(ArchiveEntity.class), eq(ArchiveModel.class))).thenReturn(archiveModel);
        when(fileSystemService.exists(filePath)).thenReturn(false);


        CustomException exception = assertThrows(CustomException.class, () -> getFileService.getFileById(suuid));
        assertEquals("File not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void testGetFileById_UuidIsInvalidFormat(){
        CustomException exception = assertThrows(CustomException.class, () -> getFileService.getFileById("invalidUuid"));
        assertEquals("Invalid UUID", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }


    @Test
    public void testGetFileById_FileIntegrityConflict() throws Exception {

        when(archiveRepositoryJpa.findById(any(UUID.class))).thenReturn(Optional.of(archiveEntity));
        when(modelMapper.map(any(ArchiveEntity.class), eq(ArchiveModel.class))).thenReturn(archiveModel);
        when(fileSystemService.exists(filePath)).thenReturn(true);
        when(fileSystemService.readAllBytes(filePath)).thenReturn(new byte[]{1, 2, 3});
        when(hashService.getSha256String(any(byte[].class))).thenReturn("invalidHash");


        CustomException exception = assertThrows(CustomException.class, () -> getFileService.getFileById(suuid));
        assertEquals("File integrity conflict", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    public void testGetFileById_FileReadError() throws Exception {

        when(archiveRepositoryJpa.findById(any(UUID.class))).thenReturn(Optional.of(archiveEntity));
        when(modelMapper.map(any(ArchiveEntity.class), eq(ArchiveModel.class))).thenReturn(archiveModel);
        when(fileSystemService.exists(filePath)).thenReturn(true);
        when(fileSystemService.readAllBytes(filePath)).thenThrow(new RuntimeException("File read error"));


        CustomException exception = assertThrows(CustomException.class, () -> getFileService.getFileById(suuid));
        assertEquals("An error occurred while retrieving the file", exception.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
    }
}
