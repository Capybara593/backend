package com.example.demo.service;

import io.minio.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.model.FileMetadata;
import com.example.demo.model.User;
import com.example.demo.repository.FileMetadataRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.controller.FileMetadataDTO;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MinIOService {

    private final MinioClient minioClient;

    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    @Autowired
    private UserRepository userRepository;

    public MinIOService(
            @Value("${minio.url}") String url,
            @Value("${minio.accessKey}") String accessKey,
            @Value("${minio.secretKey}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String uploadFile(MultipartFile file, String userId) {
        String bucketName = "bucket-" + userId;

        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            String objectName = userId + "_" + file.getOriginalFilename().replaceAll("[^\\x20-\\x7E]", "_");

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            FileMetadata fileMetadata = new FileMetadata(
                    user,
                    file.getOriginalFilename(),
                    objectName,
                    file.getSize(),
                    file.getContentType(),
                    new Date()
            );
            fileMetadataRepository.save(fileMetadata);

            return "File uploaded successfully: " + objectName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to upload file: " + e.getMessage();
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

            fileMetadataRepository.deleteByFileUrl(objectName);

            return "File deleted successfully: " + objectName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to delete file: " + e.getMessage();
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
                fileMetadataDTO.setUploadDate(Date.from(item.lastModified().toInstant()));
                fileMetadataDTO.setFileType("application/octet-stream"); // MinIO không lưu MIME type

                fileList.add(fileMetadataDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileList;
    }
}
