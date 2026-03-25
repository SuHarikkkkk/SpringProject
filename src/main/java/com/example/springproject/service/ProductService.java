package com.example.springproject.service;

import com.example.springproject.entity.Product;
import com.example.springproject.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Product product, Long id) {
        Product oldProduct = getProductById(id);
        if (oldProduct == null) {
            throw new RuntimeException("Товар не найден");
        }
        oldProduct.setName(product.getName());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setCategory(product.getCategory());
        oldProduct.setSeller(product.getSeller());
        oldProduct.setStock(product.getStock());
        return productRepository.save(oldProduct);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
