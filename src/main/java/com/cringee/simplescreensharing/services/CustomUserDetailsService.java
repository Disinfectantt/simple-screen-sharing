package com.cringee.simplescreensharing.services;

import com.cringee.simplescreensharing.CustomUserDetails;
import com.cringee.simplescreensharing.models.User;
import com.cringee.simplescreensharing.repos.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public CustomUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomUserDetails(user);
    }
}
