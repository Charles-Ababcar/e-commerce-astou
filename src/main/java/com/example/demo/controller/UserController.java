package com.example.demo.controller;

import com.example.demo.config.CustomUserDetails;
import com.example.demo.config.CustomUserDetailsService;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.UserProfileDto;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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


    // -----------------------
    // REGISTER PUBLIC
    // -----------------------
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody User user) {
        User registeredUser = userService.register(user);

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

        System.out.println("🔑 Tentative de login pour : " + request.getUsername());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            System.out.println("✅ Authentification OK");

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
                user.getUsername(),
                user.getEmail(),
                List.of(user.getRole().name())
        );

        return ResponseEntity.ok(profile);
    }


    // -----------------------
    // UPDATE USER
    // -----------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable String id,
            @RequestBody User updatedUser) {

        User user = userService.updateUser(id, updatedUser);

        return ResponseEntity.ok(new ApiResponse<>(
                "User updated successfully",
                user,
                HttpStatus.OK.value()
        ));
    }


    @GetMapping("all")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(new
                ApiResponse<>("Liste des utilisateurs récupérée avec succès", users,HttpStatus.OK.value())
        );
    }

    // -----------------------
    // DELETE USER
    // -----------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);

        return ResponseEntity.ok(new ApiResponse<>(
                "User deleted successfully",
                "OK",
                HttpStatus.OK.value()
        ));
    }
}

