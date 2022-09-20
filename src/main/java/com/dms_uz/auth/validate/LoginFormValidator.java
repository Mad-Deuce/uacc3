package com.dms_uz.auth.validate;

import com.dms_uz.auth.entity.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class LoginFormValidator implements ConstraintValidator<LoginForm, User> {

    @Override
    public boolean isValid(User user, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.disableDefaultConstraintViolation();

        return usernameCheckMinSize(user, constraintValidatorContext) &
                passwordCheckMinSize(user, constraintValidatorContext);
    }

    private boolean usernameCheckMinSize(User user, ConstraintValidatorContext constraintValidatorContext) {
        final int SIZE_MIN = 2;
        if (user.getUsername().length() < SIZE_MIN) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("Username value is too short")
                    .addPropertyNode("username")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean passwordCheckMinSize(User user, ConstraintValidatorContext constraintValidatorContext) {
        final int SIZE_MIN = 2;
        if (user.getPassword().length() < SIZE_MIN) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("Password value is too short")
                    .addPropertyNode("password")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

}
