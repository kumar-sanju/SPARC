package com.smart.sparc;

public class LoginData {
    private String userName;
    private String password;

    public LoginData(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    // Getters and setters if needed
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

