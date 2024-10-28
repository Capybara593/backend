package com.example.demo.controller;

import com.example.demo.service.EmailService;
import com.example.demo.service.MinIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private MinIOService minIOService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private EmailService emailService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId) {
        String result = minIOService.uploadFile(file, userId);

        // Tạo một thông điệp JSON để gửi
        Map<String, String> message = new HashMap<>();
        message.put("action", "upload");
        message.put("fileName", file.getOriginalFilename());

        messagingTemplate.convertAndSend("/topic/files", message);
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

        // Tạo một thông điệp JSON để gửi
        Map<String, String> message = new HashMap<>();
        message.put("action", "delete");
        message.put("fileName", objectName);

        messagingTemplate.convertAndSend("/topic/files", message);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/share/link")
    public ResponseEntity<String> shareFileLink(
            @RequestParam("userId") String userId,
            @RequestParam("fileName") String fileName,
            @RequestParam("permission") String permission) {
        String token = minIOService.createShareableLink(userId, fileName, permission);
        return ResponseEntity.ok("https://your-domain.com/api/file/access/" + token);
    }

    @PostMapping("/share/email")
    public ResponseEntity<String> shareFileViaEmail(
            @RequestParam("toEmail") String toEmail,
            @RequestParam("subject") String subject,
            @RequestParam("body") String body,
            @RequestParam("userId") String userId,
            @RequestParam("fileName") String fileName) {
        String result = emailService.sendEmailWithAttachment(toEmail, subject, body, userId, fileName);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/access/{token}")
    public ResponseEntity<byte[]> accessSharedFile(@PathVariable String token) {
        byte[] fileContent = minIOService.accessSharedFile(token);
        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(fileContent);
    }
}
