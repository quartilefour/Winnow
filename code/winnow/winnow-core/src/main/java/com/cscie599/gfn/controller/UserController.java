/**
 * POST to /api/registration {
 * "userEmail": "john_harvard@harvard.edu",
 * "userPassword": "mysecret",
 * "passwordConfirm": "mysecret",
 * "firstName": "John",
 * "lastName": "Harvard"
 * }
 *
 * POST to /api/login {
 *     "userEmail": "john_harvard@harvard.edu",
 *     "userPassword": "mysecret"
 * }
 */
package com.cscie599.gfn.controller;

import com.cscie599.gfn.validator.UserValidator;
import com.cscie599.gfn.entities.User;
import com.cscie599.gfn.service.SecurityService;
import com.cscie599.gfn.service.UserService;
import com.cscie599.gfn.views.UserView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.validation.BindingResult;

import java.util.HashMap;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

    public static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @ApiOperation(value = "Retrieve current user profile")
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
     * Update user profile (password not included - will be separate method (PATCH)
     *
     * This doesn't work yet, needs to retrieve fields from @RequestBody, validate
     * data, set User properties, save User, then return new UserView with
     * updated properties or error.
     *
     * @param authentication
     * @return
     */
    @ApiOperation(value = "Update current user profile")
    @PutMapping("/profile")
    public UserView updateProfile(Authentication authentication) {
        String userEmail = authentication.getPrincipal().toString();
        User user = userService.findByUserEmail(userEmail);
        return new UserView(
                user.getUserEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    /**
     * Change user password
     *
     * This doesn't work yet, needs to retrieve fields from @RequestBody,
     * validate data, set User password, save User, then return OK or error.
     *
     * @param authentication
     * @return
     */
    @ApiOperation(value = "Change current user password")
    @PatchMapping("/profile")
    public UserView changePassword(Authentication authentication) {
        String userEmail = authentication.getPrincipal().toString();
        User user = userService.findByUserEmail(userEmail);
        return new UserView(
                user.getUserEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    @ApiOperation(value = "Register new user")
    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody User user, UriComponentsBuilder uriComponentsBuilder,
                                          BindingResult bindingResult) {
        logger.info("Hitting API registration route with User: " + user);

        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            HashMap<String, String> error = new HashMap<>();
            error.put("error", bindingResult.getFieldError().getCode());
            return new ResponseEntity(error, HttpStatus.CONFLICT);
        }
        else {
            userService.save(user);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(uriComponentsBuilder.path("/api/user/{id}").buildAndExpand(user.getUserId()).toUri());
            return new ResponseEntity<String>(headers, HttpStatus.CREATED);
        }
    }
}