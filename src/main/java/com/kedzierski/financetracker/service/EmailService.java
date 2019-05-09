package com.kedzierski.financetracker.service;

import com.kedzierski.financetracker.entity.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailService implements IEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmailConfirmationMessage(AppUser user, String token, String url) {
        String recipientAddress = user.getUserEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl = url + token;

        String message = "registration completed";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " http://localhost:10000" + confirmationUrl);
        mailSender.send(email);
    }

    @Override
    public void sendResetPasswordMessage(AppUser user, String token, String url) {
        String recipientAddress = user.getUserEmail();
        String subject = "Reset Password";
        String confirmationUrl = url + token;

        String message = "Password reset link";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " http://localhost:10000" + confirmationUrl);
        mailSender.send(email);
    }
}
