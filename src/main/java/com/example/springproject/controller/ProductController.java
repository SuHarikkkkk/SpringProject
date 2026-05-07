package com.example.springproject.controller;


import com.example.springproject.dto.ProductCreateDto;
import com.example.springproject.dto.ProductDto;
import com.example.springproject.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Page<ProductDto> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.getAllProducts(page, size);
    }

    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable Long productId) {
        return productService.getProductById(productId);
    }

    @GetMapping("/category/{categoryId}")
    public Page<ProductDto> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.getProductsByCategory(categoryId, page, size);
    }

    @GetMapping("/seller/{sellerId}")
    public Page<ProductDto> getProductsBySeller(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.getProductsBySeller(sellerId, page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createProduct(@RequestBody ProductCreateDto dto) {
        return productService.createProduct(dto);
    }

    @PutMapping("/{productId}")
    public ProductDto updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductCreateDto dto
    ) {
        return productService.updateProduct(productId, dto);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
    }
}