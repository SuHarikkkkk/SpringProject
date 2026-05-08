package com.example.springproject.controller;

import com.example.springproject.dto.UserDto;
import com.example.springproject.dto.UserUpdateDto;
import com.example.springproject.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        return userService.getUserDtoById(userId);
    }

    @GetMapping
    public Page<UserDto> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userService.getAllUsers(page, size);
    }

    @GetMapping("/mail/{mail}")
    public UserDto getByMail(@PathVariable String mail) {
        return userService.findUserDtoByMail(mail);
    }

    @PutMapping("/{userId}")
    public UserDto update(
            @PathVariable Long userId,
            @RequestBody UserUpdateDto dto
    ) {
        return userService.updateUser(userId, dto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}