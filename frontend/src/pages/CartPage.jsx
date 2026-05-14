import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { PageTitle } from "../components/layout/PageTitle.jsx";
import { Card } from "../components/ui/Card.jsx";
import {
  getCartItems,
  getCartTotal,
  updateCartItemQuantity,
  removeCartItem,
  clearCart,
} from "../services/cartService.js";
import { createOrder } from "../services/orderService.js";

export function CartPage() {
  const [items, setItems] = useState([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const rawUser = localStorage.getItem("user");
  const user = rawUser ? JSON.parse(rawUser) : null;

  async function loadCart() {
    if (!user) {
      setLoading(false);
      setError("Сначала войдите в аккаунт");
      return;
    }

    try {
      setLoading(true);
      setError("");

      const [itemsData, totalData] = await Promise.all([
        getCartItems(user.id),
        getCartTotal(user.id),
      ]);

      setItems(itemsData || []);
      setTotal(totalData || 0);
    } catch (err) {
      console.error(err);
      setError("Не удалось загрузить корзину");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadCart();
  }, []);

  async function handleQuantityChange(cartItemId, quantity) {
    if (quantity < 1) {
      return;
    }

    try {
      await updateCartItemQuantity(cartItemId, quantity);
      await loadCart();
    } catch (err) {
      console.error(err);
      alert("Не удалось изменить количество");
    }
  }

  async function handleRemove(cartItemId) {
    try {
      await removeCartItem(cartItemId);
      await loadCart();
    } catch (err) {
      console.error(err);
      alert("Не удалось удалить товар");
    }
  }

  async function handleClearCart() {
    try {
      await clearCart(user.id);
      await loadCart();
    } catch (err) {
      console.error(err);
      alert("Не удалось очистить корзину");
    }
  }

  async function handleCreateOrder() {
    if (!user) {
      alert("Сначала войдите в аккаунт");
      return;
    }

    if (items.length === 0) {
      alert("Корзина пуста");
      return;
    }

    try {
      await createOrder(user.id, "Test address", "CARD");
      alert("Заказ создан");
      navigate("/orders");
    } catch (err) {
      console.error(err);
      alert("Не удалось создать заказ");
    }
  }

  if (loading) {
    return <p>Загрузка корзины...</p>;
  }

  if (error) {
    return <p className="text-red-600">{error}</p>;
  }

  return (
      <div>
        <PageTitle title="Cart" subtitle="Review products before creating an order." />

        <div className="grid gap-6 lg:grid-cols-[2fr_1fr]">
          <div className="space-y-4">
            {items.length === 0 && (
                <Card>
                  <p className="text-slate-600">Корзина пуста</p>
                </Card>
            )}

            {items.map((item) => (
                <Card key={item.id}>
                  <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                    <div className="flex items-center gap-4">
                      <img
                          src={item.productImageUrl || "https://placehold.co/200x200?text=No+Image"}
                          alt={item.productName || "Product"}
                          className="h-20 w-20 rounded-2xl object-cover"
                          onError={(e) => {
                            e.currentTarget.src = "https://placehold.co/200x200?text=No+Image";
                          }}
                      />

                      <div>
                        <h3 className="font-semibold">
                          {item.productName || "Без названия"}
                        </h3>

                        <p className="text-sm text-slate-500">
                          {item.categoryName || "No category"}
                        </p>

                        <p className="text-sm text-slate-600">
                          ${item.price ?? 0}
                        </p>
                      </div>
                    </div>

                    <div className="flex items-center gap-3">
                      <input
                          type="number"
                          min="1"
                          value={item.quantity}
                          onChange={(e) =>
                              handleQuantityChange(item.id, Number(e.target.value))
                          }
                          className="w-20 rounded-2xl border px-3 py-2"
                      />

                      <button
                          onClick={() => handleRemove(item.id)}
                          className="rounded-2xl border px-4 py-2 text-sm"
                      >
                        Remove
                      </button>
                    </div>
                  </div>
                </Card>
            ))}
          </div>

          <Card className="h-fit">
            <h3 className="text-lg font-semibold">Summary</h3>

            <div className="mt-4 space-y-3 text-sm text-slate-600">
              <div className="flex justify-between">
                <span>Items</span>
                <span>{items.length}</span>
              </div>

              <div className="flex justify-between">
                <span>Total</span>
                <span>${total}</span>
              </div>
            </div>

            <button
                onClick={handleCreateOrder}
                className="mt-6 w-full rounded-2xl bg-slate-900 px-4 py-3 text-white"
            >
              Create order
            </button>

            <button
                onClick={handleClearCart}
                className="mt-3 w-full rounded-2xl border px-4 py-3"
            >
              Clear cart
            </button>
          </Card>
        </div>
      </div>
  );
}