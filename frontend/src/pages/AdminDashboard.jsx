import { useEffect, useState } from "react";
import { PageTitle } from "../components/layout/PageTitle.jsx";
import { Card } from "../components/ui/Card.jsx";
import { getUsers, deleteUser } from "../services/userService.js";
import { getAllOrders, updateOrderStatus } from "../services/orderService.js";
import { getProducts, deleteProduct } from "../services/productService.js";
import {
    getCategories,
    createCategory,
    updateCategory,
    deleteCategory,
} from "../services/categoryService.js";

export function AdminDashboard() {
    const rawUser = localStorage.getItem("user");
    const user = rawUser ? JSON.parse(rawUser) : null;

    const [users, setUsers] = useState([]);
    const [orders, setOrders] = useState([]);
    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [categoryName, setCategoryName] = useState("");
    const [categoryDescription, setCategoryDescription] = useState("");
    const [editingCategoryId, setEditingCategoryId] = useState(null);

    async function loadAdminData() {
        if (!user) {
            setLoading(false);
            setError("Сначала войдите в аккаунт");
            return;
        }

        if (user.role !== "ADMIN") {
            setLoading(false);
            setError("Доступ только для администратора");
            return;
        }

        try {
            setLoading(true);
            setError("");

            const [usersData, ordersData, productsData, categoriesData] =
                await Promise.all([
                    getUsers(0, 10),
                    getAllOrders(0, 10),
                    getProducts(0, 10),
                    getCategories(0, 10),
                ]);

            setUsers(usersData.content || []);
            setOrders(ordersData.content || []);
            setProducts(productsData.content || []);
            setCategories(categoriesData.content || []);
        } catch (err) {
            console.error(err);
            setError("Не удалось загрузить админские данные");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadAdminData();
    }, []);

    async function handleDeleteUser(userId) {
        try {
            await deleteUser(userId);
            await loadAdminData();
        } catch (err) {
            console.error(err);
            alert("Не удалось удалить пользователя");
        }
    }

    async function handleDeleteProduct(productId) {
        try {
            await deleteProduct(productId);
            await loadAdminData();
        } catch (err) {
            console.error(err);
            alert("Не удалось удалить товар");
        }
    }

    async function handleUpdateOrderStatus(orderId, status) {
        try {
            await updateOrderStatus(orderId, status);
            await loadAdminData();
        } catch (err) {
            console.error(err);
            alert("Не удалось обновить статус заказа");
        }
    }

    function handleEditCategory(category) {
        setEditingCategoryId(category.id);
        setCategoryName(category.name || "");
        setCategoryDescription(category.description || "");
    }

    function resetCategoryForm() {
        setEditingCategoryId(null);
        setCategoryName("");
        setCategoryDescription("");
    }

    async function handleSaveCategory(e) {
        e.preventDefault();

        try {
            const payload = {
                name: categoryName,
                description: categoryDescription,
            };

            if (editingCategoryId) {
                await updateCategory(editingCategoryId, payload);
            } else {
                await createCategory(payload);
            }

            resetCategoryForm();
            await loadAdminData();
        } catch (err) {
            console.error(err);
            alert("Не удалось сохранить категорию");
        }
    }

    async function handleDeleteCategory(categoryId) {
        try {
            await deleteCategory(categoryId);
            await loadAdminData();
        } catch (err) {
            console.error(err);
            alert("Не удалось удалить категорию");
        }
    }

    if (loading) {
        return <p>Загрузка админки...</p>;
    }

    if (error) {
        return <p className="text-red-600">{error}</p>;
    }

    return (
        <div>
            <PageTitle
                title="Admin Dashboard"
                subtitle="Manage users, categories, products, and orders."
            />

            <div className="grid gap-5 md:grid-cols-2 xl:grid-cols-4">
                <Card>
                    <p className="text-sm text-slate-500">Users</p>
                    <p className="mt-2 text-3xl font-semibold">{users.length}</p>
                </Card>

                <Card>
                    <p className="text-sm text-slate-500">Orders</p>
                    <p className="mt-2 text-3xl font-semibold">{orders.length}</p>
                </Card>

                <Card>
                    <p className="text-sm text-slate-500">Products</p>
                    <p className="mt-2 text-3xl font-semibold">{products.length}</p>
                </Card>

                <Card>
                    <p className="text-sm text-slate-500">Categories</p>
                    <p className="mt-2 text-3xl font-semibold">{categories.length}</p>
                </Card>
            </div>

            <div className="mt-6 grid gap-6 xl:grid-cols-2">
                <Card>
                    <h3 className="mb-4 text-lg font-semibold">Users</h3>
                    <div className="space-y-3 text-sm">
                        {users.map((item) => (
                            <div
                                key={item.id}
                                className="flex items-center justify-between rounded-2xl border p-3"
                            >
                                <div>
                                    <p className="font-medium">
                                        {item.firstName} {item.lastName}
                                    </p>
                                    <p className="text-slate-500">
                                        {item.mail} · {item.role}
                                    </p>
                                </div>

                                <button
                                    onClick={() => handleDeleteUser(item.id)}
                                    className="rounded-2xl border px-4 py-2"
                                >
                                    Delete
                                </button>
                            </div>
                        ))}
                    </div>
                </Card>

                <Card>
                    <h3 className="mb-4 text-lg font-semibold">Orders</h3>
                    <div className="space-y-3 text-sm">
                        {orders.map((order) => (
                            <div
                                key={order.id}
                                className="flex items-center justify-between rounded-2xl border p-3"
                            >
                                <div>
                                    <p className="font-medium">Order #{order.id}</p>
                                    <p className="text-slate-500">
                                        {order.status} · ${order.totalPrice ?? 0}
                                    </p>
                                </div>

                                <select
                                    value={order.status}
                                    onChange={(e) =>
                                        handleUpdateOrderStatus(order.id, e.target.value)
                                    }
                                    className="rounded-2xl border px-3 py-2"
                                >
                                    <option value="NEW">NEW</option>
                                    <option value="PAID">PAID</option>
                                    <option value="SHIPPED">SHIPPED</option>
                                    <option value="DELIVERED">DELIVERED</option>
                                    <option value="CANCELLED">CANCELLED</option>
                                </select>
                            </div>
                        ))}
                    </div>
                </Card>

                <Card>
                    <h3 className="mb-4 text-lg font-semibold">Products</h3>
                    <div className="space-y-3 text-sm">
                        {products.map((product) => (
                            <div
                                key={product.id}
                                className="flex items-center justify-between rounded-2xl border p-3"
                            >
                                <div>
                                    <p className="font-medium">{product.name || "Без названия"}</p>
                                    <p className="text-slate-500">
                                        ${product.price ?? 0} · Stock: {product.stock ?? 0}
                                    </p>
                                </div>

                                <button
                                    onClick={() => handleDeleteProduct(product.id)}
                                    className="rounded-2xl border px-4 py-2"
                                >
                                    Delete
                                </button>
                            </div>
                        ))}
                    </div>
                </Card>

                <Card>
                    <h3 className="mb-4 text-lg font-semibold">
                        {editingCategoryId ? "Edit category" : "Categories"}
                    </h3>

                    <form onSubmit={handleSaveCategory} className="space-y-3">
                        <input
                            value={categoryName}
                            onChange={(e) => setCategoryName(e.target.value)}
                            placeholder="Category name"
                            className="w-full rounded-2xl border px-4 py-3"
                        />

                        <input
                            value={categoryDescription}
                            onChange={(e) => setCategoryDescription(e.target.value)}
                            placeholder="Category description"
                            className="w-full rounded-2xl border px-4 py-3"
                        />

                        <div className="flex gap-2">
                            <button className="rounded-2xl bg-slate-900 px-4 py-3 text-white">
                                {editingCategoryId ? "Update" : "Create"}
                            </button>

                            {editingCategoryId && (
                                <button
                                    type="button"
                                    onClick={resetCategoryForm}
                                    className="rounded-2xl border px-4 py-3"
                                >
                                    Cancel
                                </button>
                            )}
                        </div>
                    </form>

                    <div className="mt-4 space-y-3 text-sm">
                        {categories.map((category) => (
                            <div
                                key={category.id}
                                className="flex items-center justify-between rounded-2xl border p-3"
                            >
                                <div>
                                    <p className="font-medium">{category.name}</p>
                                    <p className="text-slate-500">{category.description}</p>
                                </div>

                                <div className="flex gap-2">
                                    <button
                                        onClick={() => handleEditCategory(category)}
                                        className="rounded-2xl border px-4 py-2"
                                    >
                                        Edit
                                    </button>

                                    <button
                                        onClick={() => handleDeleteCategory(category.id)}
                                        className="rounded-2xl border px-4 py-2"
                                    >
                                        Delete
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                </Card>
            </div>
        </div>
    );
}