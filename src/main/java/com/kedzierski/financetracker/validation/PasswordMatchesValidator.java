package com.kedzierski.financetracker.validation;

import com.kedzierski.financetracker.dto.AppUserDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
        AppUserDTO user = (AppUserDTO) obj;
        return user.getPassword().equals(user.getMatchingPassword());
    }

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }
}
