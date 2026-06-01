package com.example.springproject.service;

import com.example.springproject.dto.LoginDto;
import com.example.springproject.dto.UserDto;
import com.example.springproject.entity.User;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public UserDto login(LoginDto dto) {
        User user = userService.findUserByMail(dto.mail());

        if (user.isBanned()) {
            throw new RuntimeException("Ваш аккаунт заблокирован");
        }

        if (!dto.hashedPassword().equals(user.getHashedPassword())) {
            throw new RuntimeException("Неверный пароль");
        }

        return userService.toDto(user);
    }

    public void logout() {
    }
}