package com.vincentcodes.net.auth;

public interface UserPassAuthenticator {
    /**
     * @return true if the user is authenticated
     */
    boolean authenticate(UserPassMessage message);
}
