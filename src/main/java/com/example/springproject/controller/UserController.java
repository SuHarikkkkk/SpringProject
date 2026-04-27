package com.example.springproject.controller;

import com.example.springproject.entity.User;
import com.example.springproject.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SELLER')")
    @GetMapping("/{userId}")
    public ResponseEntity<User> getById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/mail/{mail}")
    public ResponseEntity<User> getByMail(@PathVariable String mail) {
        User user = userService.findUserByMail(mail);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER')")
    @PutMapping("/{userId}")
    public ResponseEntity<User> update(@PathVariable Long userId, @RequestBody User user) {
        User user1 = userService.updateUser(user, userId);
        return ResponseEntity.ok(user1);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
