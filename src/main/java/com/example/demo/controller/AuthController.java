package com.example.demo.controller;

import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.UserProfileDto;
import com.example.demo.service.MyUserDetailsService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("Authorization").substring(7);
        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtUtil.validateToken(refreshToken, userDetails.getUsername())) {
            String newAccessToken = jwtUtil.generateToken(username);
            String newRefreshToken = jwtUtil.generateRefreshToken(username);
            return ResponseEntity.ok(new JwtResponse(newAccessToken, newRefreshToken));
        }
        return ResponseEntity.badRequest().body("Invalid refresh token");
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new UserProfileDto(username, roles));
    }




    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // On the client side, the tokens should be deleted.
        return ResponseEntity.ok("Logged out successfully");
    }
}
