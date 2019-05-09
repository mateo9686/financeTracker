package com.kedzierski.financetracker.service;

public interface ILoginAttemptService {

    void loginSucceeded(String key);

    void loginFailed(String key);

    boolean isBlocked(String key);

    int attemptsLeft(String key);
}

