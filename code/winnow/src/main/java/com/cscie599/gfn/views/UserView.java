package com.cscie599.gfn.views;

public class UserView {
    private String userEmail;
    private String firstName;
    private String lastName;

    public UserView(String userEmail, String firstName, String lastName) {
        this.userEmail = userEmail;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
