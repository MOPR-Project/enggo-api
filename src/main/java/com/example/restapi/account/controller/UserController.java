package com.example.restapi.account.controller;

import com.example.restapi.account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                      @RequestParam String password
    ) {
        return userService.loginUser(username, password);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String username,
                                      @RequestParam String password,
                                      @RequestParam String email
    ) {
        return userService.registerUser(username, password, email);
    }

    @PostMapping("/verify-register")
    public ResponseEntity<?> verifyRegister(@RequestParam String username,
                                       @RequestParam String password,
                                       @RequestParam String email,
                                       @RequestParam String otpCode
    ) {
        return userService.verifyRegister(username, password, email, otpCode);
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
