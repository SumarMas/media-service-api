package com.platform.media_service.services.implementations;

import com.platform.media_service.dtos.FileDto;
import com.platform.media_service.entities.ArchiveEntity;
import com.platform.media_service.controllers.manageExceptions.CustomException;
import com.platform.media_service.models.ArchiveModel;
import com.platform.media_service.repositories.ArchiveRepositoryJpa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveFileServiceTest {

    @InjectMocks
    private SaveFileService saveFileService; // Este ya inyecta el valor de pathFolder automáticamente

    @Mock
    private HashService hashService;

    @Mock
    private ArchiveRepositoryJpa archiveRepositoryJpa;
    @Mock
    private MimeTypeService mimeTypeService;

    @Mock(name = "modelMapper")
    private ModelMapper modelMapper;

    @Mock
    private FileSystemService fileSystemService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testSaveFile_ValidMd5() throws IOException {
        // Arrange
        injectPathFolder();
        FileDto fileDto = mock(FileDto.class);
        MultipartFile mockFile = mock(MultipartFile.class);
        when(fileDto.getFile()).thenReturn(mockFile);
        when(fileDto.getHashMd5()).thenReturn("validMd5Hash");
        when(mockFile.getOriginalFilename()).thenReturn("testfile.txt");
        when(mockFile.getBytes()).thenReturn(new byte[]{});
        when(hashService.checkMd5Hash(mockFile, "validMd5Hash")).thenReturn(true);
        when(hashService.getSha256String(fileDto.getFile())).thenReturn("validSha256Hash");
        when(archiveRepositoryJpa.findByHashSha256(anyString())).thenReturn(Optional.empty());
        when(mimeTypeService.getMimeType(any(File.class))).thenReturn("text/plain");  // Mockear el tipo MIME
        ArchiveModel archiveModel = new ArchiveModel();
        archiveModel.setId(UUID.randomUUID());

        when(modelMapper.map(any(ArchiveModel.class), eq(ArchiveEntity.class))).thenReturn(new ArchiveEntity());

        // Mock fileSystemService to avoid real file system access
        doNothing().when(fileSystemService).write(any(Path.class), any(byte[].class));
        doNothing().when(fileSystemService).createDirectories(any(Path.class));

        // Act
        UUID result = saveFileService.saveFile(fileDto);

        // Assert
        assertNotNull(result);
        verify(archiveRepositoryJpa, times(1)).save(any(ArchiveEntity.class));
        verify(fileSystemService, times(1)).write(any(Path.class), any(byte[].class));  // Verifying the interaction
    }

    @Test
    void testSaveFile_InvalidMd5() throws IOException {
        // Arrange
        FileDto fileDto = mock(FileDto.class);
        MultipartFile mockFile = mock(MultipartFile.class);
        when(fileDto.getFile()).thenReturn(mockFile);
        when(fileDto.getHashMd5()).thenReturn("invalidMd5Hash");
        when(hashService.checkMd5Hash(mockFile, "invalidMd5Hash")).thenReturn(false);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> saveFileService.saveFile(fileDto));
        assertEquals("The integrity of the file is compromised (MD5 mismatch)", exception.getMessage());
    }

    @Test
    void testSaveFile_InvalidSha256() throws IOException {
        // Arrange
        FileDto fileDto = mock(FileDto.class);
        MultipartFile mockFile = mock(MultipartFile.class);
        when(fileDto.getFile()).thenReturn(mockFile);
        when(fileDto.getHashSha256()).thenReturn("invalidSha256");
        when(hashService.checkSha256Hash(mockFile, "invalidSha256")).thenReturn(false);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> saveFileService.saveFile(fileDto));
        assertEquals("The integrity of the file is compromised (SHA-256 mismatch)", exception.getMessage());
    }

    @Test
    void testSaveFile_GenerateArchive_IOExceptionCreatingDirectories() throws IOException {
        injectPathFolder();
        // Arrange
        FileDto fileDto = mock(FileDto.class);
        MultipartFile mockFile = mock(MultipartFile.class);
        when(fileDto.getFile()).thenReturn(mockFile);
        when(mockFile.getOriginalFilename()).thenReturn("testfile.txt");

        doThrow(new IOException("Test IO Exception")).when(fileSystemService).createDirectories(any());

        when(hashService.getSha256String(fileDto.getFile())).thenReturn("validSha256Hash");
        when(archiveRepositoryJpa.findByHashSha256(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> saveFileService.saveFile(fileDto));
        assertEquals("Error while creating directories", exception.getMessage());
    }

    @Test
    void testSaveFile_GenerateArchive_IOExceptionWritingFile() throws IOException {
        injectPathFolder();
        // Arrange
        FileDto fileDto = mock(FileDto.class);
        MultipartFile mockFile = mock(MultipartFile.class);
        when(fileDto.getFile()).thenReturn(mockFile);
        when(mockFile.getOriginalFilename()).thenReturn("testfile.txt");
        when(mockFile.getBytes()).thenReturn(new byte[]{});

        doThrow(new IOException("Test IO Exception")).when(fileSystemService).write(any(), any());
        when(hashService.getSha256String(fileDto.getFile())).thenReturn("validSha256Hash");
        when(archiveRepositoryJpa.findByHashSha256(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> saveFileService.saveFile(fileDto));
        assertEquals("Error while saving the file", exception.getMessage());
    }

    @Test
    void testSaveFile_InvalidPath() {
        // Arrange
        FileDto fileDto = mock(FileDto.class);
        MultipartFile mockFile = mock(MultipartFile.class);
        when(fileDto.getFile()).thenReturn(mockFile);
        when(mockFile.getOriginalFilename()).thenReturn("testfile.txt");

        // Simulamos que el pathFolder es inválido
        saveFileService = new SaveFileService(hashService, archiveRepositoryJpa, modelMapper, fileSystemService,mimeTypeService);
        // Inyectar pathFolder inválido a través de @TestPropertySource o simulación en el contexto

        ArchiveModel archive = new ArchiveModel();

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> saveFileService.saveFile(fileDto));
        assertEquals("Invalid path folder", exception.getMessage());
    }

    private void injectPathFolder(){
        try {
            java.lang.reflect.Field field = SaveFileService.class.getDeclaredField("pathFolder");
            field.setAccessible(true);
            field.set(saveFileService, "test-uploads/"); // Inyecta un valor de prueba
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}