package com.cscie599.gfn.service;

import com.cscie599.gfn.controller.UserController;
import com.cscie599.gfn.entities.User;
import com.cscie599.gfn.repository.RoleRepository;
import com.cscie599.gfn.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;

@Service
public class UserServiceImpl implements UserService {

    public static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void save(User user) {
        user.setUserPassword(bCryptPasswordEncoder.encode(user.getUserPassword()));
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setRoles(new HashSet<>(roleRepository.findAll()));
        userRepository.saveAndFlush(user);
    }

    public void update(User user) {
        user.setUpdatedAt(new Date());
        user.setRoles(new HashSet<>(roleRepository.findAll()));
        userRepository.saveAndFlush(user);
    }

    public User findByUserEmail(String userEmail) {
        return userRepository.findByUserEmail(userEmail);
    }

    public boolean isUserExist(User user) {
        return findByUserEmail(user.getUserEmail())!=null;
    }

    public boolean isPresent(User user) {
        return user!=null;
    }

    public void changeUserPassword(final User user, final String password) {
        user.setUserPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
    }

    public boolean checkIfValidOldPassword(final User user, final String oldPassword) {
        return bCryptPasswordEncoder.matches(oldPassword, user.getUserPassword());
    }

    public User findUserByResetToken(String resetToken) {
        return userRepository.findByResetToken(resetToken);
    }

}