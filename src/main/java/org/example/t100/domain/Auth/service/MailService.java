package org.example.t100.domain.Auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.example.t100.domain.Auth.entity.User;
import org.example.t100.domain.Auth.exception.UserNotFoundException;
import org.example.t100.domain.Auth.repository.UserRepository;
import org.example.t100.domain.community.exception.CommunityNotFoundException;
import org.example.t100.global.Enum.ErrorCode;
import org.example.t100.global.Enum.SuccessCode;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import static org.example.t100.global.Enum.SuccessCode.MAIL_SUCCESS;

@Service
public class MailService {
    private final JavaMailSender emailSender;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public MailService(JavaMailSender emailSender, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.emailSender = emailSender;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    public SuccessCode findPass(String email) {
        try {
            String newPassword = createPass(email);

            // HTML 템플릿 파일 읽기
            String htmlTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/templates/password.html")), "UTF-8");

            // 템플릿에서 {{newPassword}}를 실제 비밀번호로 치환
            String htmlContent = htmlTemplate.replace("{{newPassword}}", newPassword);

            // 이메일 생성 및 설정
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email); // 수신자
            helper.setFrom("inomotorservice@naver.com"); // 발신자 (SMTP 계정과 동일해야 함)
            helper.setSubject("변경된 비밀번호 안내");
            helper.setText(htmlContent, true); // HTML 내용 설정

            // 이메일 전송
            emailSender.send(message);
            System.out.println("HTML Email sent successfully!");

            return SuccessCode.MAIL_SUCCESS;

        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        } catch (Exception e) {
            System.err.println("Error reading HTML template: " + e.getMessage());
            throw new RuntimeException("Failed to process email template", e);
        }
    }
    public String createPass(String Email) {
        User user = userRepository.findByEmail(Email).orElseThrow(() ->
                new UserNotFoundException(ErrorCode.NOT_FOUND_DATA));

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(characters.length());
            result.append(characters.charAt(index));
        }
        user.setPass(bCryptPasswordEncoder.encode(result.toString()));
        userRepository.save(user);
        return result.toString();
    }
}
