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

import com.cscie599.gfn.entities.User;
import com.cscie599.gfn.service.SecurityService;
import com.cscie599.gfn.service.UserService;
import com.cscie599.gfn.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import java.util.HashMap;
import io.swagger.annotations.Api;
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

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        return "registration";
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

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }

    @GetMapping({"/", "/welcome"})
    public String welcome(Model model) {
        return "welcome";
    }
}