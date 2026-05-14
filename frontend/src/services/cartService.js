import { apiFetch } from "./api.js";

export function getCart(userId) {
    return apiFetch(`/carts/${userId}`);
}

export function getCartItems(userId) {
    return apiFetch(`/carts/${userId}/items`);
}

export function getCartTotal(userId) {
    return apiFetch(`/carts/${userId}/total`);
}

export function addItemToCart(userId, productId, quantity) {
    return apiFetch(`/carts/${userId}/items`, {
        method: "POST",
        body: JSON.stringify({
            productId,
            quantity,
        }),
    });
}

export function updateCartItemQuantity(cartItemId, quantity) {
    return apiFetch(`/carts/items/${cartItemId}`, {
        method: "PUT",
        body: JSON.stringify({
            quantity,
        }),
    });
}

export function removeCartItem(cartItemId) {
    return apiFetch(`/carts/items/${cartItemId}`, {
        method: "DELETE",
    });
}

export function clearCart(userId) {
    return apiFetch(`/carts/${userId}/clear`, {
        method: "DELETE",
    });
}