package com.cscie599.gfn.service;

public interface SecurityService {
    String findLoggedInUsername();

    void autoLogin(String username, String password);
}