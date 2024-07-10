package com.service_book.demo.config;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.service_book.demo.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = JwtUtil.extractUsername(jwt);
            System.out.println("JWT Token: " + jwt);
            System.out.println("Extracted Username: " + username);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (JwtUtil.validateToken(jwt, username)) {
                List<String> roles = JwtUtil.extractAllClaims(jwt).get("roles", List.class);
                System.out.println("Extracted Roles: " + roles);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null,
                                roles.stream()
                                        .map(SimpleGrantedAuthority::new)
                                        .collect(Collectors.toList()));
                System.out.println("Authentication Token: " + authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                System.out.println("Invalid JWT Token");
            }
        }
        chain.doFilter(request, response);
    }
}
