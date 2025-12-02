package com.example.demo.controller;

import com.example.demo.config.CustomUserDetails;
import com.example.demo.config.CustomUserDetailsService;
import com.example.demo.dto.*;
import com.example.demo.dto.request.*;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;


    @PostMapping("create-user")
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(
            @RequestBody UserRequestDTO dto
    ) {
        UserResponseDTO createdUser = userService.createUser(dto);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        "Utilisateur créé avec succès",
                        createdUser,
                        HttpStatus.CREATED.value()
                ));
    }

    // -----------------------
    // REGISTER PUBLIC
    // -----------------------
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(@RequestBody UserRegisterDTO registerDTO) {
        // Crée l'utilisateur
        UserResponseDTO registeredUser = userService.register(registerDTO);



        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        "User registered successfully",
                        registeredUser,
                        HttpStatus.CREATED.value()
                ));
    }


    // -----------------------
    // LOGIN
    // -----------------------
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>("Mot de passe incorrect", null, HttpStatus.UNAUTHORIZED.value()));
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getUsername());
        String accessToken = jwtUtil.generateToken(userDetails.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Login successful",
                        new AuthResponse(accessToken, refreshToken),
                        HttpStatus.OK.value()
                )
        );
    }

    // -----------------------
    // PROFILE
    // -----------------------
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> profile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.user();

        UserProfileDto profile = new UserProfileDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                List.of(user.getRole().name())
        );

        return ResponseEntity.ok(profile);
    }

    // -----------------------
    // UPDATE USER
    // -----------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequestDTO dto) {

        UserResponseDTO user = userService.updateUser(id, dto);

        return ResponseEntity.ok(new ApiResponse<>(
                "User updated successfully",
                user,
                HttpStatus.OK.value()
        ));
    }

    // -----------------------
    // GET ALL USERS
    // -----------------------
    @GetMapping("all")
    public ApiResponse<Page<UserResponseDTO>> getAllUsers( @RequestParam(defaultValue = "") String search,
                                                           Pageable pageable) {
        return userService.getAllUsers(search ,pageable);

    }


    // -----------------------
    // DELETE USER
    // -----------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        return ResponseEntity.ok(new ApiResponse<>(
                "User deleted successfully",
                "OK",
                HttpStatus.OK.value()
        ));
    }

    // -----------------------
    // UPDATE PASSWORD (SELF)
    // -----------------------
    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> updatePasswordSelf(
            @PathVariable Long id,
            @RequestBody UpdatePasswordSelfDTO dto) {

        userService.updatePasswordSelf(id, dto.getOldPassword(), dto.getNewPassword());

        return ResponseEntity.ok(new ApiResponse<>(
                "Mot de passe mis à jour avec succès",
                null,
                HttpStatus.OK.value()
        ));
    }

    // -----------------------
    // UPDATE PASSWORD (ADMIN)
    // -----------------------
    @PutMapping("/{id}/password/admin")
    public ResponseEntity<ApiResponse<Void>> updatePasswordByAdmin(
            @PathVariable Long id,
            @RequestBody UpdatePasswordAdminDTO dto) {

        userService.updatePasswordByAdmin(id, dto.getNewPassword());

        return ResponseEntity.ok(new ApiResponse<>(
                "Mot de passe mis à jour par l’admin",
                null,
                HttpStatus.OK.value()
        ));
    }
}
