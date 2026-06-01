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

export function banUser(userId) {
    return apiFetch(`/users/${userId}/ban`, {
        method: "PATCH",
    });
}

export function unbanUser(userId) {
    return apiFetch(`/users/${userId}/unban`, {
        method: "PATCH",
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