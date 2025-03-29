package com.example.restapi.account.controller;

import com.example.restapi.account.model.request.LoginRequest;
import com.example.restapi.account.model.request.RegisterRequest;
import com.example.restapi.account.model.request.VerifyRegisterRequest;
import com.example.restapi.account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return userService.loginUser(
                loginRequest.getUsername(),
                loginRequest.getPassword());
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
}
