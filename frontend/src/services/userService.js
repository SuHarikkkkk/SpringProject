import { apiFetch } from "./api.js";

export function getUserById(userId) {
    return apiFetch(`/users/${userId}`);
}

export function updateUser(userId, userData) {
    return apiFetch(`/users/${userId}`, {
        method: "PUT",
        body: JSON.stringify(userData),
    });
}

export function getUsers(page = 0, size = 10) {
    return apiFetch(`/users?page=${page}&size=${size}`);
}

export function deleteUser(userId) {
    return apiFetch(`/users/${userId}`, {
        method: "DELETE",
    });
}