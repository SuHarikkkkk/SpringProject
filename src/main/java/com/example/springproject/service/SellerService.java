package com.example.springproject.service;

import com.example.springproject.entity.Customer;
import com.example.springproject.entity.Product;
import com.example.springproject.entity.Seller;
import com.example.springproject.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SellerService {
    private final SellerRepository sellerRepository;
    private final UserService userService;
    private final ProductService productService;

    public SellerService(SellerRepository sellerRepository, UserService userService, ProductService productService) {
        this.sellerRepository = sellerRepository;
        this.userService = userService;
        this.productService = productService;
    }
    public Seller getSellerById(Long id) {
        return sellerRepository.findById(id).orElseThrow(() -> new RuntimeException("Пользователь с таким айди не найден"));
    }

    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    public Seller getSellerByEmail(String email) {
        return sellerRepository.findAll().stream().filter(c -> c.getMail().equals(email)).findFirst().orElseThrow(() -> new RuntimeException("Пользователь с такой почтой не найден"));
    }

    @Transactional
    public Seller registerSeller(Seller seller) {
        if (userService.existUserByMail(seller.getMail())) {
            throw new RuntimeException("Пользователь с такой почтой уже существует");
        }
        return sellerRepository.save(seller);
    }
    @Transactional
    public Seller updateSellerProfile(Long id, Seller seller) {
        Seller sellerOldProfile = getSellerById(id);
        sellerOldProfile.setFirstName(seller.getFirstName());
        sellerOldProfile.setLastName(seller.getLastName());
        sellerOldProfile.setMail(seller.getMail());
        return sellerRepository.save(sellerOldProfile);
    }

    public List<Product> getSellerProducts(Long id) {
        return productService.getProductBySeller(id);
    }

    @Transactional
    public Product addProduct(Long sellerId, Product product) {
        Seller seller = getSellerById(sellerId);
        product.setSeller(seller);
        return productService.saveProduct(product);
    }
}
