package com.service_book.demo.service;

import static java.lang.String.format;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.service_book.demo.dao.UserRepository;
import com.service_book.demo.entity.User;
import com.service_book.demo.exception.AuthenticationException;
import com.service_book.demo.util.JwtUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserService implements UserDetailsService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User persistUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException(
                        format("User not found with username: [%s]", username)));
    }

    public String authenticate(String username, String password) {
        UserDetails userDetails = loadUserByUsername(username);
        boolean passwordMatches = passwordEncoder.matches(password, userDetails.getPassword());

        if (passwordMatches) {
            return generateToken(userDetails, username);
        }

        throw new AuthenticationException("Invalid credentials");
    }

    private String generateToken(UserDetails userDetails, String username) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return JwtUtil.generateToken(username, roles);
    }
}
