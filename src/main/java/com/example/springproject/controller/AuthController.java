package com.example.springproject.controller;


import com.example.springproject.entity.User;
import com.example.springproject.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestParam String mail, @RequestParam String hashedPassword) {
        try {
            User user = authService.login(mail, hashedPassword);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.noContent().build();
    }
}
