package com.cscie599.gfn.controller;

import com.cscie599.gfn.entities.User;
import com.cscie599.gfn.service.EmailService;
import com.cscie599.gfn.service.UserService;
import com.cscie599.gfn.validator.UserValidator;
import com.cscie599.gfn.views.UserView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private EmailService emailService;

    @Value("${email.forgot.password}")
    private String forgotPasswordEmail;

    @Value("${url.application}")
    private String appUrl;

    public static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * Gets the current user's profile.
     *
     * @param authentication Authentication token
     * @return User view containing user email, first name, last name
     */
    @GetMapping("/profile")
    public UserView getProfile(Authentication authentication) {
        String userEmail = authentication.getPrincipal().toString();
        User user = userService.findByUserEmail(userEmail);
        return new UserView(
                user.getUserEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    /**
     * Updates user profile.
     *
     * @param user           RequestBody containing user email, first name, and last name
     * @param authentication Authentication token
     * @param bindingResult  BindingResult containing errors if any
     * @return ResponseEntity containing user view or error
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody User user, Authentication authentication, BindingResult bindingResult) {
        // Validate the request body before proceeding with the request
        userValidator.validateUpdateProfile(user, bindingResult);
        if (bindingResult.hasErrors()) {
            HashMap<String, String> error = new HashMap<>();
            error.put("error", bindingResult.getFieldError().getCode());
            return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }
        // Update the user email, first name, and last name
        else {
            String userEmail = authentication.getPrincipal().toString();
            User updateUser = userService.findByUserEmail(userEmail);
            updateUser.setUserEmail(user.getUserEmail());
            updateUser.setFirstName(user.getFirstName());
            updateUser.setLastName(user.getLastName());
            userService.update(updateUser);
            UserView userView = new UserView(
                    updateUser.getUserEmail(),
                    updateUser.getFirstName(),
                    updateUser.getLastName()
            );
            // If the user email is updated, send HTTP Status 201 Created instead
            if (updateUser.getUserEmail().equals(userEmail)) {
                return new ResponseEntity<>(userView, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(userView, HttpStatus.CREATED);
            }
        }
    }

    /**
     * Changes the user's current password.
     *
     * @param body           RequestBody containing user password, new password, and confirm password
     * @param authentication Authentication token
     * @return ResponseEntity containing success or error
     */
    @PatchMapping("/profile")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body, Authentication authentication) {
        String[] parameters = {"userPassword", "userPasswordNew", "passwordConfirm"};
        HashMap<String, Object> response = validate(body, parameters);
        if (response.containsKey("error")) {
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        User user = userService.findByUserEmail(authentication.getPrincipal().toString());
        if (!userService.checkIfValidOldPassword(user, body.get("userPassword"))) {
            response.put("error", "Wrong current password.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        if (isNewPasswordInvalid(body.get("userPasswordNew"), body.get("passwordConfirm"), response))
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        userService.changeUserPassword(user, body.get("userPasswordNew"));
        response.put("success", "Password changed.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Registers a new user with first name, last name, and email address.
     *
     * @param user          RequestBody containing user email, password, confirm password, first name, and last name
     * @param bindingResult BindingResult containing errors if any
     * @return ResponseEntity containing success or error
     */
    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody User user, BindingResult bindingResult) {
        // Validate the request body before proceeding with the request
        userValidator.validateRegistration(user, bindingResult);
        if (bindingResult.hasErrors()) {
            HashMap<String, String> error = new HashMap<>();
            error.put("error", bindingResult.getFieldError().getCode());
            return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }
        // Register the user
        else {
            userService.save(user);
            HashMap<String, String> success = new HashMap<>();
            success.put("success", "Registration completed.");
            return new ResponseEntity<>(success, HttpStatus.CREATED);
        }
    }

    /**
     * Sends email with password reset link to the email address.
     *
     * @param body RequestBody containing user email
     * @return ResponseEntity containing success regardless whether an email was sent or not
     */
    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String[] parameters = {"userEmail"};
        HashMap<String, Object> response = validate(body, parameters);
        if (response.containsKey("error")) {
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        User user = userService.findByUserEmail(body.get("userEmail"));
        // Add reset token to existing user and send password reset link email
        if (userService.isPresent(user)) {
            user.setResetToken(UUID.randomUUID().toString());
            userService.update(user);
            SimpleMailMessage passwordResetEmail = new SimpleMailMessage();
            passwordResetEmail.setFrom(forgotPasswordEmail);
            passwordResetEmail.setTo(user.getUserEmail());
            passwordResetEmail.setSubject("Password Reset Request");
            passwordResetEmail.setText("To reset your password, click the link: " + appUrl + "/reset?token=" + user.getResetToken());
            emailService.sendEmail(passwordResetEmail);
        }
        response.put("success", "Please check your email for password reset link.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Gets the HTTP status of password reset link including a token.
     *
     * @param token Token set for user email that forgot password
     * @return ResponseEntity containing OK or CONFLICT
     */
    @GetMapping("/reset")
    public ResponseEntity<?> checkPasswordResetLink(@RequestParam("token") String token) {
        User user = userService.findUserByResetToken(token);
        // If the token exists for a user, send HTTP Status OK else CONFLICT
        if (userService.isPresent(user)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Resets the user's current password with the new password based on the token.
     *
     * @param body Request body containing token, new user password, and confirm password
     * @return ResponseEntity containing success or error
     */
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String[] parameters = {"token", "userPasswordNew", "passwordConfirm"};
        HashMap<String, Object> response = validate(body, parameters);
        if (response.containsKey("error")) {
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        User user = userService.findUserByResetToken(body.get("token"));
        // If the token exists for a user and new password is good, update user with new password and reset token to null
        if (userService.isPresent(user)) {
            if (isNewPasswordInvalid(body.get("userPasswordNew"), body.get("passwordConfirm"), response)) {
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            } else {
                userService.changeUserPassword(user, body.get("userPasswordNew"));
                user.setResetToken(null);
                userService.update(user);
                response.put("success", "Password reset.");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } else {
            response.put("error", "Invalid password reset link.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    }

    /**
     * Checks if the new password is valid or not when updating.
     *
     * @param userPasswordNew New user password
     * @param passwordConfirm Confirm password
     * @param response        Response returned within the ResponseEntity
     * @return Boolean true if new password is invalid else false
     */
    private boolean isNewPasswordInvalid(String userPasswordNew, String passwordConfirm, HashMap<String, Object> response) {
        if (userPasswordNew.length() < 8 || userPasswordNew.length() > 60) {
            response.put("error", "Password must be between 8 and 60 characters.");
            return true;
        } else if (!userPasswordNew.equals(passwordConfirm)) {
            response.put("error", "Password does not match.");
            return true;
        }
        return false;
    }

    /**
     * Validates request body for required parameters.
     *
     * @param body       Request body
     * @param parameters Required parameters
     * @return Response containing error if any
     */
    private HashMap<String, Object> validate(Map<String, String> body, String[] parameters) {
        HashMap<String, Object> response = new HashMap<>();
        for (String parameter : parameters) {
            if (!(body.containsKey(parameter))) {
                response.put("error", "Missing " + parameter + ".");
            }
        }
        return response;
    }
}
