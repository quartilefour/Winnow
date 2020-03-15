/**
 * POST to /api/registration {
 * "username": "john_harvard@harvard.edu",
 * "password": "mysecret",
 * "firstname": "John",
 * "lastname": "Harvard"
 * }
 *
 * POST to /api/login {
 *     "username": "john_harvard@harvard.edu",
 *     "password": "mysecret"
 * }
 */
package com.cscie599.gfn.controller;

import com.cscie599.gfn.entities.User;
import com.cscie599.gfn.service.SecurityService;
import com.cscie599.gfn.service.UserService;
import com.cscie599.gfn.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userService.save(userForm);

        securityService.autoLogin(userForm.getUserEmail(), userForm.getPasswordConfirm());

        return "redirect:/welcome";
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