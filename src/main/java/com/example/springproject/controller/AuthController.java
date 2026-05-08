package com.example.springproject.controller;

import com.example.springproject.dto.LoginDto;
import com.example.springproject.dto.RegisterDto;
import com.example.springproject.dto.UserDto;
import com.example.springproject.service.AuthService;
import com.example.springproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@RequestBody RegisterDto dto) {
        return userService.registerUser(dto);
    }

    @PostMapping("/login")
    public UserDto login(@RequestBody LoginDto dto) {
        return authService.login(dto);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout() {
        authService.logout();
    }
}