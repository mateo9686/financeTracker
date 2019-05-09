package com.kedzierski.financetracker.repository;

import com.kedzierski.financetracker.entity.AppUser;
import com.kedzierski.financetracker.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository
        extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(AppUser user);

    void deleteByUser(AppUser user);
}
