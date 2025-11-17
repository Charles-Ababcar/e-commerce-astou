package com.example.demo.controller;

import com.example.demo.config.CustomUserDetails;
import com.example.demo.config.CustomUserDetailsService;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.UserProfileDto;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody User user) {
        User registeredUser = userService.register(user);
        return new ResponseEntity<>(new ApiResponse<>("User registered successfully", registeredUser), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody User user) {
        try {
            System.out.println("🔑 Tentative de connexion pour l'utilisateur: " + user.getUsername());

            // Tentative d'authentification
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
            System.out.println("✅ Authentification réussie pour: " + user.getUsername());

            // Charger l'utilisateur depuis le service
            final UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
            System.out.println("ℹ️ UserDetails récupéré: " + userDetails.getUsername() +
                    ", rôles: " + userDetails.getAuthorities());

            // Générer les tokens
            final String accessToken = jwtUtil.generateToken(userDetails.getUsername());
            final String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());
            System.out.println("🔐 Tokens générés pour: " + user.getUsername());

            AuthResponse authResponse = new AuthResponse(accessToken, refreshToken);
            return new ResponseEntity<>(new ApiResponse<>("Login successful", authResponse), HttpStatus.OK);

        } catch (Exception ex) {
            // Log complet de l'erreur
            System.out.println("❌ Échec de connexion pour: " + u



    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // côté client supprime token
        return ResponseEntity.ok("Logged out successfully");
    }
}
