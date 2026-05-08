package com.example.springproject.dto;

import com.example.springproject.entity.Role;

public record UserDto(
        Long id,
        String mail,
        String firstName,
        String lastName,
        Role role,
        String companyName
) {
}