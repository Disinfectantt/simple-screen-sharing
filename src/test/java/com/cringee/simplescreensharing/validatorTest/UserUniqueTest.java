package com.cringee.simplescreensharing.validatorTest;

import com.cringee.simplescreensharing.dto.UserDto;
import com.cringee.simplescreensharing.models.User;
import com.cringee.simplescreensharing.services.UserService;
import com.cringee.simplescreensharing.validators.UserUniqueValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserUniqueTest {
    @Mock
    private UserService userService;

    @Mock
    private ConstraintValidatorContext context;

    private UserUniqueValidator userUniqueValidator;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        userService.save(user);
        userUniqueValidator = new UserUniqueValidator(userService);
    }

    @Test
    void newTest() {
        String username = "NewUsername";
        boolean result = userUniqueValidator.isValid(username, context);

        assertThat(result).isTrue();
    }

    @Test
    void existTest() {
        when(userService.findByUsername("username")).thenReturn(new UserDto());
        String username = "username";
        boolean result = userUniqueValidator.isValid(username, context);

        assertThat(result).isFalse();
    }

    @Test
    void nullTest() {
        boolean result = userUniqueValidator.isValid(null, context);

        assertThat(result).isFalse();
    }

}
