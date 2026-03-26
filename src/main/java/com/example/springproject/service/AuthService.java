package com.example.springproject.service;

import com.example.springproject.entity.User;
import com.example.springproject.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public User login(String mail, String hashedPassword) {
        User user = userService.findUserByMail(mail);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }
        if (!hashedPassword.equals(user.getHashedPassword())) {
            throw new RuntimeException("Неверный пароль");
        }
        return user;
    }

    public void logout() {

    }
}
