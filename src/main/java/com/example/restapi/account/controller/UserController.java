package com.example.restapi.account.controller;

import com.example.restapi.account.model.request.LoginRequest;
import com.example.restapi.account.model.request.RegisterRequest;
import com.example.restapi.account.model.request.UpdateProfileRequest;
import com.example.restapi.account.model.request.VerifyRegisterRequest;
import com.example.restapi.account.service.OtpService;
import com.example.restapi.account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/account")
public class UserController {
    private UserService userService;
    private OtpService otpService;
    private Map<String, Long> otpRequestTime = new ConcurrentHashMap<>();
    private static final long OTP_RESEND_INTERVAL = 60 * 1000;

    @Autowired
    public UserController(UserService userService, OtpService otpService) {
        this.userService = userService;
        this.otpService = otpService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return userService.loginUser(
                loginRequest.getUsername(),
                loginRequest.getPassword());
    }

        @PostMapping("/login-anonymous")
    public ResponseEntity<?> loginAnonymous() {
        return userService.loginAnonymous();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        return userService.registerUser(
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getEmail());
    }

    @PostMapping("/verify-register")
    public ResponseEntity<?> verifyRegister(@RequestBody VerifyRegisterRequest verifyRegisterRequest)
    {
        return userService.verifyRegister(
                verifyRegisterRequest.getUsername(),
                verifyRegisterRequest.getPassword(),
                verifyRegisterRequest.getEmail(),
                verifyRegisterRequest.getOtpCode());
    }

    @PostMapping("/forget")
    public ResponseEntity<?> forget(@RequestParam String username,
                                            @RequestParam String email
    ) {
        return userService.forgetPassword(username, email);
    }

    @PostMapping("/verify-forget")
    public ResponseEntity<?> verifyForget(@RequestParam String username,
                                            @RequestParam String newPassword,
                                            @RequestParam String email,
                                            @RequestParam String otpCode
    ) {
        return userService.verifyForget(username, newPassword, email, otpCode);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Invalid Email!"
            ));
        }

        long now = System.currentTimeMillis();
        Long lastRequest = otpRequestTime.get(email);

        if (lastRequest != null && (now - lastRequest) < OTP_RESEND_INTERVAL) {
            long secondsLeft = (OTP_RESEND_INTERVAL - (now - lastRequest)) / 1000;
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Wait " + secondsLeft + " second!"
            ));
        }

        String otp = String.valueOf((int)((Math.random() * 900000) + 100000));

        boolean success = otpService.sendOtp(email, otp);

        if (success) {
            otpRequestTime.put(email, now);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Send OTP success."
            ));
        } else {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Sen OTP fail!"
            ));
        }
    }

        @PostMapping("/update-profile")
        public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request) {
            return userService.updateUserProfile(
                    request.getUsername(),
                    request.getPassword(),
                    request.getName(),
                    request.getDateOfBirth(),
                    request.getGender()
            );
        }

    @PostMapping("/update-avatar")
    public ResponseEntity<?> updateAvatarFile(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam("avatar") MultipartFile avatarFile
    ) {
        return userService.updateAvatarFile(username, password, avatarFile);
    }

    @PostMapping("/upgrade-anonymous")
    public ResponseEntity<?> upgradeAnonymous(
            @RequestParam String oldUsername,
            @RequestParam String oldPassword,
            @RequestParam String newEmail
    ) {
        return userService.upgradeAnonymous(oldUsername, oldPassword, newEmail);
    }

    @PostMapping("/login-google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> request) {
        String idToken = request.get("idToken");
        return userService.loginWithGoogle(idToken);
    }

    @PostMapping("/verify-upgrade-anonymous")
    public ResponseEntity<?> verifyUpgradeAnonymous(
            @RequestParam String oldUsername,
            @RequestParam String oldPassword,
            @RequestParam String newUsername,
            @RequestParam String newPassword,
            @RequestParam String newEmail,
            @RequestParam String otpCode
    ) {
        return userService.verifyUpgradeAnonymous(
                oldUsername,
                oldPassword,
                newUsername,
                newPassword,
                newEmail,
                otpCode
        );
    }
}
