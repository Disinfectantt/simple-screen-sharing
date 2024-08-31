package com.cringee.simplescreensharing.serviceTest;

import com.cringee.simplescreensharing.dto.UserDto;
import com.cringee.simplescreensharing.models.User;
import com.cringee.simplescreensharing.repos.UserRepo;
import com.cringee.simplescreensharing.services.RoleService;
import com.cringee.simplescreensharing.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepo userRepo;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("encodedPassword");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testUserDto");
        userDto.setPassword("password");
    }

    @Test
    void testFindByUsername() {
        when(userRepo.findByUsername("testUser")).thenReturn(user);

        UserDto result = userService.findByUsername("testUser");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testUser");
    }

    @Test
    void testFindById() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void testFindByUsernameUser() {
        when(userRepo.findByUsername("testUser")).thenReturn(user);

        User result = userService.findByUsernameUser("testUser");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testUser");
    }

    @Test
    void testSaveUser() {
        userService.save(user);

        verify(userRepo).save(user);
    }

    @Test
    void testSaveUserDto() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        userService.save(userDto);

        verify(userRepo).save(argThat(savedUser ->
                savedUser.getUsername().equals("testUserDto") &&
                        savedUser.getPassword().equals("encodedPassword")
        ));
    }

    @Test
    void testUpdate() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("oldUsername");
        existingUser.setPassword("oldPassword");

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setId(userId);
        updatedUserDto.setUsername("newUsername");
        updatedUserDto.setPassword("newPassword");

        when(userRepo.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        userService.update(userId, updatedUserDto);

        verify(userRepo).save(argThat(savedUser ->
                savedUser.getId().equals(userId) &&
                        savedUser.getUsername().equals("newUsername") &&
                        savedUser.getPassword().equals("encodedNewPassword")
        ));
    }

    @Test
    void testDelete() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepo).deleteById(1L);
    }

    @Test
    void testDeleteAdmin() {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        when(userRepo.findById(1L)).thenReturn(Optional.of(adminUser));

        userService.delete(1L);

        verify(userRepo, never()).deleteById(1L);
    }

    @Test
    void testFindAll() {
        List<User> users = Arrays.asList(user, new User());
        when(userRepo.findAll()).thenReturn(users);

        List<User> result = userService.findAll();

        assertThat(result).hasSize(2);
    }

}
