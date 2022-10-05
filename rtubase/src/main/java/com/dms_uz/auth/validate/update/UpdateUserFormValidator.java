package com.dms_uz.auth.validate.update;

import com.dms_uz.auth.dto.UserDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class UpdateUserFormValidator implements ConstraintValidator<UpdateUserForm, UserDTO> {

    @Override
    public boolean isValid(UserDTO userDTO, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.disableDefaultConstraintViolation();

        return usernameCheckMinSizeOrNull(userDTO, constraintValidatorContext) &
                passwordCheckMinSize(userDTO, constraintValidatorContext) &
                passwordConfirmCheckMinSize(userDTO, constraintValidatorContext) &
                passwordConfirm(userDTO, constraintValidatorContext);
    }

    private boolean usernameCheckMinSizeOrNull(UserDTO userDTO, ConstraintValidatorContext constraintValidatorContext) {
        final int SIZE_MIN = 2;
        if (userDTO.getUsername() != null && userDTO.getUsername().length() < SIZE_MIN) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("Username value is too short")
                    .addPropertyNode("username")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean passwordCheckMinSize(UserDTO userDTO, ConstraintValidatorContext constraintValidatorContext) {
        final int SIZE_MIN = 2;
        if (userDTO.getPassword() != null && userDTO.getPassword().length() < SIZE_MIN) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("Password value is too short")
                    .addPropertyNode("password")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean passwordConfirmCheckMinSize(UserDTO userDTO, ConstraintValidatorContext constraintValidatorContext) {
        final int SIZE_MIN = 2;
        if (userDTO.getPasswordConfirm() != null && userDTO.getPasswordConfirm().length() < SIZE_MIN) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("PasswordConfirm value is too short")
                    .addPropertyNode("passwordConfirm")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean passwordConfirm(UserDTO userDTO, ConstraintValidatorContext constraintValidatorContext) {
        if (!Objects.equals(userDTO.getPassword(), userDTO.getPasswordConfirm())) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("PasswordConfirm value not equal Password value")
                    .addPropertyNode("passwordConfirm")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

}
