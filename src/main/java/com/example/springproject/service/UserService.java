package com.example.springproject.service;

import com.example.springproject.entity.User;
import com.example.springproject.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerUser(User user) {
        boolean exists = userRepository.findAll().stream().anyMatch(user1 -> user1.getMail().equals(user.getMail()));
        if (exists) {
            throw new RuntimeException("Пользователь с такой почтой уже существует");
        }
        return userRepository.save(user);
    }

    public User findUserByMail(String mail) {
        return userRepository.findAll().stream().filter(user -> user.getMail().equals(mail)).findFirst().orElseThrow(() -> new RuntimeException("Пользователь с такой почтой не найден"));
    }

    public boolean existUserByMail(String mail) {
        return userRepository.findAll().stream().anyMatch(user -> user.getMail().equals(mail));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Пользователь с таким айди не найден"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(User userDetails, Long id) {
        User user = getUserById(id);
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setMail(userDetails.getMail());
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}


