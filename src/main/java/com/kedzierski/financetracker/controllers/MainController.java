package com.kedzierski.financetracker.controllers;

import com.kedzierski.financetracker.components.SessionComponent;
import com.kedzierski.financetracker.dto.AppUserDTO;
import com.kedzierski.financetracker.entity.AppUser;
import com.kedzierski.financetracker.entity.VerificationToken;
import com.kedzierski.financetracker.service.IUserService;
import com.kedzierski.financetracker.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Calendar;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private IUserService userService;

    @Autowired
    private SessionComponent sessionComponent;

    @RequestMapping(value = { "/", "/welcome" }, method = RequestMethod.GET)
    public String welcomePage(Model model) {
        model.addAttribute("title", "Welcome");
        model.addAttribute("message", "This is welcome page!");
        return "welcomePage";
    }

    @RequestMapping(value = "/delete/{id}")
    public String deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminPage(Model model, Principal principal) {

        User loggedUser = (User) ((Authentication) principal).getPrincipal();

        String userInfo = WebUtils.toString(loggedUser);

        List<AppUser> users = userService.getAllUsers();

        model.addAttribute("users", users);
        model.addAttribute("userInfo", userInfo);

        return "adminPage";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(Model model) {
        return "loginPage";
    }

    @RequestMapping(value = "/logoutSuccessful", method = RequestMethod.GET)
    public String logoutSuccessfulPage(Model model) {
        model.addAttribute("title", "Logout");
        return "logoutSuccessfulPage";
    }

    @RequestMapping(value = "/userInfo", method = RequestMethod.GET)
    public String userInfo(HttpServletRequest request, Model model, Principal principal) {

        System.out.println("Scoped SessionComponent page views: " + sessionComponent.getPageViews());

        int pageViews = 1;
        if (request.getSession().getAttribute("pageViews") != null) {
            pageViews += (int) request.getSession().getAttribute("pageViews");
        }

        sessionComponent.setPageViews(pageViews);
        request.getSession().setAttribute("pageViews", pageViews);

        // After user login successfully.
        String userName = principal.getName();

        System.out.println("User Name: " + userName);

        User loggedUser = (User) ((Authentication) principal).getPrincipal();

        String userInfo = WebUtils.toString(loggedUser);
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("pageViews", pageViews);

        return "userInfoPage";
    }

    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public String accessDenied(Model model, Principal principal) {

        if (principal != null) {
            User loggedUser = (User) ((Authentication) principal).getPrincipal();

            String userInfo = WebUtils.toString(loggedUser);

            model.addAttribute("userInfo", userInfo);

            String message = "Hi " + principal.getName() //
                    + "<br> You do not have permission to access this page!";
            model.addAttribute("message", message);

        }

        return "403Page";
    }

    @RequestMapping(value = "/block/{id}")
    public String blockUser(@PathVariable long id) {
        userService.blockUser(id);
        return "redirect:/admin";
    }

    @RequestMapping(value = "/unblock/{id}")
    public String unblockUser(@PathVariable long id) {
        userService.unblockUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/loginSuccessful")
    public String loginSuccessful(Principal principal) {
        User loggedUser = (User) ((Authentication) principal).getPrincipal();
        userService.saveLastLogin(loggedUser.getUsername());
        return "redirect:/userInfo";
    }

    @GetMapping("/resetPassword")
    public String resetPassword() {
        return "resetPasswordForm";
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public String resetPassword(@ModelAttribute("username") String email) {
        userService.resetPassword(email);
        return "redirect:/resetPassword?emailSent=true";
    }

    @GetMapping("/changePassword/{id}")
    public String resetPassword(@PathVariable("id") long id, Model model) {

        String userEmail = userService.getUserEmail(id);
        AppUserDTO userDTO = new AppUserDTO();
        userDTO.setEmail(userEmail);
        Calendar cal = Calendar.getInstance();

        model.addAttribute("user", userDTO);

        return "changePasswordForm";
    }

    @GetMapping("/changePassword")
    public String resetPassword(Model model, @RequestParam("token") String token) {

        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null || !verificationToken.isPasswordReset()) {
            String message = "Invalid token";
            model.addAttribute("message", message);
            // TODO: badToken view
            return "redirect:/badToken";
        }

        AppUser user = verificationToken.getUser();
        AppUserDTO userDTO = new AppUserDTO();
        userDTO.setEmail(user.getUserEmail());
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            String messageValue = "token expired";
            model.addAttribute("message", messageValue);
            return "redirect:/badToken";
        }

        model.addAttribute("user", userDTO);
        model.addAttribute("token", token);

        return "changePasswordForm";
    }

    @PostMapping("/changePassword")
    public String changePassword(@ModelAttribute("user") @Valid AppUserDTO user, BindingResult result,
                                 HttpServletRequest request, Errors errors) {

        if (result.hasErrors()) {
            return "redirect:/resetPassword";
        }

        userService.changePassword(user.getEmail(), user.getPassword());

        return "redirect:/login?passwordChanged=true";

    }
}