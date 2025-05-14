package com.example.restapi.account.model.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    private String username;
    private String password;
    private String name;
    private LocalDate dateOfBirth;
    private String gender;
}
