package com.StorageAPI.UserManagement.api;

import lombok.Data;

@Data
public class LoginResponse {

    private String access_token;

    private String refresh_token;

    public LoginResponse(String access_token, String refresh_token) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }
}
