package com.example.springproject.service;

import com.example.springproject.entity.*;
import com.example.springproject.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public UserService(UserRepository userRepository, CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository, OrderItemRepository orderItemRepository) {

        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
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
        User user = getUserById(id);
        if (user instanceof Customer) {
            Customer customer = (Customer) user;
            if (customer.getCart() != null) {
                cartItemRepository.deleteAll(customer.getCart().getCartItems());
                cartRepository.delete(customer.getCart());
            }
        }
        if (user instanceof Seller) {
            Seller seller = (Seller) user;
            List<Product> products = productRepository.findAll().stream().filter(product -> product.getSeller() != null && product.getSeller().getId().equals(seller.getId())).toList();
            for (Product product : products) {
                List<OrderItem> orderItems = orderItemRepository.findAll().stream().filter(oi -> oi.getProduct() != null && oi.getProduct().getId().equals(product.getId())).collect(Collectors.toList());
                orderItemRepository.deleteAll(orderItems);
                productRepository.delete(product);
            }
        }

        userRepository.delete(user);
    }
}


