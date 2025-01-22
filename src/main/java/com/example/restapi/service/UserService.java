package com.example.restapi.service;
import com.example.restapi.model.Otp;
import com.example.restapi.model.User;
import com.example.restapi.repo.OtpRepo;
import com.example.restapi.repo.UserRepo;
import com.example.restapi.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private OtpRepo otpRepo;

    @Autowired
    private EmailService emailService;


    public ResponseEntity<?> registerUser(String username, String email, String password) {
        if (userRepo.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Email already exists!"));
        }

        String otp = String.valueOf((int) (Math.random() * 9000));
        Otp otpVerification = new Otp();
        otpVerification.setEmail(email);
        otpVerification.setOtp(otp);
        otpVerification.setCreatedTime(LocalDateTime.now());
        otpVerification.setVerified(false);
        otpRepo.save(otpVerification);


        emailService.sendOtpEmail(email, otp);

        return ResponseEntity.ok(Map.of("status", "success", "message", "OTP sent to your email. Please verify."));
    }

    public ResponseEntity<?> verifyOtp(String username, String email, String password, String otp) {
        Optional<Otp> otpVerification = otpRepo.findByEmailAndOtp(email, otp);

        if (otpVerification.isEmpty() || otpVerification.get().isVerified()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid or already used OTP!"));
        }

        Otp verifiedOtp = otpVerification.get();
        verifiedOtp.setVerified(true);
        otpRepo.save(verifiedOtp);

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        userRepo.save(user);

        return ResponseEntity.ok(Map.of("status", "success", "message", "User registered successfully!"));
    }

    public ResponseEntity<?> loginUser(String email, String password) {
        Optional<User> userOptional = userRepo.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "User not found!"));
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

    private final Map<String, String> otpStore = new HashMap<>();

    public ResponseEntity<?> sendResetOtp(String email) {
        Optional<User> userOptional = userRepo.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "User not found!"));
        }

        String otp = String.valueOf((int)(Math.random() * 1000));
        otpStore.put(email, otp);

        emailService.sendOtpEmail(email, otp);

        return ResponseEntity.ok(Map.of("status", "success", "message", "OTP sent to your email!"));
    }

    public ResponseEntity<?> resetPassword(String email, String otp, String newPassword) {
        if (!otpStore.containsKey(email) || !otpStore.get(email).equals(otp)) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid OTP!"));
        }

        Optional<User> userOptional = userRepo.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "User not found!"));
        }

        User user = userOptional.get();
        user.setPassword(newPassword);
        userRepo.save(user);

        otpStore.remove(email);

        return ResponseEntity.ok(Map.of("status", "success", "message", "Password reset successfully!"));
    }
}
