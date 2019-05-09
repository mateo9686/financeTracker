package com.kedzierski.financetracker.entity;

import com.kedzierski.financetracker.validation.ValidEmail;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "App_User", //
        uniqueConstraints = { //
                @UniqueConstraint(name = "APP_USER_UK", columnNames = "User_Email") })
public class AppUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "User_Id", nullable = false)
    private Long userId;

    @ValidEmail
    @Column(name = "User_Email", length = 50, nullable = false)
    private String userEmail;

    @Column(name = "Encrypted_Password", length = 128, nullable = false)
    private String encryptedPassword;

    @Column(name = "Last_Login")
    private Timestamp lastLogin;

    @Column(name = "Is_Admin", nullable = false)
    private boolean isAdmin;

    @Column(name = "Is_Activated", nullable = false)
    private boolean isActivated;

    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;

    public AppUser() {}

    public AppUser(String userEmail, String encryptedPassword) {
        this.userEmail = userEmail;
        this.encryptedPassword = encryptedPassword;
        this.isAdmin = false;
        this.lastLogin = null;
        this.isActivated = false;
        this.isBlocked = false;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setIsActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }

    public boolean isAdmin() { return isAdmin; }

    public void setIsAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }

    public Timestamp getLastLogin() { return this.lastLogin; }

    public void setLastLogin(Timestamp lastLogin) { this.lastLogin = lastLogin; }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}