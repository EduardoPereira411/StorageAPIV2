package com.StorageAPI.UserManagement.services;

import com.StorageAPI.UserManagement.api.RegisterAdminRequest;
import com.StorageAPI.UserManagement.api.RegisterRequest;
import com.StorageAPI.UserManagement.model.MyUser;
import com.StorageAPI.UserManagement.model.RefreshToken;
import com.StorageAPI.UserManagement.model.Role;
import com.StorageAPI.UserManagement.repository.MyUserRepository;
import com.StorageAPI.UserManagement.repository.RefreshTokenRepository;
import com.StorageAPI.Exceptions.ConflictException;
import com.StorageAPI.Exceptions.NotFoundException;
import com.StorageAPI.FileManagement.FileStorageService;
import com.StorageAPI.FileManagement.UploadFileResponse;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private MyUserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private JwtEncoder jwtEncoder;

    public MyUser createAdmin(RegisterAdminRequest request, MultipartFile photo) {
        try{
            findByUsername(request.username());
            throw new ConflictException("Username already exists!");
        }catch (NotFoundException ex){
            if (!request.password().equals(request.rePassword())) {
                throw new ValidationException("Passwords don't match!");
            }
            Set<String> requestedAuthorities = request.roles();
            boolean isValidAuthority = requestedAuthorities.stream()
                    .anyMatch(Role::isValidAdminRole);

            if (!isValidAuthority) {
                throw new ValidationException("Admin Role not found, Role must be one presented in documents!");
            }

            MyUser user = new MyUser(request.username(), passwordEncoder.encode(request.password()));

            if (user.getAuthorities() != null) {
                Set<Role> set = Role.stringToRole(request.roles());
                if (set != null) {
                    user.getAuthorities().addAll(set);
                }
            }
            if(!(photo == null || photo.isEmpty())){
                final var fileResponse = uploadUserPfp(request.username(), photo);
                user.setImage(fileResponse.getFileDownloadUri());
            }
            return userRepository.save(user);

        }
    }

    public MyUser createUser(RegisterRequest request, MultipartFile photo) {
        try{
            findByUsername(request.username());
            throw new ConflictException("Username already exists!");
        }catch (NotFoundException ex){
            if (!request.password().equals(request.rePassword())) {
                throw new ValidationException("Passwords don't match!");
            }
            MyUser user = new MyUser(request.username(), passwordEncoder.encode(request.password()));
            if(!(photo == null || photo.isEmpty())){
                final var fileResponse = uploadUserPfp(request.username(), photo);
                user.setImage(fileResponse.getFileDownloadUri());
            }
            return userRepository.save(user);
        }
    }

    @Transactional
    @Override
    public RefreshToken refreshAuthToken(RefreshToken newRefreshToken) {
        Optional<RefreshToken> isRefreshToken = refreshTokenRepository.findRefreshTokenByUserUsername(newRefreshToken.getUser().getUsername());
        if (isRefreshToken.isPresent()){
            refreshTokenRepository.deleteByTokenValue(isRefreshToken.get().getTokenValue());
            return refreshTokenRepository.save(newRefreshToken);
        }
        return refreshTokenRepository.save(newRefreshToken);
    }

    @Override
    public Optional<RefreshToken> getRefreshToken(String tokenValue) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findRefreshTokenByTokenValue(tokenValue);
        if (refreshToken.isPresent()) {
            return refreshToken;
        } else {
            throw new NotFoundException("Not a valid refresh Token");
        }
    }

    @Override
    public MyUser findByUsername(String username) {
        Optional<MyUser> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new NotFoundException("User doesn't exist");
        }
    }

    @Override
    public MyUser findByUserID(UUID userID) {
        Optional<MyUser> user = userRepository.findById(userID);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new NotFoundException("User doesn't exist");
        }
    }

    public UploadFileResponse uploadUserPfp(final String username, final MultipartFile file) {

        final String fileName = fileStorageService.storeFile(String.valueOf(username), file);

        String fileDownloadUri = "/user/photo/" + username + "/" + fileName;

        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

    @Override
    public String getJWTToken(MyUser user){

        final Instant now = Instant.now();
        final long expiry = 36000L;

        final String scope = user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(joining(" "));

        final JwtClaimsSet claims = JwtClaimsSet.builder().issuer("UserMagAPI").issuedAt(now)
                .expiresAt(now.plusSeconds(expiry)).subject(format("%s,%s", user.getUserId(), user.getUsername()))
                .claim("roles", scope).build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public String getRefreshToken(MyUser user) {
        final Instant now = Instant.now();

        final long refreshTokenExpiry = 604800L; // 7 days
        final JwtClaimsSet refreshTokenClaims = JwtClaimsSet.builder()
                .issuer("UserMagAPI")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshTokenExpiry))
                .subject(format("%s,%s", user.getUserId(), user.getUsername()))
                .claim("type", "refresh_token")
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(refreshTokenClaims)).getTokenValue();
    }

}
