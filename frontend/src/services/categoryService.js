import { apiFetch } from "./api.js";

export function getCategories(page = 0, size = 10) {
    return apiFetch(`/categories?page=${page}&size=${size}`);
}

export function createCategory(category) {
    return apiFetch("/categories", {
        method: "POST",
        body: JSON.stringify(category),
    });
}

export function updateCategory(categoryId, category) {
    return apiFetch(`/categories/${categoryId}`, {
        method: "PUT",
        body: JSON.stringify(category),
    });
}

export function deleteCategory(categoryId) {
    return apiFetch(`/categories/${categoryId}`, {
        method: "DELETE",
    });
}