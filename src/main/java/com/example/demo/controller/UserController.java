package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/result/{userId}")
    public User getUserById(@PathVariable("userId") String userId) {
        return userService.getUserById(userId);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addUser(@RequestBody User user) {
        userService.addUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String userId, @RequestParam String password) {
        String result = userService.login(userId, password);
        if ("Success".equals(result)) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }

    @GetMapping("/list")
    public List<User> getAllUserList() {
        return userService.getAllUser();
    }

    @PutMapping("/update")
    public String updateUserById(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/delete/{userId}")
    public String deleteUserById(@PathVariable("userId") String userId) {
        return userService.removeUser(userId);
    }
}
