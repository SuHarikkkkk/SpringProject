import { Link } from "react-router-dom";
import { Card } from "../ui/Card.jsx";
import { addItemToCart } from "../../services/cartService.js";

export function ProductRow({ product, isSeller = false }) {
  async function handleAddToCart() {
    const rawUser = localStorage.getItem("user");

    if (!rawUser) {
      alert("Сначала войдите в аккаунт");
      return;
    }

    const user = JSON.parse(rawUser);

    try {
      await addItemToCart(user.id, product.id, 1);
      alert("Товар добавлен в корзину");
    } catch (err) {
      console.error(err);
      alert("Не удалось добавить товар в корзину");
    }
  }

  return (
      <Card>
        <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div className="flex gap-4">
            <img
                src={product.imageUrl || "https://placehold.co/300x200?text=No+Image"}
                alt={product.name}
                className="h-28 w-full rounded-2xl object-cover md:w-36"
                onError={(e) => {
                  e.currentTarget.src = "https://placehold.co/300x200?text=No+Image";
                }}
            />

            <div className="flex-1">
              <div className="mb-2 flex flex-wrap items-center gap-2">
                <h3 className="text-lg font-semibold">{product.name || "Без названия"}</h3>
                <span className="rounded-full bg-slate-100 px-3 py-1 text-xs">
                {product.categoryName || "No category"}
              </span>
              </div>

              <p className="text-sm text-slate-600">
                {product.description || "Описание отсутствует"}
              </p>
            </div>
          </div>

          <div className="flex flex-col items-start gap-3 md:items-end">
            <span className="text-xl font-semibold">${product.price ?? 0}</span>

            <div className="flex gap-2">
              <Link
                  to={`/products/${product.id}`}
                  className="rounded-2xl border px-4 py-2 text-sm"
              >
                View
              </Link>

              {!isSeller && (
                  <button
                      onClick={handleAddToCart}
                      className="rounded-2xl bg-slate-900 px-4 py-2 text-sm text-white"
                  >
                    Add to cart
                  </button>
              )}
            </div>
          </div>
        </div>
      </Card>
  );
}