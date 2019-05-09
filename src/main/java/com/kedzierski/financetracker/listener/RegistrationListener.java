package com.kedzierski.financetracker.listener;

import com.kedzierski.financetracker.entity.AppUser;
import com.kedzierski.financetracker.event.OnRegistrationCompleteEvent;
import com.kedzierski.financetracker.service.IEmailService;
import com.kedzierski.financetracker.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements
        ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private IUserService service;

    @Autowired
    private IEmailService emailService;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        AppUser user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token, false);
        //TODO: what is event.getAppUrl()?
        String url = event.getAppUrl() + "/registrationConfirm?token=";
        emailService.sendEmailConfirmationMessage(user, token, url);
    }
}
