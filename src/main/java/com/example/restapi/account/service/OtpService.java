package com.example.restapi.account.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;



@Service
public class OtpService {
    private final JavaMailSender javaMailSender;

    public OtpService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public boolean sendOtp(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            String subject = "🔐 Xác nhận đăng ký tài khoản - Enggo";

            String content = "<div style='font-family:Arial,sans-serif;padding:20px;border:1px solid #ddd;border-radius:8px;width:500px;'>"
                    + "<h2 style='color:#2c3e50;'>Mã xác nhận đăng ký</h2>"
                    + "<p>Xin chào,</p>"
                    + "<p>Cảm ơn bạn đã đăng ký tài khoản trên <strong>Enggo</strong>.</p>"
                    + "<p>Mã OTP của bạn là:</p>"
                    + "<h2 style='text-align:center;background:#f4f4f4;padding:10px;border-radius:5px;'>"
                    + otp + "</h2>"
                    + "<p>Xin lưu ý rằng otp sẽ hết hạn sau <strong>10 phút</strong>. Vui lòng không chia sẻ OTP với bất kỳ ai.</p>"
                    + "<p>Nếu bạn không yêu cầu đăng ký tài khoản, vui lòng bỏ qua email này.</p>"
                    + "<br><p>Trân trọng,<br><strong>Đội ngũ hỗ trợ - Enggo</strong></p>"
                    + "</div>";

            message.setTo(email);
            message.setSubject(subject);
            message.setText("Mã OTP của bạn là: " + otp);

            // javaMailSender.send(message);
            sendHtmlEmail(email, subject, content);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi gửi email OTP!");
        }
    }
}
