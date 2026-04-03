package com.example.springproject.service;

import com.example.springproject.entity.*;
import com.example.springproject.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public UserService(UserRepository userRepository, CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository, OrderItemRepository orderItemRepository) {

        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
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

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    @Transactional
    public User updateUser(User userDetails, Long id) {
        User user = getUserById(id);
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setMail(userDetails.getMail());
        user.setCompanyName(userDetails.getCompanyName());
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        if (user.getRole() == Role.CUSTOMER) {
            Cart cart = cartRepository.findAll().stream().filter(c -> c.getUser() != null && c.getUser().getId().equals(user.getId())).findFirst().orElse(null);

            if (cart != null) {
                cartItemRepository.deleteAll(cart.getCartItems());
                cartRepository.delete(cart);
            }
        }

        if (user.getRole() == Role.SELLER) {
            List<Product> products = productRepository.findAll().stream().filter(product -> product.getSeller() != null && product.getSeller().getId().equals(user.getId())).toList();
            productRepository.deleteAll(products);
        }
        userRepository.delete(user);
    }
}


