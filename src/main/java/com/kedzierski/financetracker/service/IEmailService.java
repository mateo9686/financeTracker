package com.kedzierski.financetracker.service;

import com.kedzierski.financetracker.entity.AppUser;

public interface IEmailService {

    void sendEmailConfirmationMessage(AppUser user, String token, String url);

    void sendResetPasswordMessage(AppUser user, String token, String url);
}
