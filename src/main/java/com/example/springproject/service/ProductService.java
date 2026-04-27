package com.example.springproject.service;

import com.example.springproject.entity.Product;
import com.example.springproject.entity.Role;
import com.example.springproject.entity.User;
import com.example.springproject.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }



    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product saveProduct(Product product) {
        if (product.getSeller() == null || product.getSeller().getRole() != Role.SELLER) {
            throw new RuntimeException("Товар может принадлежать только продавцу");
        }
        return productRepository.save(product);
    }

    public Page<Product> getProductsByCategory(Long id, Pageable pageable) {
        return productRepository.findByCategoryId(id, pageable);
    }

    public Product updateProduct(Product product, Long id) {
        Product oldProduct = getProductById(id);
        if (oldProduct == null) {
            throw new RuntimeException("Товар не найден");
        }

        if (product.getSeller() != null && product.getSeller().getRole() != Role.SELLER) {
            throw new RuntimeException("Товар может принадлежать только продавцу");
        }

        oldProduct.setName(product.getName());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setCategory(product.getCategory());
        oldProduct.setSeller(product.getSeller());
        oldProduct.setStock(product.getStock());
        oldProduct.setImageUrl(product.getImageUrl());
        return productRepository.save(oldProduct);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Page<Product> getProductBySeller(Long id, Pageable pageable) {
        return productRepository.findBySellerId(id, pageable);
    }
}
