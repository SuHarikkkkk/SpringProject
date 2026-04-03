package com.example.springproject.service;

import com.example.springproject.entity.Product;
import com.example.springproject.entity.Role;
import com.example.springproject.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        if (product.getSeller() == null || product.getSeller().getRole() != Role.SELLER) {
            throw new RuntimeException("Товар может принадлежать только продавцу");
        }
        return productRepository.save(product);
    }

    public List<Product> getProductsByCategory(Long id) {
        return productRepository.findAll().stream().filter(p -> p.getCategory() != null && p.getCategory().getId().equals(id)).collect(Collectors.toList());
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
        return productRepository.save(oldProduct);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getProductBySeller(Long id) {
        return productRepository.findAll().stream().filter(p -> p.getSeller() != null && p.getSeller().getId().equals(id)).collect(Collectors.toList());
    }
}
