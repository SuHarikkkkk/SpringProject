package com.example.springproject.service;

import com.example.springproject.dto.OrderCreateDto;
import com.example.springproject.dto.OrderDto;
import com.example.springproject.dto.OrderItemDto;
import com.example.springproject.dto.OrderStatusUpdateDto;
import com.example.springproject.entity.*;
import com.example.springproject.repository.OrderItemRepository;
import com.example.springproject.repository.OrderRepository;
import com.example.springproject.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            CartService cartService
    ) {
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

    public Page<OrderDto> getAllOrders(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page, size))
                .map(this::toDto);
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        return toDto(order);
    }

    private Order getOrderEntityById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
    }

    public Page<OrderDto> getOrdersByCustomer(User user, int page, int size) {
        validateCustomer(user);

        return orderRepository.findByCustomerId(user.getId(), PageRequest.of(page, size))
                .map(this::toDto);
    }

    @Transactional
    public OrderDto createOrderFromCart(User user, OrderCreateDto dto) {
        validateCustomer(user);

        Cart cart = cartService.getCartEntityForOrder(user);
        List<CartItem> cartItems = cart.getCartItems();

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Нельзя создать заказ из пустой корзины");
        }

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Недостаточное количество товара на складе. Доступно: " + product.getStock());
            }
        }

        Order order = new Order();
        order.setCustomer(user);
        order.setShippingAddress(dto.shippingAddress());
        order.setPaymentMethod(dto.paymentMethod());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW);
        order.setTotalPrice(0.0);

        order = orderRepository.save(order);

        double totalPrice = 0.0;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice() * cartItem.getQuantity());

            orderItemRepository.save(orderItem);

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            totalPrice += orderItem.getPrice();
        }

        order.setTotalPrice(totalPrice);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(user);

        return toDto(savedOrder);
    }

    @Transactional
    public OrderDto updateOrderStatus(Long orderId, OrderStatusUpdateDto dto) {
        Order order = getOrderEntityById(orderId);

        OrderStatus oldStatus = order.getStatus();

        if (oldStatus == OrderStatus.CANCELLED || oldStatus == OrderStatus.DELIVERED) {
            throw new RuntimeException("Нельзя изменить статус доставленного или отменённого заказа");
        }

        order.setStatus(dto.status());
        order.setUpdatedAt(LocalDateTime.now());

        if (dto.status() == OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                Product product = productRepository.findById(item.getProductId()).orElse(null);

                if (product != null) {
                    product.setStock(product.getStock() + item.getQuantity());
                    productRepository.save(product);
                }
            }
        }

        return toDto(orderRepository.save(order));
    }

    public void deleteOrder(Long id) {
        Order order = getOrderEntityById(id);

        if (order.getStatus() != OrderStatus.NEW) {
            throw new RuntimeException("Невозможно удалить активный заказ");
        }

        orderRepository.deleteById(id);
    }

    @Transactional
    public OrderDto cancelOrder(Long id) {
        Order order = getOrderEntityById(id);

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Нельзя отменить доставленный заказ");
        }

        return updateOrderStatus(id, new OrderStatusUpdateDto(OrderStatus.CANCELLED));
    }

    @Transactional
    public OrderDto payOrder(Long id) {
        Order order = getOrderEntityById(id);

        if (order.getStatus() != OrderStatus.NEW) {
            throw new RuntimeException("Оплатить можно только новый заказ");
        }

        order.setStatus(OrderStatus.PAID);
        order.setUpdatedAt(LocalDateTime.now());

        return toDto(orderRepository.save(order));
    }

    private OrderDto toDto(Order order) {
        List<OrderItemDto> items = order.getItems() == null
                ? List.of()
                : order.getItems()
                .stream()
                .map(this::toItemDto)
                .toList();

        return new OrderDto(
                order.getId(),
                order.getCustomer() != null ? order.getCustomer().getId() : null,
                order.getCustomer() != null ? order.getCustomer().getMail() : null,
                order.getShippingAddress(),
                order.getPaymentMethod(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                items
        );
    }

    private OrderItemDto toItemDto(OrderItem item) {
        return new OrderItemDto(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getPrice()
        );
    }
}