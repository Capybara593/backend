package com.example.demo.service;

import com.example.demo.controller.FileMetadataDTO;
import com.example.demo.model.FileShareMetadata;
import io.minio.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MinIOService {
    private final MinioClient minioClient;
    private final TokenService tokenService;

    @Autowired
    public MinIOService(
            @Value("${minio.url}") String url,
            @Value("${minio.accessKey}") String accessKey,
            @Value("${minio.secretKey}") String secretKey,
            TokenService tokenService) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
        this.tokenService = tokenService;
    }

    public String uploadFile(MultipartFile file, String userId) {
        String bucketName = "bucket-" + userId;
        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            String objectName = userId + "_" + file.getOriginalFilename().replaceAll("[^\\x20-\\x7E]", "_");
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            return "File uploaded successfully: " + objectName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to upload file: " + e.getMessage();
        }
    }

    public List<FileMetadataDTO> listFilesFromMinIO(String userId) {
        String bucketName = "bucket-" + userId;
        List<FileMetadataDTO> fileList = new ArrayList<>();
        try {
            Iterable<Result<Item>> objects = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
            for (Result<Item> result : objects) {
                Item item = result.get();
                FileMetadataDTO fileMetadataDTO = new FileMetadataDTO();
                fileMetadataDTO.setFileName(item.objectName());
                fileMetadataDTO.setFileSize(item.size());
                fileMetadataDTO.setUploadDate(dateFormat.format(Date.from(item.lastModified().toInstant())));
                fileMetadataDTO.setFileType("application/octet-stream");
                fileList.add(fileMetadataDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileList;
    }

    public String createShareableLink(String userId, String fileName, String permission, Date expirationDate) {
        FileShareMetadata metadata = new FileShareMetadata(userId, fileName, permission, expirationDate);
        return tokenService.createToken(metadata);
    }

    public byte[] accessSharedFile(String token) {
        try {
            FileShareMetadata metadata = tokenService.getMetadataFromToken(token);
            if (metadata == null || metadata.getExpirationDate().before(new Date())) {
                throw new SecurityException("Token không hợp lệ hoặc đã hết hạn.");
            }
            String bucketName = "bucket-" + metadata.getUserId();
            String objectName = metadata.getObjectName();
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            return stream.readAllBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] downloadFile(String userId, String objectName) {
        String bucketName = "bucket-" + userId;
        String sanitizedObjectName = objectName.replaceAll("[^\\x20-\\x7E]", "_");
        try {
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(sanitizedObjectName)
                    .build());
            return stream.readAllBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String deleteFile(String userId, String objectName) {
        String bucketName = "bucket-" + userId;
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            return "File deleted successfully: " + objectName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to delete file: " + e.getMessage();
        }
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
