import { apiFetch } from "./api.js";

export function login(mail, hashedPassword) {
    return apiFetch("/auth/login", {
        method: "POST",
        body: JSON.stringify({
            mail,
            hashedPassword,
        }),
    });
}

export function register(user) {
    return apiFetch("/auth/register", {
        method: "POST",
        body: JSON.stringify(user),
    });
}