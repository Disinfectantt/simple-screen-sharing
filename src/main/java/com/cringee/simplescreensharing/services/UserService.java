package com.cringee.simplescreensharing.services;

import com.cringee.simplescreensharing.dto.UserDto;
import com.cringee.simplescreensharing.mapper.UserMapper;
import com.cringee.simplescreensharing.models.User;
import com.cringee.simplescreensharing.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public UserDto findById(Long id) {
        User user = userRepo.findById(id).orElse(null);
        if (user == null) return null;
        return UserMapper.INSTANCE.toUserDto(user);
    }

    public void save(UserDto userDto) {
        User user = UserMapper.INSTANCE.toUser(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
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
        user = UserMapper.INSTANCE.toUser(userDto);
        user.setId(id);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepo.save(user);
    }

    public UserDto findByUsername(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            return null;
        }
        return UserMapper.INSTANCE.toUserDto(user);
    }

    public User findByUsernameUser(String username) {
        return userRepo.findByUsername(username);
    }

}
