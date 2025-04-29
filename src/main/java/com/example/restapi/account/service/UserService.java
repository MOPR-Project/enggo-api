package com.example.restapi.account.service;

import com.example.restapi.account.model.Otp;
import com.example.restapi.account.model.User;
import com.example.restapi.account.repository.OtpRepository;
import com.example.restapi.account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private OtpService otpService;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;

    private String generateOtpCode() {
        SecureRandom random = new SecureRandom();
        return new DecimalFormat("000000").format(random.nextInt(1000000));
    }

    public ResponseEntity<?> loginUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsernameAndPassword(username, password);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Wrong username or password!"));
        }

        User user = userOptional.get();

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Login successful!",
                "user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail()
                )
        ));
    }

    public ResponseEntity<?> registerUser(String username, String password, String email) {
        if (userRepository.existsByEmail(email) || userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Email or Username already exists!"));
        }

        String otpCode = generateOtpCode();

        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otp.setVerified(false);
        otpRepository.save(otp);

        otpService.sendOtp(email, otpCode);

        return ResponseEntity.ok(Map.of("status", "success", "message", "OTP sent to your email. Please verify."));
    }

    public ResponseEntity<?> verifyRegister(String username, String password, String email, String otpCode) {
        Optional<Otp> otpOptional = otpRepository.findByEmailAndOtpCode(email, otpCode);

        if (otpOptional.isEmpty() || otpOptional.get().isVerified()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Incorrect or already used OTP!"));
        }

        Otp otp = otpOptional.get();
        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "OTP has expired!"));
        }

        otp.setVerified(true);
        otpRepository.save(otp);

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("status", "success", "message", "User registered successfully!"));
    }

    public ResponseEntity<?> forgetPassword(String username, String email) {
        Optional<User> userOptional = userRepository.findByUsernameAndEmail(username, email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Wrong username or email!"));
        }

        String otpCode = generateOtpCode();

        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otp.setVerified(false);
        otpRepository.save(otp);

        otpService.sendOtp(email, otpCode);

        return ResponseEntity.ok(Map.of("status", "success", "message", "OTP sent to your email. Please verify."));
    }

    public ResponseEntity<?> verifyForget(String username, String newPassword, String email, String otpCode) {
        Optional<Otp> otpOptional = otpRepository.findByEmailAndOtpCode(email, otpCode);

        if (otpOptional.isEmpty() || otpOptional.get().isVerified()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid or already used OTP!"));
        }

        Otp otp = otpOptional.get();
        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "OTP has expired!"));
        }

        otp.setVerified(true);
        otpRepository.save(otp);

        User user = userRepository.findByUsernameAndEmail(username, email).get();
        user.setPassword(newPassword);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("status", "success", "message", "Password changed successfully!"));
    }
}
