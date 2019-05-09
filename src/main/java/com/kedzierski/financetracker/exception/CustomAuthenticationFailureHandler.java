package com.kedzierski.financetracker.exception;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        setDefaultFailureUrl("/login?error=true");

        super.onAuthenticationFailure(request, response, exception);

        String errorMessage = "";

        if (exception.getClass().equals(BadCredentialsException.class)) {
            errorMessage = "Incorrect username or password. " + exception.getMessage();
        }

        if (exception.getClass().equals(DisabledException.class)) {
            errorMessage = "Email address has not been confirmed. To activate your account click on the activation link in your email inbox.";
        }
        if (exception.getClass().equals(LockedException.class)) {
            errorMessage = "Your account is blocked";
        }
        if (exception.getMessage().equalsIgnoreCase("blocked")) {
            errorMessage = "Your account has been blocked for 24 Hours due to too many failed login attempts.";
        }

        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage);
    }
}
