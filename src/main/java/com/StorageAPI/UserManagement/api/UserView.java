package com.StorageAPI.UserManagement.api;

import com.StorageAPI.UserManagement.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserView {

    private UUID userId;

    private String username;

    private Set<Role> authorities;

    private String image;
}
