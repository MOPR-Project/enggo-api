package com.example.restapi.account.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;

    private String name;

    private LocalDate dateOfBirth;

    private String gender;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] avatar;
}
