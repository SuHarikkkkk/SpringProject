package com.example.springproject.repository;

import com.example.springproject.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Modifying
    @Query("delete from CartItem ci where ci.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}