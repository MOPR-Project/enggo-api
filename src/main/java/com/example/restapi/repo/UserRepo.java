package com.example.restapi.repo;

import com.example.restapi.model.Otp;
import com.example.restapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);


    Optional<User> findByEmail(String email);
}
