package org.example.supermarketpos.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.example.supermarketpos.dto.LoginRequest;
import org.example.supermarketpos.dto.LoginResponse;
import org.example.supermarketpos.model.User;
import org.example.supermarketpos.repository.UserRepository;
import org.example.supermarketpos.security.JwtUtil;
import org.example.supermarketpos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class authController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Optional<User> userOptional = userService.findByUsername(loginRequest.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(loginRequest.getPassword())) {
                String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

                // Add JWT as HTTP-only cookie
                Cookie cookie = new Cookie("jwt", token);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(3600); // 1 hour
                response.addCookie(cookie);

                return ResponseEntity.ok(new LoginResponse(token, user.getRole()));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/validate")
    public ResponseEntity<LoginResponse> validateToken(@CookieValue(value = "jwt", required = false) String token,
                                                       HttpServletResponse response) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            if (jwtUtil.isTokenExpired(token)) {
                // Clear the expired cookie
                Cookie cookie = new Cookie("jwt", null);
                cookie.setPath("/");
                cookie.setMaxAge(0); // Deletes the cookie
                response.addCookie(cookie);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);
            return ResponseEntity.ok(new LoginResponse(token, role));

        } catch (Exception e) {
            // Clear cookie if token is invalid (corrupted, invalid signature, etc.)
            Cookie cookie = new Cookie("jwt", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        // Invalidate the JWT token cookie
        Cookie cookie = new Cookie("jwt", null);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Delete the cookie
        response.addCookie(cookie);

        // Optionally, invalidate the session if you are using HttpSession
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok().build();
    }




}



