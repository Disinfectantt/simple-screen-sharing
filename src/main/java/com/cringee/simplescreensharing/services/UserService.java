package com.cringee.simplescreensharing.services;

import com.cringee.simplescreensharing.dto.UserDto;
import com.cringee.simplescreensharing.models.Role;
import com.cringee.simplescreensharing.models.User;
import com.cringee.simplescreensharing.repos.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    UserService(UserRepo userRepo, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public UserDto findById(Long id) {
        User user = userRepo.findById(id).orElse(null);
        if (user == null) return null;
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(passwordEncoder.encode(user.getPassword()));
        userDto.setEnabled(user.getEnabled());
        return userDto;
    }

    public void save(UserDto userDto) {
        User user = UserDtoToUser(userDto);
        userRepo.save(user);
    }

    public void save(User user) {
        userRepo.save(user);
    }

    public void delete(Long id) {
        User user = userRepo.findById(id).orElse(null);
        if (user != null && !user.getUsername().equals("admin")) {
            userRepo.deleteById(id);
        }
    }

    public void update(Long id, UserDto userDto) {
        User user = userRepo.findById(id).orElse(null);
        if (user == null) return;
        user = UserDtoToUser(userDto);
        user.setId(id);
        userRepo.save(user);
    }

    public UserDto findByUsername(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        return userDto;
    }

    public User findByUsernameUser(String username) {
        return userRepo.findByUsername(username);
    }

    private User UserDtoToUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        if (userDto.getRoles() != null) {
            user.setRoles(new HashSet<>());
            for (var roleId : userDto.getRoles()) {
                Role role = roleService.findById(roleId);
                user.getRoles().add(role);
            }
        }
        return user;
    }

}
