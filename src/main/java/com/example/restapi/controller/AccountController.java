package com.example.restapi.controller;

import com.example.restapi.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String username,
                                      @RequestParam String email,
                                      @RequestParam String password
    ) {
        return userService.registerUser(username, email, password);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String username,
                                       @RequestParam String email,
                                       @RequestParam String password, @RequestParam String otp
    ) {
        return userService.verifyOtp(username,email,password, otp);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> sendResetOtp(@RequestParam String email) {
        return userService.sendResetOtp(email);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email,
                                           @RequestParam String otp,
                                           @RequestParam String newPassword
    ) {
        return userService.resetPassword(email, otp, newPassword);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        return userService.loginUser(email, password);
    }


}