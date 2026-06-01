import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { PageTitle } from "../components/layout/PageTitle.jsx";
import { Card } from "../components/ui/Card.jsx";
import { getProductById } from "../services/productService.js";
import { addItemToCart } from "../services/cartService.js";

export function ProductPage() {
  const { id } = useParams();

  const [product, setProduct] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    setLoading(true);
    setError("");

    getProductById(id)
        .then((data) => {
          setProduct(data);
        })
        .catch((err) => {
          console.error(err);
          setError("Не удалось загрузить товар");
        })
        .finally(() => {
          setLoading(false);
        });
  }, [id]);

  async function handleAddToCart() {
    const rawUser = localStorage.getItem("user");

    if (!rawUser) {
      alert("Сначала войдите в аккаунт");
      return;
    }

    const user = JSON.parse(rawUser);

    try {
      await addItemToCart(user.id, product.id, quantity);
      alert("Товар добавлен в корзину");
    } catch (err) {
      console.error(err);
      alert("Не удалось добавить товар в корзину");
    }
  }

  if (loading) {
    return <p>Загрузка товара...</p>;
  }

  if (error) {
    return <p className="text-red-600">{error}</p>;
  }

  if (!product) {
    return <p>Товар не найден</p>;
  }

  const rawUser = localStorage.getItem("user");
  const user = rawUser ? JSON.parse(rawUser) : null;

  const isSeller = user?.role === "SELLER";

  return (
      <div>
        <PageTitle title="Product Details" subtitle="Detailed page for a selected cosmetic product." />

        <div className="grid gap-6 lg:grid-cols-2">
          <Card>
            <img
                src={product.imageUrl || "https://placehold.co/600x500?text=No+Image"}
                alt={product.name || "Product image"}
                className="h-96 w-full rounded-3xl object-cover"
                onError={(e) => {
                  e.currentTarget.src = "https://placehold.co/600x500?text=No+Image";
                }}
            />
          </Card>

          <Card>
            <p className="mb-2 text-sm text-slate-500">
              {product.category?.name || "No category"}
            </p>

            <h2 className="text-3xl font-semibold">{product.name || "Без названия"}</h2>

            <p className="mt-3 text-slate-600">
              {product.description || "Описание отсутствует"}
            </p>

            <p className="mt-5 text-2xl font-semibold">${product.price ?? 0}</p>

            {!isSeller && (
                <>
                  <div className="mt-6 w-40">
                    <label className="block space-y-2">
                      <span className="text-sm font-medium text-slate-700">Quantity</span>
                      <input
                          type="number"
                          min="1"
                          value={quantity}
                          onChange={(e) => setQuantity(Number(e.target.value))}
                          className="w-full rounded-2xl border px-4 py-3"
                      />
                    </label>
                  </div>

                  <div className="mt-6 flex gap-3">
                    <button
                        onClick={handleAddToCart}
                        className="rounded-2xl bg-slate-900 px-5 py-3 text-white"
                    >
                      Add to cart
                    </button>
                  </div>
                </>
            )}

            <div className="mt-6 flex gap-3">
              {!isSeller && (
                  <Button onClick={handleAddToCart}>
                    Add to cart
                  </Button>
              )}

            </div>
          </Card>
        </div>
      </div>
  );
}