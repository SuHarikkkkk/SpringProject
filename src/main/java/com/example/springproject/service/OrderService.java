package com.example.springproject.service;

import com.example.springproject.entity.Order;
import com.example.springproject.entity.OrderItem;
import com.example.springproject.entity.Product;
import com.example.springproject.repository.OrderItemRepository;
import com.example.springproject.repository.OrderRepository;
import com.example.springproject.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order createOrder() {
        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalPrice(0.0);
        return orderRepository.save(order);
    }

    public OrderItem addProductToOrder(Long orderId, Long productId, int quantity) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);

        double price = product.getPrice() * quantity;
        item.setPrice(price);

        orderItemRepository.save(item);

        order.setTotalPrice(order.getTotalPrice() + price); //увеличиваем общую стоимость заказа на цену добавленного товара
        orderRepository.save(order);

        return item;
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
