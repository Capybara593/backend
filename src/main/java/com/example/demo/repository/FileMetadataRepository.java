package com.example.demo.repository;

import com.example.demo.model.FileMetadata;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    List<FileMetadata> findByUser_UserId(String userId);

    @Transactional
    void deleteByFileUrl(String fileUrl);
}
