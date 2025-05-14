package com.example.restapi.account.repository;

import com.example.restapi.account.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndPassword(String username, String password);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByUsernameAndEmail(String username, String email);
    Optional<User> findByEmail(String email);
}