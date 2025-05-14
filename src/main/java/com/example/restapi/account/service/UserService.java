package com.example.restapi.account.service;

import com.example.restapi.account.model.Otp;
import com.example.restapi.account.model.Streak;
import com.example.restapi.account.model.User;
import com.example.restapi.account.repository.OtpRepository;
import com.example.restapi.account.repository.StreakRepository;
import com.example.restapi.account.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private StreakRepository streakRepository;

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

        Streak streak = streakRepository.findByUser(user).orElseGet(() -> {
            Streak newStreak = new Streak();
            newStreak.setUser(user);
            newStreak.setCurrentStreak(1);
            newStreak.setMaxStreak(1);
            newStreak.setLastUpdated(LocalDate.now());
            return newStreak;
        });

        LocalDate today = LocalDate.now();
        LocalDate lastDate = streak.getLastUpdated();

        if (lastDate != null) {
            if (lastDate.equals(today.minusDays(1))) {
                streak.setCurrentStreak(streak.getCurrentStreak() + 1);
            } else if (!lastDate.equals(today)) {
                streak.setCurrentStreak(1);
            }
        }

        streak.setLastUpdated(today);
        if (streak.getCurrentStreak() > streak.getMaxStreak()) {
            streak.setMaxStreak(streak.getCurrentStreak());
        }
        streakRepository.save(streak);

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("username", user.getUsername());
        userData.put("password", user.getPassword());
        userData.put("email", user.getEmail());
        userData.put("name", user.getName());
        userData.put("dateOfBirth", user.getDateOfBirth());
        userData.put("gender", user.getGender());

        if (user.getAvatar() != null && user.getAvatar().length > 0) {
            String base64Avatar = Base64.getEncoder().encodeToString(user.getAvatar());
            userData.put("avatar", base64Avatar);
        } else {
            userData.put("avatar", null);
        }
        userData.put("streak", streak.getCurrentStreak());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Login successful!",
                "user", userData
        ));
    }

    public ResponseEntity<?> loginWithGoogle(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList("187794834711-fovk8v7b6s6fdf80shdssj975s5e1nqj.apps.googleusercontent.com"))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid ID token!"));
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            Optional<User> userOptional = userRepository.findByEmail(email);
            User user;

            if (userOptional.isPresent()) {
                user = userOptional.get();
            } else {
                user = new User();
                user.setEmail(email);
                user.setUsername(email);
                user.setPassword(UUID.randomUUID().toString());
                user.setName(name);
                userRepository.save(user);
            }

            Streak streak = streakRepository.findByUser(user).orElseGet(() -> {
                Streak newStreak = new Streak();
                newStreak.setUser(user);
                newStreak.setCurrentStreak(1);
                newStreak.setMaxStreak(1);
                newStreak.setLastUpdated(LocalDate.now());
                return newStreak;
            });

            LocalDate today = LocalDate.now();
            LocalDate lastDate = streak.getLastUpdated();

            if (lastDate != null) {
                if (lastDate.equals(today.minusDays(1))) {
                    streak.setCurrentStreak(streak.getCurrentStreak() + 1);
                } else if (!lastDate.equals(today)) {
                    streak.setCurrentStreak(1);
                }
            }

            streak.setLastUpdated(today);
            if (streak.getCurrentStreak() > streak.getMaxStreak()) {
                streak.setMaxStreak(streak.getCurrentStreak());
            }
            streakRepository.save(streak);

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("password", user.getPassword());
            userData.put("email", user.getEmail());
            userData.put("name", user.getName());
            userData.put("dateOfBirth", user.getDateOfBirth());
            userData.put("gender", user.getGender());

            if (user.getAvatar() != null) {
                String base64Avatar = Base64.getEncoder().encodeToString(user.getAvatar());
                userData.put("avatar", base64Avatar);
            } else {
                userData.put("avatar", null);
            }

            userData.put("streak", streak.getCurrentStreak());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Login with Google successful!",
                    "user", userData
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("status", "error", "message", "Google login failed!"));
        }
    }

    public ResponseEntity<?> loginAnonymous() {
        String username;
        String password;
        do {
            username = "anonymous_" + UUID.randomUUID().toString().substring(0, 8);
            password = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        } while (userRepository.existsByUsername(username));

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        userRepository.save(user);

        Streak streak = new Streak();
        streak.setUser(user);
        streak.setCurrentStreak(1);
        streak.setMaxStreak(1);
        streak.setLastUpdated(LocalDate.now());
        streakRepository.save(streak);

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("username", user.getUsername());
        userData.put("password", user.getPassword());
        userData.put("email", user.getEmail());
        userData.put("name", user.getName());
        userData.put("dateOfBirth", user.getDateOfBirth());
        userData.put("gender", user.getGender());
        userData.put("avatar", null);
        userData.put("streak", streak.getCurrentStreak());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Anonymous account created!",
                "user", userData
        ));
    }

    public ResponseEntity<?> registerUser(String username, String password, String email) {
        if (username.toLowerCase().startsWith("anonymous")) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Username cannot start with 'anonymous'!"
            ));
        }

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

    public ResponseEntity<?> updateUserProfile(String username, String password, String name, LocalDate dateOfBirth, String gender) {
        Optional<User> userOptional = userRepository.findByUsernameAndPassword(username, password);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "User not found!"));
        }

        User user = userOptional.get();

        if (dateOfBirth != null) {
            LocalDate today = LocalDate.now();
            if (dateOfBirth.isAfter(today)) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Date of birth cannot be in the future!"));
            }

            int age = Period.between(dateOfBirth, today).getYears();
            if (age < 5) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "You must be at least 5 years old!"));
            }
            if (age > 100) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Age cannot be more than 100 years old!"));
            }

            user.setDateOfBirth(dateOfBirth);
        }

        if (name != null && !name.isBlank()) {
            user.setName(name);
        }

        if (gender != null && !gender.isBlank()) {
            user.setGender(gender);
        }

        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Profile updated successfully!"
        ));
    }

    public ResponseEntity<?> updateAvatarFile(String username, String password, MultipartFile avatarFile) {
        Optional<User> userOptional = userRepository.findByUsernameAndPassword(username, password);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "User not found!"));
        }

        if (avatarFile == null || avatarFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "No file uploaded!"));
        }

        try {
            User user = userOptional.get();
            user.setAvatar(avatarFile.getBytes());
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Avatar updated successfully!"
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Error processing the file!"
            ));
        }
    }

    public ResponseEntity<?> upgradeAnonymous(String oldUsername, String oldPassword, String newEmail) {
        System.out.println("DEBUG - oldUsername: " + oldUsername);
        System.out.println("DEBUG - oldPassword: " + oldPassword);
        Optional<User> userOptional = userRepository.findByUsernameAndPassword(oldUsername, oldPassword);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid anonymous account credentials!"));
        }

        if (userRepository.existsByEmail(newEmail)) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Email already exists!"));
        }

        String otpCode = generateOtpCode();

        Otp otp = new Otp();
        otp.setEmail(newEmail);
        otp.setOtpCode(otpCode);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otp.setVerified(false);
        otpRepository.save(otp);

        otpService.sendOtp(newEmail, otpCode);

        return ResponseEntity.ok(Map.of("status", "success", "message", "OTP sent to your new email. Please verify."));
    }

    public ResponseEntity<?> verifyUpgradeAnonymous(String oldUsername, String oldPassword,
                                                    String newUsername, String newPassword,
                                                    String newEmail, String otpCode) {
        Optional<User> userOptional = userRepository.findByUsernameAndPassword(oldUsername, oldPassword);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid anonymous account credentials!"));
        }

        if (newUsername.toLowerCase().startsWith("anonymous")) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Username cannot start with 'anonymous'!"));
        }

        if (userRepository.existsByUsername(newUsername)) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Username already exists!"));
        }

        Optional<Otp> otpOptional = otpRepository.findByEmailAndOtpCode(newEmail, otpCode);
        if (otpOptional.isEmpty() || otpOptional.get().isVerified()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid or already used OTP!"));
        }

        Otp otp = otpOptional.get();
        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "OTP has expired!"));
        }

        otp.setVerified(true);
        otpRepository.save(otp);

        User user = userOptional.get();
        user.setUsername(newUsername);
        user.setPassword(newPassword);
        user.setEmail(newEmail);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("status", "success", "message", "Anonymous account upgraded successfully!"));
    }
}
