package com.StorageAPI.UserManagement.model;

import com.StorageAPI.UserManagement.api.LoginRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class MyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    @Getter
    @Setter
    private UUID userId;

    @Column(unique = true, updatable = false, nullable = false)
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String image;

    @ElementCollection
    @Getter
    private final Set<Role> authorities = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private List<RefreshToken> refreshTokens;


    public MyUser(String username, String password) {
        this.username = username;
        this.password = password;
        addAuthority(new Role("USER"));
    }

    public MyUser(String username, String password, String image) {
        this.username = username;
        this.password = password;
        this.image = image;
        addAuthority(new Role("USER"));
    }

    public MyUser() {

    }

    public void addAuthority(final Role r) {
        authorities.add(r);
    }

    public boolean isLoginCorrect(LoginRequest loginRequest, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(loginRequest.password(), this.password);
    }

}
