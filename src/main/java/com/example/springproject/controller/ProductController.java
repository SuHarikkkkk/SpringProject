package com.example.springproject.controller;


import com.example.springproject.entity.Product;
import com.example.springproject.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(Pageable pageable) {
        Page<Product> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<Product>> getProductsByCategory(@PathVariable Long categoryId, Pageable pageable) {
        try {
            Page<Product> products = productService.getProductsByCategory(categoryId, pageable);
            return ResponseEntity.ok(products);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<Page<Product>> getProductsBySellerId(@PathVariable Long sellerId, Pageable pageable) {
        try {
            Page<Product> products = productService.getProductBySeller(sellerId, pageable);
            return ResponseEntity.ok(products);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        try {
            Product savedProduct = productService.saveProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(product, productId);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);
            return ResponseEntity.noContent().build();
        }  catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

