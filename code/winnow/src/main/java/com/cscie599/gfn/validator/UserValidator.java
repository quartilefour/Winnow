package com.cscie599.gfn.validator;

import com.cscie599.gfn.entities.User;
import com.cscie599.gfn.service.UserService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        // Validate user email
        if (user.getUserEmail()==null) {
            errors.rejectValue("userEmail", "Email is required.");
        }
        else {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userEmail", "Email is required.");
            if (!(EmailValidator.getInstance().isValid(user.getUserEmail()))) {
                errors.rejectValue("userEmail", "Invalid email, please try again.");
            }
            if (user.getUserEmail().length() < 6 || user.getUserEmail().length() > 254) {
                errors.rejectValue("userEmail", "Email must be between 6 to 254 characters.");
            }
            if (userService.isUserExist(user)) {
                errors.rejectValue("userEmail", "Email already exists.");
            }
        }

        // Validate user password
        if (user.getUserPassword()==null) {
            errors.rejectValue("userEmail", "Password is required.");
        }
        else {
            ValidationUtils.rejectIfEmpty(errors, "userPassword", "Password is required.");
            if (user.getUserPassword().length() < 8 || user.getUserPassword().length() > 64) {
                errors.rejectValue("userPassword", "Password must be between 8 and 64 characters.");
            }
            if (user.getPasswordConfirm()==null || !user.getPasswordConfirm().equals(user.getUserPassword())) {
                errors.rejectValue("passwordConfirm", "Password does not match.");
            }
        }

        // Validate user first name
        if (user.getFirstName()==null) {
            errors.rejectValue("userEmail", "First name is required.");
        }
        else {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "First name is required.");
            if (user.getFirstName().length() < 1 || user.getFirstName().length() > 300) {
                errors.rejectValue("firstName", "First name must be between 1 and 300 characters.");
            }
        }

        // Validate user last name
        if (user.getLastName()==null) {
            errors.rejectValue("userEmail", "Last name is required.");
        }
        else {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "Last name is required.");
            if (user.getLastName().length() < 1 || user.getLastName().length() > 300) {
                errors.rejectValue("lastName", "Last name must be between 1 and 300 characters.");
            }
        }
    }
}