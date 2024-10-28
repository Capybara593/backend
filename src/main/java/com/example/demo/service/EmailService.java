package com.example.demo.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MinioClient minioClient;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public String sendEmailWithAttachment(String toEmail, String subject, String body, String userId, String objectName) {
        try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                .bucket("bucket-" + userId)
                .object(objectName)
                .build())) {

            // Tạo email với đính kèm file
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body);

            // Đính kèm file
            ByteArrayResource resource = new ByteArrayResource(stream.readAllBytes());
            helper.addAttachment(objectName, resource);

            // Gửi email
            mailSender.send(message);
            return "Email sent successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to send email: " + e.getMessage();
        }
    }
}
