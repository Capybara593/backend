package com.example.demo.service;

import com.example.demo.model.FileShareMetadata;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {
    private Map<String, FileShareMetadata> tokenStore = new HashMap<>();

    // Lưu metadata với token
    public String createToken(FileShareMetadata metadata) {
        String token = metadata.getUserId() + "_" + metadata.getObjectName() + "_" + System.currentTimeMillis();
        tokenStore.put(token, metadata);
        return token;
    }

    // Lấy metadata từ token
    public FileShareMetadata getMetadataFromToken(String token) {
        return tokenStore.get(token);
    }
}
