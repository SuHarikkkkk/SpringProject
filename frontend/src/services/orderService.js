import { apiFetch } from "./api.js";

export function getOrdersByUser(userId, page = 0, size = 10) {
    return apiFetch(`/orders/user/${userId}?page=${page}&size=${size}`);
}

export function createOrder(userId, shippingAddress, paymentMethod) {
    return apiFetch(`/orders/${userId}/create`, {
        method: "POST",
        body: JSON.stringify({
            shippingAddress,
            paymentMethod,
        }),
    });
}

export function cancelOrder(orderId) {
    return apiFetch(`/orders/${orderId}/cancel`, {
        method: "POST",
    });
}

export function getAllOrders(page = 0, size = 10) {
    return apiFetch(`/orders?page=${page}&size=${size}`);
}

export function updateOrderStatus(orderId, status) {
    return apiFetch(`/orders/${orderId}/status`, {
        method: "PUT",
        body: JSON.stringify({
            status,
        }),
    });
}