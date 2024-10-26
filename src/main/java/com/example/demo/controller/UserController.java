package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.MinIOService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.OpenOption;
import java.util.List;
import java.util.Optional;

//@CrossOrigin(origins = { "https://capybara593.github.io", " https://fresh-plants-judge.loca.lt" })

 // Cho phép CORS từ GitHub Pages
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserService userService;


    @GetMapping("/result/{userId}")
    public User getUserById(@PathVariable("userId") String userId) {

        return userService.getUserById(userId);
    }
    // API đăng ký người dùng
    @PostMapping("/add")
    public ResponseEntity<String> addUser(@RequestBody User user) {
        userService.addUser(user); // Gọi service để thêm user mới
        return ResponseEntity.ok("User registered successfully"); // Trả về phản hồi thành công
    }

    // API đăng nhập người dùng
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String userId, @RequestParam String password) {
        String result = userService.login(userId, password);
        if ("Success".equals(result)) {
            return ResponseEntity.ok(result); // Trả về 'Success' nếu đăng nhập đúng
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password"); // Trả về lỗi nếu đăng nhập thất bại
    }
    @GetMapping("/list")
    public List<User> getAllUserList(){
        return userService.getAllUser();
    }
    @PutMapping("/update")
    public String updateUserById(@RequestBody User user){

        return userService.updateUser(user);
    }
    @PutMapping("/delete/{userId}")
    public String deleteUserById(@PathVariable("userId") String userId){
        return userService.removeUser(userId);
    } // API upload file và liên kết với User


}
