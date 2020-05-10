package com.cscie599.gfn.controller;

import com.cscie599.gfn.entities.User;
import com.cscie599.gfn.service.EmailService;
import com.cscie599.gfn.service.UserService;
import com.cscie599.gfn.validator.UserValidator;
import com.cscie599.gfn.views.UserView;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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

    public static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * Retrieve current user profile.
     *
     * @param authentication Token for authentication request
     * @return UserView       User view containing user email, first name, last name
     */
    @ApiOperation(value = "Retrieve current user profile.")
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
     * Update current user profile including user email, first name, and last name.
     *
     * @param user             Request body representing user for update
     * @param authentication   Token for authentication request
     * @param bindingResult    BindingResult containing errors if any
     * @return ResponseEntity  ResponseEntity including user view and OK/Created or error
     */
    @ApiOperation(value = "Update current user profile including user email, first name, and last name.")
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
     * Change current user password.
     *
     * @param userPassword     Current user password
     * @param userPasswordNew  New user password
     * @param passwordConfirm  Confirm new user password
     * @param authentication   Token for authentication request
     * @return ResponseEntity  ResponseEntity including success or error
     */
    @ApiOperation(value = "Change current user password.")
    @PatchMapping("/profile")
    public ResponseEntity<?> changePassword(@RequestParam("userPassword") String userPassword, @RequestParam("userPasswordNew") String userPasswordNew, @RequestParam("passwordConfirm") String passwordConfirm, Authentication authentication) {
        HashMap<String, Object> response = new HashMap<>();
        User user = userService.findByUserEmail(authentication.getPrincipal().toString());
        if (!userService.checkIfValidOldPassword(user, userPassword)) {
            response.put("error", "Wrong current password.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        if (isNewPasswordInvalid(userPasswordNew, passwordConfirm, response))
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        userService.changeUserPassword(user, userPasswordNew);
        response.put("success", "Password changed.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Register new user.
     *
     * @param user            Request body representing user for update
     * @param bindingResult   BindingResult containing errors if any
     * @return ResponseEntity ResponseEntity including success or error
     */
    @ApiOperation(value = "Register new user.")
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
     * Send password reset link email to existing user email.
     *
     * @param userEmail User email to receive the password reset link
     * @param request   HttpServletRequest for sending email using URL
     * @return ResponseEntity ResponseEntity including OK regardless whether an email was sent or not
     */
    @ApiOperation(value = "Send password reset link email to existing user email.")
    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestParam("userEmail") String userEmail, HttpServletRequest request) {
        User user = userService.findByUserEmail(userEmail);
        // Add reset token to existing user and send password reset link email
        if (userService.isUserExist(user)) {
            user.setResetToken(UUID.randomUUID().toString());
            userService.update(user);
            String appUrl = request.getScheme() + "://" + request.getServerName();
            SimpleMailMessage passwordResetEmail = emailService.createEmail(user.getUserEmail(), "Password Reset Request",
                    "To reset your password, click the link: " + appUrl + "/reset?token=" + user.getResetToken());
            emailService.sendEmail(passwordResetEmail);
        }
        return new ResponseEntity<>("Please check your email for password reset link.", HttpStatus.OK);
    }

    /**
     * Check if token exists for user.
     *
     * @param token Token set for user email that forgot password
     * @return ResponseEntity ResponseEntity including OK or CONFLICT
     */
    @ApiOperation(value = "Check if token exists for user.")
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
     * Reset password.
     *
     * @param token           Token set for user email that forgot password
     * @param userPasswordNew New user password
     * @param passwordConfirm Confirm new user password
     * @return ResponseEntity ResponseEntity including success or failure
     */
    @ApiOperation(value = "Reset password.")
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token, @RequestParam("userPasswordNew") String userPasswordNew, @RequestParam("passwordConfirm") String passwordConfirm) {
        HashMap<String, Object> response = new HashMap<>();
        User user = userService.findUserByResetToken(token);
        // If the token exists for a user and new password is good, update user with new password and reset token to null
        if (userService.isPresent(user)) {
            if (isNewPasswordInvalid(userPasswordNew, passwordConfirm, response)) {
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            } else {
                userService.changeUserPassword(user, userPasswordNew);
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
     * Check if the new password is valid or not when updating.
     *
     * @param userPasswordNew New user password
     * @param passwordConfirm Confirm new user password
     * @param response        Response to be returned in the ResponseEntity
     * @return boolean        true if new password is invalid else false
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
}
