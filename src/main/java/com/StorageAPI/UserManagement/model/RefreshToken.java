package com.StorageAPI.UserManagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refreshtoken")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "refresh_id")
    @Getter
    @Setter
    private UUID refreshId;

    @Getter
    @Setter
    @Column(name = "token_value", columnDefinition = "TEXT")
    private String tokenValue;

    @Getter
    @Setter
    private LocalDateTime expiration;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false) // Foreign key column in refresh_token table
    @Getter
    @Setter
    private MyUser user;

    public RefreshToken(MyUser user, String tokenValue) {
        this.user = user;
        this.tokenValue = tokenValue;
        this.expiration = LocalDateTime.now().plusDays(7);
    }

    public RefreshToken() {

    }
}
