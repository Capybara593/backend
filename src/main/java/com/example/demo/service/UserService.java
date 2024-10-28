package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User getUserById(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        return (user != null && Boolean.TRUE.equals(user.getStatus())) ? user : null;
    }

    public List<User> getAllUser() {
        return userRepository.findAll().stream()
                .filter(user -> Boolean.TRUE.equals(user.getStatus()))
                .collect(Collectors.toList());
    }

    public String updateUser(User user) {
        Optional<User> existingUserOpt = userRepository.findById(user.getUserId());
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            existingUser.setFullName(user.getFullName());
            existingUser.setAddress(user.getAddress());
            existingUser.setBirthDay(user.getBirthDay());
            existingUser.setImage(user.getImage());
            existingUser.setUsername(user.getUsername());
            existingUser.setPassword(passwordEncoder.encode(user.getPassword())); // Mã hóa mật khẩu
            existingUser.setPhoneNumber(user.getPhoneNumber());
            existingUser.setEmail(user.getEmail());
            existingUser.setStatus(user.getStatus());
            userRepository.save(existingUser);
            return "Update Success";
        }
        return "Update Fail";
    }

    public String removeUser(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setStatus(false); // Đánh dấu là đã bị xóa
            userRepository.save(user);
            return "Remove Successful";
        }
        return "User not found";
    }

    public String login(String userId, String password) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return "Success";
        }
        return "Fails";
    }

    public void addUser(User user) {
        if (!userRepository.existsById(user.getUserId())) {
            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);
            userRepository.save(user);
        }
    }
}
