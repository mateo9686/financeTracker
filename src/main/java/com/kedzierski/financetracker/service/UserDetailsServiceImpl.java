package com.kedzierski.financetracker.service;

import com.kedzierski.financetracker.entity.AppUser;
import com.kedzierski.financetracker.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AppUserRepository repository;

    @Autowired
    private ILoginAttemptService loginAttemptService;

    @Autowired
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        String ip = getClientIp();
        if (loginAttemptService.isBlocked(ip)) {
            throw new RuntimeException("blocked");
        }


        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;

        try {
            AppUser appUser = repository.findByUserEmail(userName);

            if (appUser == null) {
                System.out.println("User not found! " + userName);
                throw new UsernameNotFoundException("User " + userName + " was not found in the database");
            }

            System.out.println("Found User: " + appUser);
            System.out.println(appUser.getUserEmail());
            System.out.println(appUser.getEncryptedPassword());
            System.out.println(appUser.isAdmin());

            // [ROLE_USER, ROLE_ADMIN,..]
            //boolean isAdmin = this.appUserDAO.isAdmin(appUser.getUserEmail());

            List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
            GrantedAuthority userRole = new SimpleGrantedAuthority("ROLE_USER");
            grantList.add(userRole);
            if (appUser.isAdmin()) {
                GrantedAuthority adminRole = new SimpleGrantedAuthority("ROLE_ADMIN");
                grantList.add(adminRole);
            }

            return new User(
                    appUser.getUserEmail(),
                    appUser.getEncryptedPassword(),
                    appUser.isActivated(),
                    accountNonExpired,
                    credentialsNonExpired,
                    !appUser.isBlocked(),
                    grantList);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private String getClientIp() {
        String xfHeader = request.getHeader("X-Forwarder-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

}