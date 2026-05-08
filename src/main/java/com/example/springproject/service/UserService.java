package com.example.springproject.service;

import com.example.springproject.dto.RegisterDto;
import com.example.springproject.dto.UserDto;
import com.example.springproject.dto.UserUpdateDto;
import com.example.springproject.entity.*;
import com.example.springproject.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public UserService(
            UserRepository userRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository
    ) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public Page<UserDto> getAllUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size))
                .map(this::toDto);
    }

    @Transactional
    public UserDto registerUser(RegisterDto dto) {
        boolean exists = userRepository.findAll()
                .stream()
                .anyMatch(user -> user.getMail().equals(dto.mail()));

        if (exists) {
            throw new RuntimeException("Пользователь с такой почтой уже существует");
        }

        User user = new User();
        user.setMail(dto.mail());
        user.setHashedPassword(dto.hashedPassword());
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setRole(dto.role());
        user.setCompanyName(dto.companyName());

        User savedUser = userRepository.save(user);

        return toDto(savedUser);
    }

    public User findUserByMail(String mail) {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getMail().equals(mail))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Пользователь с такой почтой не найден"));
    }

    public UserDto findUserDtoByMail(String mail) {
        return toDto(findUserByMail(mail));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    public UserDto getUserDtoById(Long id) {
        return toDto(getUserById(id));
    }

    @Transactional
    public UserDto updateUser(Long id, UserUpdateDto dto) {

        User user = getUserById(id);

        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setMail(dto.mail());
        user.setCompanyName(dto.companyName());

        User updatedUser = userRepository.save(user);

        return toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {

        User user = getUserById(id);

        if (user.getRole() == Role.CUSTOMER) {

            Cart cart = cartRepository.findAll()
                    .stream()
                    .filter(c ->
                            c.getUser() != null &&
                                    c.getUser().getId().equals(user.getId()))
                    .findFirst()
                    .orElse(null);

            if (cart != null) {
                cartItemRepository.deleteAll(cart.getCartItems());
                cartRepository.delete(cart);
            }
        }

        if (user.getRole() == Role.SELLER) {

            List<Product> products = productRepository.findAll()
                    .stream()
                    .filter(product ->
                            product.getSeller() != null &&
                                    product.getSeller().getId().equals(user.getId()))
                    .toList();

            productRepository.deleteAll(products);
        }

        userRepository.delete(user);
    }

    UserDto toDto(User user) {

        return new UserDto(
                user.getId(),
                user.getMail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getCompanyName()
        );
    }
}