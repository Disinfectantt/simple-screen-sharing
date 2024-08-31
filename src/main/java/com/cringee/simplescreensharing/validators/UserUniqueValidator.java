package com.cringee.simplescreensharing.validators;

import com.cringee.simplescreensharing.annotations.UsernameUnique;
import com.cringee.simplescreensharing.services.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserUniqueValidator implements ConstraintValidator<UsernameUnique, String> {

    private final UserService userService;

    public UserUniqueValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(String teamNameField,
                           ConstraintValidatorContext cxt) {
        return teamNameField != null && userService.findByUsername(teamNameField) == null;
    }

}
