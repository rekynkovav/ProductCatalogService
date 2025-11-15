package org.example.config;

import org.example.model.entity.Role;
import org.example.model.entity.User;

public interface UserSecurityConfig {
    boolean verificationUser(String userName, String password);
    void registerUser(String userName, String password, Role role);
    void setThisUser(User thisUser);
    boolean isAuthenticated();
}
