package com.platform.media_service.controllers;

import com.platform.media_service.dtos.FileDto;
import com.platform.media_service.dtos.FileResponseDto;
import com.platform.media_service.dtos.UuidResponseDto;
import com.platform.media_service.dtos.common.ErrorApi;
import com.platform.media_service.services.IGetFileService;
import com.platform.media_service.services.ISaveFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller class for managing
 * file operations such as saving files
 * and retrieving file details.
 * This class provides RESTFUL endpoints
 * for uploading files
 * and fetching file metadata based on UUID.
 */
@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class FileController {
    /**
     * Service that handles the file saving logic.
     */
    private final ISaveFileService saveFileService;
    /**
     * Service that handles the file retrieval logic.
     */
    private final IGetFileService getFileService;

    /**
     * Retrieves the file as a byte array by UUID and returns it in the response,
     * along with file metadata in the headers.
     * The file is returned as "application/octet-stream".
     *
     * @param uuid the unique identifier (UUID) of the file.
     * @return the file data as bytes and metadata in headers.
     */
    @Operation(
            summary = "Retrieve file by UUID",
            description = "Fetches the file as a byte array using its UUID and returns metadata in the headers."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "File retrieved successfully as a byte array",
                    content = @Content(schema = @Schema(type = "string", format = "binary"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "File not found or not registered",
                    content = @Content(schema = @Schema(implementation = ErrorApi.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "File integrity conflict",
                    content = @Content(schema = @Schema(implementation = ErrorApi.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorApi.class))
            )
    })
    @SuppressWarnings("PMD.LooseCoupling")
    @GetMapping("/getFile/{uuid}")
    public ResponseEntity<byte[]> getFile(@PathVariable String uuid) {
        FileResponseDto responseDto = getFileService.getFileById(uuid);
        byte[] fileData = responseDto.getBytes();

        // Set the metadata as custom headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add("uuid", responseDto.getUuid().toString());
        headers.add("sha256", responseDto.getSha256());
        headers.add("fileName", responseDto.getFileName());
        headers.add("mimeType", responseDto.getMimeType());
        headers.add("extension", responseDto.getExtension());

        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
    }
    /**
     * Retrieves file data by UUID, returning it in Base64
     * format and metadata within a DTO.
     *
     * @param uuid the unique identifier of the file.
     * @return ResponseEntity containing the file data in Base64 and metadata.
     */
    @Operation(
            summary = "Retrieve file in Base64 by UUID",
            description = "Fetches file data using its UUID and returns "
                    + "it encoded in Base64 and metadata within a DTO."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "File retrieved successfully as a byte array",
                    content = @Content(schema = @Schema(type = "string", format = "binary"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "File not found or not registered",
                    content = @Content(schema = @Schema(implementation = ErrorApi.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "File integrity conflict",
                    content = @Content(schema = @Schema(implementation = ErrorApi.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorApi.class))
            )
    })
    @GetMapping("/getFileBase64/{uuid}")
    public ResponseEntity<FileResponseDto> getFileBase64(@PathVariable String uuid) {
        FileResponseDto responseDto = getFileService.getFileById(uuid);
        responseDto.setBytes(null);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * Uploads a file.
     *
     * @param file the file to upload
     * @param hashMd5 optional MD5 hash for verification
     * @param hashSha256 optional SHA-256 hash for verification
     * @return a {@link ResponseEntity} containing the
     * {@link UuidResponseDto} with the UUID of the saved file
     */
    @Operation(
            summary = "Upload a file and optionally verify its integrity with hashes",
            description = "This endpoint allows you to upload a file. Optionally,"
                    + " you can provide MD5 or SHA-256 hashes for integrity checks."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "File uploaded successfully",
                    content = @Content(schema = @Schema(implementation = UuidResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request due to invalid input "
                            + "(e.g., missing file, invalid hash format, or invalid storage path)",
                    content = @Content(schema = @Schema(implementation = ErrorApi.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found: File not found for the provided hash",
                    content = @Content(schema = @Schema(implementation = ErrorApi.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict: File integrity compromised (e.g., hash mismatch for MD5 or SHA-256)",
                    content = @Content(schema = @Schema(implementation = ErrorApi.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error due to unexpected issues "
                            + "(e.g., file system error, MIME type detection failure, or database interaction error)",
                    content = @Content(schema = @Schema(implementation = ErrorApi.class))
            )
    })
    @PostMapping(value = "/savefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UuidResponseDto> saveFile(@RequestPart("file") MultipartFile file,
                                                    @RequestPart(value = "hashMd5", required = false) String hashMd5,
                                                    @RequestPart(value = "hashSha256", required = false) String hashSha256) {
        UuidResponseDto responseDto = new UuidResponseDto();
        FileDto fileDto = new FileDto();
        fileDto.setFile(file);
        if (hashMd5 != null) {
            fileDto.setHashMd5(hashMd5);
        }
        if (hashSha256 != null) {
            fileDto.setHashSha256(hashSha256);
        }
        responseDto.setUuid(saveFileService.saveFile(fileDto));
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
