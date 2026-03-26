package com.example.springproject.service;

import com.example.springproject.entity.Customer;
import com.example.springproject.entity.Order;
import com.example.springproject.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final OrderService orderService;
    private final UserService userService;

    public CustomerService(CustomerRepository customerRepository, UserService userService, OrderService orderService) {
        this.customerRepository = customerRepository;
        this.userService = userService;
        this.orderService = orderService;
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Покупатель с таким айди не найден"));
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }


    public Customer getCustomerByEmail(String email) {
        return customerRepository.findAll().stream().filter(c -> c.getMail().equals(email)).findFirst().orElseThrow(() -> new RuntimeException("Пользователь с такой почтой не найден"));
    }

    @Transactional
    public Customer registerCustomer(Customer customer) {
        if (userService.existUserByMail(customer.getMail())) {
            throw new RuntimeException("Пользователь с такой почтой уже существует");
        }
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer updateCustomerProfile(Long id, Customer customer) {
        Customer customerOldProfile = getCustomerById(id);
        customerOldProfile.setFirstName(customer.getFirstName());
        customerOldProfile.setLastName(customer.getLastName());
        customerOldProfile.setMail(customer.getMail());
        return customerRepository.save(customerOldProfile);
    }

    public List<Order> getOrders(Long id) {
        Customer customer = getCustomerById(id);
        return orderService.getOrdersByCustomer(customer);
    }
}
