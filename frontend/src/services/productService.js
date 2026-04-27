import { apiFetch } from "./api.js";

export function getProducts(page = 0, size = 10) {
    return apiFetch(`/products?page=${page}&size=${size}`);
}

export function getProductById(productId) {
    return apiFetch(`/products/${productId}`);
}

export function getProductsByCategory(categoryId, page = 0, size = 10) {
    return apiFetch(`/products/category/${categoryId}?page=${page}&size=${size}`);
}

export function getProductsBySeller(sellerId, page = 0, size = 10) {
    return apiFetch(`/products/seller/${sellerId}?page=${page}&size=${size}`);
}

export function createProduct(product) {
    return apiFetch("/products", {
        method: "POST",
        body: JSON.stringify(product),
    });
}

export function updateProduct(productId, product) {
    return apiFetch(`/products/${productId}`, {
        method: "PUT",
        body: JSON.stringify(product),
    });
}

export function deleteProduct(productId) {
    return apiFetch(`/products/${productId}`, {
        method: "DELETE",
    });
}