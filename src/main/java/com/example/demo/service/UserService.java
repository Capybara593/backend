package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    // Khai báo BCryptPasswordEncoder chỉ một lần, tránh khởi tạo lại nhiều lần
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Lấy thông tin người dùng theo ID và kiểm tra trạng thái người dùng
    public User getUserById(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        return (user != null && !user.getTatus()) ? user : null; // Trả về null nếu user bị vô hiệu hóa
    }

    // Lấy tất cả người dùng có trạng thái hoạt động
    public List<User> getAllUser() {
        return userRepository.findAll().stream()
                .filter(user -> !user.getTatus()) // Lọc chỉ lấy user hoạt động
                .collect(Collectors.toList());
    }

    // Cập nhật thông tin người dùng nếu tồn tại
    public String updateUser(User user) {
        Optional<User> userId = userRepository.findById(user.getUserId());
        if (userId.isPresent()) {
            userRepository.save(user); // Lưu thay đổi nếu user tồn tại
            return "Update Success";
        }
        return "Update Fail"; // Thông báo nếu không tìm thấy user
    }

    // Xóa người dùng bằng cách đặt trạng thái 'Tatus' thành true
    public String removeUser(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setTatus(true); // Đánh dấu là đã bị xóa
            userRepository.save(user);
            return "Remove Successful";
        }
        return "User not found";
    }

    // Xử lý đăng nhập với mã hóa BCrypt cho bảo mật
    public String login(String userId, String password) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return "Success"; // Trả về 'Success' nếu đăng nhập đúng
        }
        return "Fails"; // Trả về 'Fails' nếu đăng nhập thất bại
    }

    // Thêm người dùng mới và mã hóa mật khẩu trước khi lưu
    public void addUser(User user) {
        if (!userRepository.existsById(user.getUserId())) {
            String encryptedPassword = passwordEncoder.encode(user.getPassword()); // Mã hóa mật khẩu
            user.setPassword(encryptedPassword);
            userRepository.save(user); // Lưu người dùng mới
        }
    }
}
