package com.cscie599.gfn.service;

import com.cscie599.gfn.entities.User;
import com.cscie599.gfn.repository.RoleRepository;
import com.cscie599.gfn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>(roleRepository.findAll()));
        userRepository.saveAndFlush(user);
    }


    public User findByUserEmail(String userEmail) {
        return userRepository.findByUserEmail(userEmail);
    }


    public boolean isUserExist(User user) {
        return findByUserEmail(user.getUserEmail())!=null;
    }
}