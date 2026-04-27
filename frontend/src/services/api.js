const API_URL = import.meta.env.VITE_API_URL;

export async function apiFetch(path, options = {}) {
    const response = await fetch(`${API_URL}${path}`, {
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {}),
        },
        ...options,
    });

    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}