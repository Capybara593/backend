package com.example.demo.model;

public class FileShareMetadata {
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    private String userId;
    private String objectName;
    private String permission;

    public FileShareMetadata(String userId, String objectName, String permission) {
        this.userId = userId;
        this.objectName = objectName;
        this.permission = permission;
    }

    public String getUserId() {
        return userId;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getPermission() {
        return permission;
    }

    public boolean hasAccess() {
        // Điều kiện để kiểm tra quyền truy cập
        return permission.equals("read") || permission.equals("edit");
    }
}
