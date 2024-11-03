package com.StorageAPI.UserManagement.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Value
@AllArgsConstructor
public class Role implements GrantedAuthority {

    private static final long serialVersionUID = 1L;

    public static final String ADMIN = "ADMIN";

    public static final String USER = "USER";

    private String authority;

    public static boolean isValidAdminRole(String role) {
        // Check if the role matches any of the predefined roles
        return role.equals(ADMIN) || role.equals(USER);
    }

    public static Set<Role> stringToRole(final Set<String> authorities) {
        if (authorities != null) {
            return authorities.stream().map(Role::new).collect(toSet());
        }
        return new HashSet<>();
    }

}
