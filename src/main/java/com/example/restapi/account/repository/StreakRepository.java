package com.example.restapi.account.repository;

import com.example.restapi.account.model.Otp;
import com.example.restapi.account.model.Streak;
import com.example.restapi.account.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StreakRepository extends JpaRepository<Streak, Long> {
    Optional<Streak> findByUser(User user);
}
