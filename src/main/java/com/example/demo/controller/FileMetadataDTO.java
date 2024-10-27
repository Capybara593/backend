package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Date;

//@CrossOrigin(origins = {"https://capybara593.github.io", "http://localhost:3000", "https://foxhound-sharing-mackerel.ngrok-free.app", "https://df01-2001-ee0-4fc5-56f0-f547-fb30-a2d4-615f.ngrok-free.app"})
public class FileMetadataDTO {
    private String fileName;
    private Long fileSize;
    private String fileType;
    private Date uploadDate;

    public FileMetadataDTO() {}

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }
}
