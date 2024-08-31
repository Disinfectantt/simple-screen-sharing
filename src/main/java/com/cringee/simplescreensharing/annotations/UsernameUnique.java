package com.cringee.simplescreensharing.annotations;

import com.cringee.simplescreensharing.validators.UserUniqueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserUniqueValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UsernameUnique {
    String message() default "User already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
