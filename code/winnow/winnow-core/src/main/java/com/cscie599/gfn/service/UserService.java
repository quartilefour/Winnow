package com.cscie599.gfn.service;

import com.cscie599.gfn.entities.User;

public interface UserService {

    void save(User user);

    void update(User user);

    User findByUserEmail(String userEmail);

    boolean isUserExist(User user);

    void changeUserPassword(final User user, final String password);

    boolean checkIfValidOldPassword(final User user, final String oldPassword);
}