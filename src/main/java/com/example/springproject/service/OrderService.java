package com.example.springproject.service;

import com.example.springproject.entity.*;
import com.example.springproject.repository.OrderItemRepository;
import com.example.springproject.repository.OrderRepository;
import com.example.springproject.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    private void validateCustomer(User user) {
        if (user == null || user.getRole() != Role.CUSTOMER) {
            throw new RuntimeException("Заказы доступны только покупателю");
        }
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Order> getOrdersByCustomer(User user) {
        validateCustomer(user);
        return orderRepository.findAll().stream().filter(x -> x.getCustomer()!= null && x.getCustomer().getId().equals(user.getId())).collect(Collectors.toList());
    }

    public Order createOrderFromCart(User user, String shippingAddress, String paymentMethod) {
        validateCustomer(user);
        Cart cart = cartService.getOrCreateCart(user);
        List<CartItem> cartItems = cart.getCartItems();

        if (cartItems.isEmpty()) {
            throw  new RuntimeException("Нельзя создать заказ из пустой корзины");
        }

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Недостаточное количество товара на складе. Доступно: " + product.getStock());
            }
        }

        Order order = new Order();
        order.setCustomer(user);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW);
        order.setTotalPrice(0.0);
        orderRepository.save(order);

        double totalPrice = 0.0;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice() * cartItem.getQuantity());
            orderItem.setProductName(product.getName());
            orderItemRepository.save(orderItem);

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
            totalPrice += orderItem.getPrice();
        }
        order.setTotalPrice(totalPrice);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        cartService.clearCart(user);
        return order;
    }


    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        Order order = getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("Заказ не найден");
        }
        OrderStatus oldStatus = order.getStatus();
        if (oldStatus == OrderStatus.CANCELLED || oldStatus == OrderStatus.DELIVERED) {
            throw new RuntimeException("Нельзя изменить статус доставленного или отменённого заказа");
        }
        order.setStatus(orderStatus);
        order.setUpdatedAt(LocalDateTime.now());
        if (orderStatus == OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                Product product = productRepository.findById(item.getProductId()).orElse(null);
                if (product != null) {
                    product.setStock(product.getStock() + item.getQuantity());
                    productRepository.save(product);
                }
            }
        }
    }

    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        if (order == null) {
            throw new RuntimeException("Заказ не найден");
        }
        if (order.getStatus() != OrderStatus.NEW) {
            throw new RuntimeException("Невозможно удалить активный заказ");
        }
        orderRepository.deleteById(id);
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = getOrderById(id);
        if (order == null) {
            throw new RuntimeException("Заказ не найден");
        }
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Нельзя отменить доставленный заказ");
        }
        updateOrderStatus(id, OrderStatus.CANCELLED);
    }
}
