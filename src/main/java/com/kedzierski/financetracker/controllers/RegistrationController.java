package com.kedzierski.financetracker.controllers;

import com.kedzierski.financetracker.dto.AppUserDTO;
import com.kedzierski.financetracker.entity.AppUser;
import com.kedzierski.financetracker.entity.VerificationToken;
import com.kedzierski.financetracker.event.OnRegistrationCompleteEvent;
import com.kedzierski.financetracker.exception.EmailExistsException;
import com.kedzierski.financetracker.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Calendar;

@Controller
public class RegistrationController {

    @Autowired
    IUserService service;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String showRegistrationForm(WebRequest request, Model model) {
        AppUserDTO user = new AppUserDTO();
        model.addAttribute("user", user);
        return "registrationForm";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid AppUserDTO accountDTO, BindingResult result,
                                            WebRequest request, Errors errors) {

        AppUser registered = createUserAccount(accountDTO, result);

        if (registered == null && !result.hasFieldErrors("email")) {
            result.rejectValue("email", "error.emailExists");
        }
        if (result.hasErrors()) {
            return new ModelAndView("registrationForm", "user", accountDTO);
        }
        try {
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));
        } catch (Exception ex) {
            return new ModelAndView("emailError", "user", accountDTO);
        }
        return new ModelAndView("registrationSuccessful", "user", accountDTO);
    }


    private AppUser createUserAccount(AppUserDTO accountDTO, BindingResult result) {
        AppUser registered = null;
        try {
            registered = service.registerNewUserAccount(accountDTO);
        } catch (EmailExistsException e) {
            return null;
        }
        return registered;
    }

    @GetMapping("/registrationConfirm")
    public String confirmRegistration(WebRequest request, Model model, @RequestParam("token") String token) {
        VerificationToken verificationToken = service.getVerificationToken(token);
        if (verificationToken == null) {
            String message = "Invalid token";
            model.addAttribute("message", message);
            // TODO: badUser view
            return "redirect:/badUser";
        }

        AppUser user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            String messageValue = "token expired";
            model.addAttribute("message", messageValue);
            return "redirect:/badUser";
        }

        user.setIsActivated(true);
        service.saveRegisteredUser(user);
        return "redirect:/emailConfirmed";
    }

    @GetMapping("/emailConfirmed")
    public String emailConfirmed() {
        return "redirect:/login?emailConfirmed=true";
    }

    @GetMapping("/resendActivationLink")
    public String resendActivationLink() {
        return "resendActivationLink";
    }

    @RequestMapping(value = "/resendActivationLink", method = RequestMethod.POST)
    public String resetPassword(@ModelAttribute("username") String email) {
        if (!service.userExists(email) || service.userActivated(email)) {
            return "redirect:/resendActivationLink?error=true";
        }
        service.resendActivationLink(email);
        return "redirect:/login?emailResent=true";
    }
}