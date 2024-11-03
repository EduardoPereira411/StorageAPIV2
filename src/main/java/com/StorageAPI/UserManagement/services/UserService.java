package com.StorageAPI.UserManagement.services;


import com.StorageAPI.UserManagement.api.RegisterAdminRequest;
import com.StorageAPI.UserManagement.api.RegisterRequest;
import com.StorageAPI.UserManagement.model.MyUser;
import com.StorageAPI.UserManagement.model.RefreshToken;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    MyUser createAdmin(RegisterAdminRequest request, MultipartFile photo);

    MyUser createUser(RegisterRequest request, MultipartFile photo);

    RefreshToken refreshAuthToken(RefreshToken newRefreshToken);

    Optional<RefreshToken> getRefreshToken(String tokenValue);
    MyUser findByUsername(String username);

    MyUser findByUserID(UUID userID);

    String getJWTToken(MyUser user);

    String getRefreshToken(MyUser user);
}
