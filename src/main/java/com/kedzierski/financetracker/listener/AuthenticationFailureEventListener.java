package com.kedzierski.financetracker.listener;

import com.kedzierski.financetracker.service.ILoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureEventListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private ILoginAttemptService loginAttemptService;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent authenticationFailureBadCredentialsEvent) {
        WebAuthenticationDetails auth = (WebAuthenticationDetails) authenticationFailureBadCredentialsEvent.
                getAuthentication().getDetails();

        loginAttemptService.loginFailed(auth.getRemoteAddress());

        throw new BadCredentialsException("You have " + loginAttemptService.attemptsLeft(auth.getRemoteAddress()) + " attempts left.");

    }
}