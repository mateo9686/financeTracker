package com.kedzierski.financetracker.repository;

import com.kedzierski.financetracker.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByUserEmail(String email);

    AppUser findByUserId(long id);
}
