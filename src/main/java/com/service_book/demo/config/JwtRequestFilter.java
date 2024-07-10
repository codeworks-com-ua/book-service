package com.service_book.demo.config;

import static com.service_book.demo.util.JwtUtil.ROLES;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
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

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response,
            @NotNull FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader(AUTHORIZATION);
        String username = null;
        String jwt = null;
        final boolean jwtExists = nonNull(authorizationHeader) && authorizationHeader.startsWith(BEARER);

        if (jwtExists) {
            jwt = authorizationHeader.substring(7);
            username = JwtUtil.extractUsername(jwt);
        }

        final boolean requireAuth = nonNull(username) && isNull(SecurityContextHolder.getContext().getAuthentication());

        if (requireAuth) {

            final boolean isTokenValid = JwtUtil.validateToken(jwt, username);

            if (isTokenValid) {
                List<String> roles = JwtUtil.extractAllClaims(jwt).get(ROLES, List.class);

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null,
                                roles.stream()
                                        .map(SimpleGrantedAuthority::new)
                                        .toList());

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}
