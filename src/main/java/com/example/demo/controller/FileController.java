package com.example.demo.controller;

import com.example.demo.service.MinIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private MinIOService minIOService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId) {
        String result = minIOService.uploadFile(file, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/files/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FileMetadataDTO>> getFilesFromMinIO(@PathVariable("userId") String userId) {
        List<FileMetadataDTO> fileDTOs = minIOService.listFilesFromMinIO(userId);
        return ResponseEntity.ok(fileDTOs);
    }

    @GetMapping("/download/{userId}/{objectName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String userId, @PathVariable String objectName) {
        byte[] content = minIOService.downloadFile(userId, objectName);
        if (content == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + objectName + "\"")
                .body(content);
    }

    @DeleteMapping("/delete/{userId}/{objectName}")
    public ResponseEntity<String> deleteFile(@PathVariable String userId, @PathVariable String objectName) {
        String result = minIOService.deleteFile(userId, objectName);
        return ResponseEntity.ok(result);
    }
}
