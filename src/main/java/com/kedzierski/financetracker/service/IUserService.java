package com.kedzierski.financetracker.service;

import com.kedzierski.financetracker.dto.AppUserDTO;
import com.kedzierski.financetracker.entity.AppUser;
import com.kedzierski.financetracker.entity.VerificationToken;
import com.kedzierski.financetracker.exception.EmailExistsException;

import java.util.List;

public interface IUserService {
    AppUser registerNewUserAccount(AppUserDTO accountDTO)
            throws EmailExistsException;

    AppUser getUser(String verificationToken);

    void saveRegisteredUser(AppUser user);

    void createVerificationToken(AppUser user, String token, boolean passwordReset);

    VerificationToken getVerificationToken(String VerificationToken);

    List<AppUser> getAllUsers();

    void deleteUser(long id);

    void blockUser(long id);

    void unblockUser(long id);

    void saveLastLogin(String email);

    void resetPassword(String email);

    void changePassword(String email, String password);

    void resendActivationLink(String email);

    String getUserEmail(long id);

    boolean userExists(String email);

    boolean userActivated (String email);
}
