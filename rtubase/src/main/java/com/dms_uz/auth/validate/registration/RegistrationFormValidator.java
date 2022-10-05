package com.dms_uz.auth.validate.registration;

import com.dms_uz.auth.entity.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class RegistrationFormValidator implements ConstraintValidator<RegistrationForm, User> {

    @Override
    public boolean isValid(User user, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.disableDefaultConstraintViolation();

        return usernameCheckMinSize(user, constraintValidatorContext) &
                passwordCheckMinSize(user, constraintValidatorContext) &
                passwordConfirmCheckMinSize(user, constraintValidatorContext) &
                passwordConfirm(user, constraintValidatorContext);
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

    private boolean passwordConfirmCheckMinSize(User user, ConstraintValidatorContext constraintValidatorContext) {
        final int SIZE_MIN = 2;
        if (user.getPasswordConfirm().length() < SIZE_MIN) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("PasswordConfirm value is too short")
                    .addPropertyNode("passwordConfirm")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean passwordConfirm(User user, ConstraintValidatorContext constraintValidatorContext) {
        if (!Objects.equals(user.getPassword(), user.getPasswordConfirm())){
            constraintValidatorContext.buildConstraintViolationWithTemplate("PasswordConfirm value not equal Password value")
                    .addPropertyNode("passwordConfirm")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

}

