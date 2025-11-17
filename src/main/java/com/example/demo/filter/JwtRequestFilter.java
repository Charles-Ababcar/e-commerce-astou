package com.example.demo.filter;

import com.example.demo.config.CustomUserDetailsService;
import com.example.demo.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // 🔥 Endpoints publics à ignorer
        List<String> excluded = List.of(
                "/api/users/login",
                "/api/users/register",
                "/api/auth/refresh",
                "/v3/api-docs",
                "/swagger-ui"
        );

        if (excluded.stream().anyMatch(uri::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // ✅ Extraction du token
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // Token invalide → ne pas authentifier
                logger.warn("JWT invalide pour la requête " + uri + " : " + e.getMessage());
            }
        }

        // ✅ Authentification dans le contexte Spring
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Log côté serveur
                System.out.println("Authentication set for user: " + username);

                // Message dans la réponse (pour debug uniquement, pas en prod)
                response.setHeader("X-Debug-Message", "Authentication set for user: " + username);
            } else {
                System.out.println("JWT token invalid for user: " + username);
                response.setHeader("X-Debug-Message", "JWT token invalid for user: " + username);
            }
        } else {
            System.out.println("No username found in JWT or authentication already set.");
            response.setHeader("X-Debug-Message", "No username found in JWT or authentication already set.");
        }


        chain.doFilter(request, response);
    }
}
