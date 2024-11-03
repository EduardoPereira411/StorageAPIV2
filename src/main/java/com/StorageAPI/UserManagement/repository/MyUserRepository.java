package com.StorageAPI.UserManagement.repository;

import com.StorageAPI.UserManagement.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MyUserRepository extends JpaRepository<MyUser, UUID> {

    Optional<MyUser> findByUsername(String username);
}
