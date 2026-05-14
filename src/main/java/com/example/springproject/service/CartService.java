package com.example.springproject.service;

import com.example.springproject.dto.CartDto;
import com.example.springproject.dto.CartItemCreateDto;
import com.example.springproject.dto.CartItemDto;
import com.example.springproject.dto.CartItemUpdateDto;
import com.example.springproject.entity.*;
import com.example.springproject.repository.CartItemRepository;
import com.example.springproject.repository.CartRepository;
import com.example.springproject.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(
            CartRepository cartRepository,
            ProductRepository productRepository,
            CartItemRepository cartItemRepository
    ) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    private void validateCustomer(User user) {
        if (user == null || user.getRole() != Role.CUSTOMER) {
            throw new RuntimeException("Корзина доступна только покупателям");
        }
    }

    public Cart getCartEntityForOrder(User user) {
        return getOrCreateCartEntity(user);
    }

    public CartDto getOrCreateCart(User user) {
        Cart cart = getOrCreateCartEntity(user);
        return toCartDto(cart);
    }

    private Cart getOrCreateCartEntity(User user) {
        validateCustomer(user);

        Cart cart = cartRepository.findAll()
                .stream()
                .filter(c -> c.getUser() != null && c.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElse(null);

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setCartItems(new ArrayList<>());
            cartRepository.save(cart);
        }

        return cart;
    }

    @Transactional
    public CartItemDto addItemToCart(User user, CartItemCreateDto dto) {
        validateCustomer(user);

        Cart cart = getOrCreateCartEntity(user);

        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        if (dto.quantity() <= 0) {
            throw new RuntimeException("Количество должно быть больше 0");
        }

        if (product.getStock() < dto.quantity()) {
            throw new RuntimeException("Товара недостаточно на складе");
        }

        CartItem item = cart.getCartItems()
                .stream()
                .filter(ci -> ci.getProduct().getId().equals(dto.productId()))
                .findFirst()
                .orElse(null);

        if (item != null) {
            int newQuantity = item.getQuantity() + dto.quantity();

            if (product.getStock() < newQuantity) {
                throw new RuntimeException("Товара недостаточно на складе");
            }

            item.setQuantity(newQuantity);
            item.setPrice(product.getPrice());
            item = cartItemRepository.save(item);
        } else {
            item = new CartItem();
            item.setProduct(product);
            item.setQuantity(dto.quantity());
            item.setCart(cart);
            item.setPrice(product.getPrice());

            item = cartItemRepository.save(item);

            if (cart.getCartItems() == null) {
                cart.setCartItems(new ArrayList<>());
            }

            cart.getCartItems().add(item);
        }

        cartRepository.save(cart);

        return toCartItemDto(item);
    }

    @Transactional
    public void removeItemFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    public CartItemDto updateCartItemQuantity(Long cartItemId, CartItemUpdateDto dto) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Товар в корзине не найден"));

        if (dto.quantity() <= 0) {
            cartItemRepository.delete(item);
            return null;
        }

        Product product = item.getProduct();

        if (product.getStock() < dto.quantity()) {
            throw new RuntimeException("Not enough stock. Available: " + product.getStock());
        }

        item.setQuantity(dto.quantity());

        CartItem savedItem = cartItemRepository.save(item);

        return toCartItemDto(savedItem);
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = getOrCreateCartEntity(user);

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();

        cartRepository.save(cart);
    }

    public Double getCartTotalPrice(User user) {
        Cart cart = getOrCreateCartEntity(user);

        return cart.getCartItems()
                .stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    public List<CartItemDto> getCartItems(User user) {
        Cart cart = getOrCreateCartEntity(user);

        return cart.getCartItems()
                .stream()
                .map(this::toCartItemDto)
                .toList();
    }

    private CartDto toCartDto(Cart cart) {
        return new CartDto(
                cart.getId(),
                cart.getUser() != null ? cart.getUser().getId() : null
        );
    }

    private CartItemDto toCartItemDto(CartItem item) {
        Product product = item.getProduct();

        Long categoryId = null;
        String categoryName = null;

        if (product != null && product.getCategory() != null) {
            categoryId = product.getCategory().getId();
            categoryName = product.getCategory().getName();
        }

        return new CartItemDto(
                item.getId(),
                product != null ? product.getId() : null,
                product != null ? product.getName() : null,
                product != null ? product.getImageUrl() : null,
                categoryId,
                categoryName,
                item.getQuantity(),
                item.getPrice()
        );
    }
}