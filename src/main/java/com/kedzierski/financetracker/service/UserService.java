package com.kedzierski.financetracker.service;

import com.kedzierski.financetracker.dto.AppUserDTO;
import com.kedzierski.financetracker.entity.AppUser;
import com.kedzierski.financetracker.entity.VerificationToken;
import com.kedzierski.financetracker.exception.EmailExistsException;
import com.kedzierski.financetracker.repository.AppUserRepository;
import com.kedzierski.financetracker.repository.VerificationTokenRepository;
import com.kedzierski.financetracker.utils.EncryptedPasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements IUserService{

    @Autowired
    private AppUserRepository repository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private IEmailService emailService;

    @Override
    public AppUser registerNewUserAccount(AppUserDTO accountDTO) throws EmailExistsException {
        if (emailExists(accountDTO.getEmail())) {
            throw new EmailExistsException (
                    "There is an account with that email address: "
                            + accountDTO.getEmail());
        }
        AppUser user = new AppUser(accountDTO.getEmail(),
                EncryptedPasswordUtils.encryptPassword(accountDTO.getPassword()));
        try {
            return repository.save(user);
        } catch (Exception ex) {
            System.out.println(ex.getStackTrace());
            System.out.println(ex.getMessage());
            return null;
        }

    }

    private boolean emailExists(String email) {
        AppUser user = repository.findByUserEmail(email);
        if (user != null) {
            return true;
        }
        return false;
    }

    public void saveRegisteredUser(AppUser user) {
        try {
            repository.save(user);
        } catch (Exception ex) {
            System.out.println(ex.getStackTrace());
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public AppUser getUser(String verificationToken) {
        AppUser user = tokenRepository.findByToken(verificationToken).getUser();
        return user;
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        VerificationToken token = tokenRepository.findByToken(VerificationToken);
        return token;
    }

    @Override
    public void createVerificationToken(AppUser user, String token, boolean passwordReset) {
        VerificationToken myToken = new VerificationToken(token, user, passwordReset);
        tokenRepository.save(myToken);
    }

    @Override
    public List<AppUser> getAllUsers() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "userId"));
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        AppUser user = repository.findByUserId(id);
        tokenRepository.deleteByUser(user);
        repository.delete(user);
    }

    @Override
    public void blockUser(long id) {
        AppUser user = repository.findByUserId(id);
        user.setBlocked(true);
        repository.save(user);
    }

    @Override
    public void unblockUser(long id) {
        AppUser user = repository.findByUserId(id);
        user.setBlocked(false);
        repository.save(user);
    }

    @Override
    public void saveLastLogin(String email) {
        AppUser user = repository.findByUserEmail(email);
        user.setLastLogin(new Timestamp(System.currentTimeMillis()));
        repository.save(user);
    }

    @Override
    public void resetPassword(String email) {
        AppUser user = repository.findByUserEmail(email);
        String token = UUID.randomUUID().toString();
        createVerificationToken(user, token, true);
        String url = "/changePassword?token=";
        emailService.sendResetPasswordMessage(user, token, url);
    }

    @Override
    public void changePassword(String email, String password) {
        AppUser user = repository.findByUserEmail(email);
        user.setEncryptedPassword(EncryptedPasswordUtils.encryptPassword(password));
        repository.save(user);
    }

    @Override
    public void resendActivationLink(String email) {
        AppUser user = repository.findByUserEmail(email);
        String token = UUID.randomUUID().toString();
        createVerificationToken(user, token, false);
        //TODO: what is event.getAppUrl()?
        String url = "/registrationConfirm?token=";
        emailService.sendEmailConfirmationMessage(user, token, url);
    }

    @Override
    public String getUserEmail(long id) {
        AppUser user = repository.findByUserId(id);
        return user.getUserEmail();
    }

    @Override
    public boolean userExists(String email) {
        AppUser user = repository.findByUserEmail(email);
        return user != null;
    }

    @Override
    public boolean userActivated(String email) {
        AppUser user = repository.findByUserEmail(email);
        return user.isActivated();
    }
}
