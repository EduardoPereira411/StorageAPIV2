package com.StorageAPI.UserManagement.api;
import com.StorageAPI.UserManagement.model.MyUser;
import com.StorageAPI.UserManagement.model.RefreshToken;
import com.StorageAPI.UserManagement.services.UserService;
import com.StorageAPI.FileManagement.FileStorageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;


@Tag(name = "Validation", description = "Endpoints for users login and registering")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserViewMapper userViewMapper;

    private final JwtDecoder jwtDecoder;

    private final UserService userService;

    private final FileStorageService fileStorageService;

    private final BCryptPasswordEncoder passwordEncoder;


    @Operation(summary = "Logs in the user, giving a authorization token in the header")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        try {
            MyUser user = userService.findByUsername(loginRequest.username());
            if (!user.isLoginCorrect(loginRequest, passwordEncoder)) {
                throw new BadCredentialsException("User or password is invalid!");
            }

            String token = userService.getJWTToken(user);

            String refreshTokenValue = userService.getRefreshToken(user);

            // Save refresh token to the database
            RefreshToken refreshToken = new RefreshToken(user, refreshTokenValue);
            userService.refreshAuthToken(refreshToken);

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION,token)
                    .header("RefreshToken",refreshTokenValue)
                    .body(new LoginResponse(token,refreshTokenValue));
        } catch (final BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Endpoint for refreshing Authentication tokens without needing log in")
    @GetMapping("/refreshToken/{refreshToken}")
    public ResponseEntity<String> refreshToken(@PathVariable String refreshToken){
        try {
            Jwt refTokenClaims = jwtDecoder.decode(refreshToken);
            if (!refTokenClaims.getClaimAsString("type").equals("refresh_token")) {
                throw new BadCredentialsException("Invalid refresh token");
            }
            Optional<RefreshToken> refreshTokenOptional = userService.getRefreshToken(refreshToken);
            if (refreshTokenOptional.isEmpty()) {
                throw new RuntimeException("Refresh Token doesntExist!");
            }

            final RefreshToken refToken = refreshTokenOptional.get();
            if(LocalDateTime.now().isAfter(refToken.getExpiration())){
                throw new RuntimeException("Refresh Token is Expired!");
            }

            final MyUser refTokenUser = refToken.getUser();

            String token = userService.getJWTToken(refTokenUser);

            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION,token).body(token);
        } catch (final BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Register endpoint for normal users")
    @PostMapping("/register")
    public ResponseEntity<UserView> register(@RequestPart(name="json") String requestString,
                                             @RequestPart(name="photo", required=false) MultipartFile file) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        RegisterRequest createUserRequest = objectMapper.readValue(requestString, RegisterRequest.class);

        final var user = userService.createUser(createUserRequest,file);
        return ResponseEntity.status(HttpStatus.CREATED).body(userViewMapper.toUserView(user));
    }

    @Operation(summary = "Register endpoint for admins where they can specify the roles for the user")
    @PostMapping("/admin/register")
    public ResponseEntity<UserView> registerAdmin(@RequestPart(name="json") String requestString,
                                                  @RequestPart(name="photo", required=false) MultipartFile file) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        RegisterAdminRequest createUserRequest = objectMapper.readValue(requestString, RegisterAdminRequest.class);

        final var user = userService.createAdmin(createUserRequest,file);
        return ResponseEntity.status(HttpStatus.CREATED).body(userViewMapper.toUserView(user));
    }

    @Operation(summary = "Download a Users Photo")
    @GetMapping("/user/photo/{username}/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable final String fileName,
                                                 final HttpServletRequest request) {
        // Load file as Resource
        final Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (final IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
