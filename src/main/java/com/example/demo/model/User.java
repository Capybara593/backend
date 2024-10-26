package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;


import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "user")
public class User {
    @Id
    private String userId;
    private String fullName;
    private String address;
    private String birthDay;
    private String image;
    private String username;
    private String password;
    private String phoneNumber;
    private String email;

    // Thêm mối quan hệ một User có nhiều FileMetadata
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileMetadata> files;
    public Boolean getTatus() {
        return isTatus;
    }

    public void setTatus(Boolean tatus) {
        isTatus = tatus;
    }

    public Boolean isTatus;

    public User(){

    }
    public User(String userId, String fullName, String address, String birthDay, String image, String username, String password, String phoneNumber, String email) {
        this.userId = userId;
        this.fullName = fullName;
        this.address = address;
        this.birthDay = birthDay;
        this.image = image;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;

    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
