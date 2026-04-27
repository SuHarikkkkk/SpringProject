import { useEffect, useState } from "react";
import { PageTitle } from "../components/layout/PageTitle.jsx";
import { Card } from "../components/ui/Card.jsx";
import {
    getProductsBySeller,
    createProduct,
    updateProduct,
    deleteProduct,
} from "../services/productService.js";

export function SellerProductsPage() {
    const rawUser = localStorage.getItem("user");
    const user = rawUser ? JSON.parse(rawUser) : null;

    const [products, setProducts] = useState([]);
    const [pageData, setPageData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [editingId, setEditingId] = useState(null);

    const [form, setForm] = useState({
        name: "",
        description: "",
        price: "",
        stock: "",
        imageUrl: "",
    });

    async function loadSellerProducts(page = 0) {
        if (!user) {
            setLoading(false);
            setError("Сначала войдите в аккаунт");
            return;
        }

        if (user.role !== "SELLER") {
            setLoading(false);
            setError("Доступ только для продавца");
            return;
        }

        try {
            setLoading(true);
            setError("");

            const data = await getProductsBySeller(user.id, page, 10);
            setProducts(data.content || []);
            setPageData(data);
        } catch (err) {
            console.error(err);
            setError("Не удалось загрузить товары продавца");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadSellerProducts(0);
    }, []);

    function handleChange(e) {
        setForm({
            ...form,
            [e.target.name]: e.target.value,
        });
    }

    function handleEdit(product) {
        setEditingId(product.id);
        setForm({
            name: product.name || "",
            description: product.description || "",
            price: product.price || "",
            stock: product.stock || "",
            imageUrl: product.imageUrl || "",
        });
    }

    function resetForm() {
        setEditingId(null);
        setForm({
            name: "",
            description: "",
            price: "",
            stock: "",
            imageUrl: "",
        });
    }

    async function handleSubmit(e) {
        e.preventDefault();

        try {
            const payload = {
                name: form.name,
                description: form.description,
                price: Number(form.price),
                stock: Number(form.stock),
                imageUrl: form.imageUrl,
                seller: { id: user.id },
            };

            if (editingId) {
                await updateProduct(editingId, payload);
                alert("Товар обновлен");
            } else {
                await createProduct(payload);
                alert("Товар создан");
            }

            resetForm();
            await loadSellerProducts(pageData?.number || 0);
        } catch (err) {
            console.error(err);
            alert("Не удалось сохранить товар");
        }
    }

    async function handleDelete(productId) {
        try {
            await deleteProduct(productId);
            alert("Товар удален");
            await loadSellerProducts(pageData?.number || 0);
        } catch (err) {
            console.error(err);
            alert("Не удалось удалить товар");
        }
    }

    function goToPrevPage() {
        if (pageData && pageData.number > 0) {
            loadSellerProducts(pageData.number - 1);
        }
    }

    function goToNextPage() {
        if (pageData && !pageData.last) {
            loadSellerProducts(pageData.number + 1);
        }
    }

    if (loading) {
        return <p>Загрузка товаров продавца...</p>;
    }

    if (error) {
        return <p className="text-red-600">{error}</p>;
    }

    return (
        <div>
            <PageTitle
                title="Seller Products"
                subtitle="Create, edit and manage your products."
            />

            <div className="grid gap-6 lg:grid-cols-[1.2fr_1.8fr]">
                <Card>
                    <h3 className="text-lg font-semibold">
                        {editingId ? "Edit product" : "Create product"}
                    </h3>

                    <form onSubmit={handleSubmit} className="mt-4 space-y-4">
                        <label className="block space-y-2">
                            <span className="text-sm font-medium text-slate-700">Name</span>
                            <input
                                name="name"
                                value={form.name}
                                onChange={handleChange}
                                className="w-full rounded-2xl border px-4 py-3"
                            />
                        </label>

                        <label className="block space-y-2">
                            <span className="text-sm font-medium text-slate-700">Description</span>
                            <textarea
                                name="description"
                                value={form.description}
                                onChange={handleChange}
                                className="w-full rounded-2xl border px-4 py-3"
                            />
                        </label>

                        <label className="block space-y-2">
                            <span className="text-sm font-medium text-slate-700">Price</span>
                            <input
                                name="price"
                                type="number"
                                value={form.price}
                                onChange={handleChange}
                                className="w-full rounded-2xl border px-4 py-3"
                            />
                        </label>

                        <label className="block space-y-2">
                            <span className="text-sm font-medium text-slate-700">Stock</span>
                            <input
                                name="stock"
                                type="number"
                                value={form.stock}
                                onChange={handleChange}
                                className="w-full rounded-2xl border px-4 py-3"
                            />
                        </label>

                        <label className="block space-y-2">
                            <span className="text-sm font-medium text-slate-700">Image URL</span>
                            <input
                                name="imageUrl"
                                value={form.imageUrl}
                                onChange={handleChange}
                                className="w-full rounded-2xl border px-4 py-3"
                            />
                        </label>

                        <div className="flex gap-3">
                            <button className="rounded-2xl bg-slate-900 px-5 py-3 text-white">
                                {editingId ? "Update" : "Create"}
                            </button>

                            {editingId && (
                                <button
                                    type="button"
                                    onClick={resetForm}
                                    className="rounded-2xl border px-5 py-3"
                                >
                                    Cancel
                                </button>
                            )}
                        </div>
                    </form>
                </Card>

                <div className="space-y-4">
                    {products.length === 0 && (
                        <Card>
                            <p className="text-slate-600">У вас пока нет товаров</p>
                        </Card>
                    )}

                    {products.map((product) => (
                        <Card key={product.id}>
                            <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                                <div className="flex gap-4">
                                    <img
                                        src={product.imageUrl || "https://placehold.co/200x200?text=No+Image"}
                                        alt={product.name || "Product"}
                                        className="h-24 w-24 rounded-2xl object-cover"
                                        onError={(e) => {
                                            e.currentTarget.src = "https://placehold.co/200x200?text=No+Image";
                                        }}
                                    />

                                    <div>
                                        <h3 className="font-semibold">{product.name || "Без названия"}</h3>
                                        <p className="text-sm text-slate-500">{product.description || "Без описания"}</p>
                                        <p className="text-sm text-slate-600">
                                            ${product.price ?? 0} · Stock: {product.stock ?? 0}
                                        </p>
                                    </div>
                                </div>

                                <div className="flex gap-2">
                                    <button
                                        onClick={() => handleEdit(product)}
                                        className="rounded-2xl border px-4 py-2 text-sm"
                                    >
                                        Edit
                                    </button>

                                    <button
                                        onClick={() => handleDelete(product.id)}
                                        className="rounded-2xl border px-4 py-2 text-sm"
                                    >
                                        Delete
                                    </button>
                                </div>
                            </div>
                        </Card>
                    ))}

                    {pageData && (
                        <div className="flex items-center justify-between">
                            <div className="text-sm text-slate-500">
                                Страница {pageData.number + 1} из {pageData.totalPages || 1}, всего товаров: {pageData.totalElements}
                            </div>

                            <div className="flex gap-2">
                                <button
                                    onClick={goToPrevPage}
                                    disabled={pageData.number === 0}
                                    className="rounded-2xl border px-4 py-2 text-sm disabled:opacity-50"
                                >
                                    Prev
                                </button>

                                <button
                                    onClick={goToNextPage}
                                    disabled={pageData.last}
                                    className="rounded-2xl border px-4 py-2 text-sm disabled:opacity-50"
                                >
                                    Next
                                </button>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}