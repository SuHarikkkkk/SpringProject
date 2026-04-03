package com.example.springproject.controller;

import com.example.springproject.entity.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sellers")
public class SellerController {
    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @GetMapping
    public ResponseEntity<List<Seller>> getAllSellers() {
        List<Seller> sellers = sellerService.getAllSellers();
        return ResponseEntity.ok(sellers);
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<List<Product>> getSellerProducts(@PathVariable Long id) {
        List<Product> products = sellerService.getSellerProducts(id);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable Long id) {
        Seller seller = sellerService.getSellerById(id);
        if (seller == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(seller);
    }

    @GetMapping("/mail")
    public ResponseEntity<Seller> getSellerByMail(@RequestParam String mail) {
        Seller seller = sellerService.getSellerByEmail(mail);
        if (seller == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(seller);
    }

    @PostMapping
    public ResponseEntity<Seller> registerSeller(@RequestBody Seller seller) {
        try {
            Seller newSeller = sellerService.registerSeller(seller);
            return ResponseEntity.ok(newSeller);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Seller> updateSeller(@PathVariable Long id, @RequestBody Seller seller) {
        Seller sellerToUpdate = sellerService.updateSellerProfile(id, seller);
        if (sellerToUpdate == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(sellerToUpdate);
    }

    @PostMapping("/{id}/products")
    public ResponseEntity<Product> addProduct(@RequestBody Product product, @PathVariable Long id) {
        try {
            Product savedProduct = sellerService.addProduct(id, product);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
