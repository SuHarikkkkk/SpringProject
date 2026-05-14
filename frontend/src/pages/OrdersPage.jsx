import { useEffect, useState } from "react";
import { PageTitle } from "../components/layout/PageTitle.jsx";
import { Card } from "../components/ui/Card.jsx";
import { getOrdersByUser, cancelOrder } from "../services/orderService.js";

export function OrdersPage() {
    const [orders, setOrders] = useState([]);
    const [pageData, setPageData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const rawUser = localStorage.getItem("user");
    const user = rawUser ? JSON.parse(rawUser) : null;

    async function loadOrders(page = 0) {
        if (!user) {
            setLoading(false);
            setError("Сначала войдите в аккаунт");
            return;
        }

        try {
            setLoading(true);
            setError("");

            const data = await getOrdersByUser(user.id, page, 10);
            setOrders(data.content || []);
            setPageData(data);
        } catch (err) {
            console.error(err);
            setError("Не удалось загрузить заказы");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadOrders(0);
    }, []);

    async function handleCancel(orderId) {
        try {
            await cancelOrder(orderId);
            alert("Заказ отменен");
            await loadOrders(pageData?.number || 0);
        } catch (err) {
            console.error(err);
            alert("Не удалось отменить заказ");
        }
    }

    function goToPrevPage() {
        if (pageData && pageData.number > 0) {
            loadOrders(pageData.number - 1);
        }
    }

    function goToNextPage() {
        if (pageData && !pageData.last) {
            loadOrders(pageData.number + 1);
        }
    }

    if (loading) {
        return <p>Загрузка заказов...</p>;
    }

    if (error) {
        return <p className="text-red-600">{error}</p>;
    }

    return (
        <div>
            <PageTitle title="Orders" subtitle="Track created orders and cancel available ones." />

            <div className="space-y-4">
                {orders.length === 0 && (
                    <Card>
                        <p className="text-slate-600">Заказов пока нет</p>
                    </Card>
                )}

                {orders.map((order) => (
                    <Card key={order.id}>
                        <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                            <div>
                                <h3 className="text-lg font-semibold">Order #{order.id}</h3>
                                <p className="text-sm text-slate-500">
                                    Created: {order.createdAt || "No date"}
                                </p>
                            </div>

                            <div className="flex items-center gap-3">
                <span className="rounded-full bg-slate-100 px-3 py-1 text-sm">
                  {order.status}
                </span>

                                <span className="font-semibold">
                  ${order.totalPrice ?? 0}
                </span>

                                <button
                                    onClick={() => handleCancel(order.id)}
                                    className="rounded-2xl border px-4 py-2 text-sm"
                                >
                                    Cancel
                                </button>
                            </div>
                        </div>
                    </Card>
                ))}
            </div>

            {pageData && (
                <div className="mt-6 flex items-center justify-between">
                    <div className="text-sm text-slate-500">
                        Страница {pageData.number + 1} из {pageData.totalPages || 1}, всего заказов: {pageData.totalElements}
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
    );
}