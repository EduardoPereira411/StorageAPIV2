package com.StorageAPI.UserManagement.api;


import com.StorageAPI.UserManagement.model.MyUser;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserViewMapper {
    public abstract UserView toUserView(MyUser user);

    public abstract List<UserView> toUserView(List<MyUser> users);
}
