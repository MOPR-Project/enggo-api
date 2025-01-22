package com.example.restapi.repo;

import com.example.restapi.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepo extends JpaRepository<Otp, Long> {
    Optional<Otp> findByEmailAndOtp(String email, String otp);
    Optional<Otp> findByEmail(String email);
}
