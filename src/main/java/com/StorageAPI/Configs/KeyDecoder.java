package com.StorageAPI.Configs;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class KeyDecoder {

    @Autowired
    private JwtDecoder jwtDecoder;

    public String getUsernameByToken(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        String newToken = token.replace("Bearer ","");
        Jwt decodedToken = this.jwtDecoder.decode(newToken);
        String subject = (String) decodedToken.getClaims().get("sub");

        return String.valueOf(subject.split(",")[1]);
    }

    public UUID getIdByToken(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        String newToken = token.replace("Bearer ","");
        Jwt decodedToken = this.jwtDecoder.decode(newToken);
        String subject = (String) decodedToken.getClaims().get("sub");

        return UUID.fromString(subject.split(",")[0]);
    }
}
