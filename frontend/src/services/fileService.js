import { apiFetch } from "./api";

export async function uploadFile(file) {

    const formData =
        new FormData();

    formData.append("file", file);

    const response =
        await fetch(
            "http://localhost:8080/files/upload",
            {
                method: "POST",
                body: formData
            }
        );

    return response.text();
}