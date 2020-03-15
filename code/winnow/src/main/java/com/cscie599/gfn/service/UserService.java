package com.cscie599.gfn.service;

import com.cscie599.gfn.entities.User;

public interface UserService {
    void save(User user);

    User findByUserEmail(String userEmail);
    boolean isUserExist(User user);
}