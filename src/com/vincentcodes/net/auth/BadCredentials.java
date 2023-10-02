package com.vincentcodes.net.auth;

public class BadCredentials extends Exception {
    public BadCredentials(String username, String pass){
        super("Invalid username or password: ['" + username + "', '" + pass + "']");
    }
}
