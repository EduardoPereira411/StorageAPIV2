package com.StorageAPI.UserManagement.api;


import java.util.Set;

public record RegisterAdminRequest (String username, String password, String rePassword, Set<String> roles){

}