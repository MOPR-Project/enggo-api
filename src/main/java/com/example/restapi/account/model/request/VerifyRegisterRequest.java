package com.example.restapi.account.model.request;
import lombok.Data;

@Data
public class VerifyRegisterRequest {
    private String username;
    private String email;
    private String password;
    private String otpCode;
}
