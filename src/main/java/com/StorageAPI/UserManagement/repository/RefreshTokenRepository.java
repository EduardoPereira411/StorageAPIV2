package com.StorageAPI.UserManagement.repository;

import com.StorageAPI.UserManagement.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findRefreshTokenByTokenValue(String tokenValue);

    Optional<RefreshToken> findRefreshTokenByUserUsername(String username);

    void deleteByTokenValue(String tokenValue);
}
