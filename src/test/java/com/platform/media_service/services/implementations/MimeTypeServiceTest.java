package com.platform.media_service.services.implementations;

import com.platform.media_service.services.IMimeTypeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MimeTypeServiceTest {

    private File tempFile;
    private IMimeTypeService mimeTypeService;

    @BeforeEach
    public void setUp() throws IOException {
        mimeTypeService = new MimeTypeService();

        // Crear un archivo temporal
        tempFile = File.createTempFile("testfile", ".txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Contenido de prueba");
        }
    }

    @AfterEach
    public void tearDown() {
        // Eliminar el archivo temporal después de cada prueba
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    public void testGetMimeType_ValidFile() throws IOException {
        // Act
        String mimeType = mimeTypeService.getMimeType(tempFile);

        // Assert
        assertEquals("text/plain", mimeType);  // Comprobar el tipo MIME
    }

    @Test
    public void testGetMimeType_IOException() {
        // Eliminar el archivo antes de intentar obtener el tipo MIME para forzar un error
        if (tempFile.exists()) {
            tempFile.delete();
        }

        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> {
            mimeTypeService.getMimeType(tempFile);
        });

        assertEquals("Error while detecting MIME type", exception.getMessage());
    }
}
