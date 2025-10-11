package com.platform.media_service.controllers;

import com.platform.media_service.dtos.FileDto;
import com.platform.media_service.dtos.FileResponseDto;
import com.platform.media_service.services.IGetFileService;
import com.platform.media_service.services.ISaveFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FileControllerTest {
    private final String BASE_URL = "/api/v1/media";

    private MockMvc mockMvc;

    @Mock
    private ISaveFileService saveFileService;

    @Mock
    private IGetFileService getFileService;

    @InjectMocks
    private FileController fileController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();
    }

    @Test
    public void testGetFile_Success() throws Exception {
        // Arrange
        FileResponseDto fileResponseDto = new FileResponseDto();
        UUID randomUUID = UUID.randomUUID();
        String uuidString = randomUUID.toString();
        fileResponseDto.setUuid(randomUUID);
        fileResponseDto.setFileName("testfile.txt");
        fileResponseDto.setMimeType("application/octet-stream");
        fileResponseDto.setSha256("dummy-sha256-hash");
        fileResponseDto.setExtension("txt");
        fileResponseDto.setBytes("Test file content".getBytes()); // Simulación del contenido del archivo

        when(getFileService.getFileById(any(String.class))).thenReturn(fileResponseDto);

        // Act & Assert
        mockMvc.perform(get(BASE_URL+"/getFile/{uuid}", uuidString)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk())
                .andExpect(header().string("uuid", randomUUID.toString()))
                .andExpect(header().string("fileName", "testfile.txt"))
                .andExpect(header().string("mimeType", "application/octet-stream"))
                .andExpect(header().string("extension", "txt"))
                .andExpect(header().string("sha256", "dummy-sha256-hash"))
                .andExpect(content().bytes("Test file content".getBytes())); // Verificación del contenido
    }

    @Test
    public void testSaveFile_Success() throws Exception {
        // Arrange
        UUID randomUUID = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "testfile.txt", "text/plain", "Contenido de prueba".getBytes());
        when(saveFileService.saveFile(any(FileDto.class))).thenReturn(randomUUID);

        // Act & Assert
        mockMvc.perform(multipart(BASE_URL+"/savefile")
                        .file(file)
                        .part(new MockPart("hashMd5", "dummyMd5Hash".getBytes()))  // Usar MockPart para @RequestPart
                        .part(new MockPart("hashSha256", "dummySha256Hash".getBytes()))  // Usar MockPart para @RequestPart
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(randomUUID.toString()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    public void testSaveFileWhenHashNulls_Success() throws Exception {
        // Arrange
        UUID randomUUID = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "testfile.txt", "text/plain", "Contenido de prueba".getBytes());
        when(saveFileService.saveFile(any(FileDto.class))).thenReturn(randomUUID);

        // Act & Assert
        mockMvc.perform(multipart(BASE_URL+"/savefile")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(randomUUID.toString()));
    }

    @Test
    public void testSaveFile_BadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(multipart(BASE_URL+"/savefile")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }
}