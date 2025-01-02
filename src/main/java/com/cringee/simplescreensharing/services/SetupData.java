package com.cringee.simplescreensharing.services;

import com.cringee.simplescreensharing.models.Role;
import com.cringee.simplescreensharing.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SetupData implements CommandLineRunner {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role("ROLE_ADMIN"));
        if (roleService.count() == 0) {
            for (var role : roles) {
                roleService.save(role);
            }
        }

        User defaultUser = userService.findByUsernameUser("admin");
        if (defaultUser == null) {
            defaultUser = new User();
            defaultUser.setUsername("admin");
            defaultUser.setPassword(passwordEncoder.encode("admin"));
            defaultUser.setEnabled(true);
            defaultUser.setRoles(roles);
            userService.save(defaultUser);
        }
    }
}
