package com.example.springproject.service;

import com.example.springproject.dto.ProductCreateDto;
import com.example.springproject.dto.ProductDto;
import com.example.springproject.entity.Category;
import com.example.springproject.entity.Product;
import com.example.springproject.entity.Role;
import com.example.springproject.entity.User;
import com.example.springproject.repository.CategoryRepository;
import com.example.springproject.repository.ProductRepository;
import com.example.springproject.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<ProductDto> getAllProducts(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size))
                .map(this::toDto);
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        return toDto(product);
    }

    public Page<ProductDto> getProductsByCategory(Long categoryId, int page, int size) {
        return productRepository.findByCategoryId(categoryId, PageRequest.of(page, size))
                .map(this::toDto);
    }

    public Page<ProductDto> getProductsBySeller(Long sellerId, int page, int size) {
        return productRepository.findBySellerId(sellerId, PageRequest.of(page, size))
                .map(this::toDto);
    }

    public ProductDto createProduct(ProductCreateDto dto) {
        User seller = userRepository.findById(dto.sellerId())
                .orElseThrow(() -> new RuntimeException("Продавец не найден"));

        if (seller.getRole() != Role.SELLER) {
            throw new RuntimeException("Товар может принадлежать только продавцу");
        }

        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setImageUrl(dto.imageUrl());
        product.setPrice(dto.price());
        product.setStock(dto.stock());
        product.setSeller(seller);

        if (dto.categoryId() != null) {
            Category category = categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new RuntimeException("Категория не найдена"));
            product.setCategory(category);
        }

        Product savedProduct = productRepository.save(product);
        return toDto(savedProduct);
    }

    public ProductDto updateProduct(Long productId, ProductCreateDto dto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setImageUrl(dto.imageUrl());
        product.setPrice(dto.price());
        product.setStock(dto.stock());

        if (dto.categoryId() != null) {
            Category category = categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new RuntimeException("Категория не найдена"));
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }

        if (dto.sellerId() != null) {
            User seller = userRepository.findById(dto.sellerId())
                    .orElseThrow(() -> new RuntimeException("Продавец не найден"));

            if (seller.getRole() != Role.SELLER) {
                throw new RuntimeException("Товар может принадлежать только продавцу");
            }

            product.setSeller(seller);
        }

        Product savedProduct = productRepository.save(product);
        return toDto(savedProduct);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private ProductDto toDto(Product product) {
        Long categoryId = null;
        String categoryName = null;

        if (product.getCategory() != null) {
            categoryId = product.getCategory().getId();
            categoryName = product.getCategory().getName();
        }

        Long sellerId = null;

        if (product.getSeller() != null) {
            sellerId = product.getSeller().getId();
        }

        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getImageUrl(),
                product.getPrice(),
                product.getStock(),
                categoryId,
                categoryName,
                sellerId
        );
    }
}