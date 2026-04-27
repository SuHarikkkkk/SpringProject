import { apiFetch } from "./api.js";

export function login(mail, hashedPassword) {
    return apiFetch(
        `/auth/login?mail=${encodeURIComponent(mail)}&hashedPassword=${encodeURIComponent(hashedPassword)}`,
        {
            method: "POST",
        }
    );
}

export function register(user) {
    return apiFetch("/auth/register", {
        method: "POST",
        body: JSON.stringify(user),
    });
}