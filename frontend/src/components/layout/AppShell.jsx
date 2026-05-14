import { Link, useLocation } from "react-router-dom";
import { ShoppingCart, Package, User, Shield } from "lucide-react";

export function AppShell({ children }) {
  const location = useLocation();

  const rawUser = localStorage.getItem("user");
  const user = rawUser ? JSON.parse(rawUser) : null;

  const nav = [
    { to: "/", label: "Catalog", icon: Package },
  ];

  if (user?.role === "CUSTOMER") {
    nav.push(
        { to: "/cart", label: "Cart", icon: ShoppingCart },
        { to: "/orders", label: "Orders", icon: Package }
    );
  }

  if (user?.role === "SELLER") {
    nav.push(
        { to: "/seller/products", label: "My Products", icon: Package }
    );
  }

  if (user?.role === "ADMIN") {
    nav.push(
        { to: "/admin", label: "Admin", icon: Shield }
    );
  }

  if (user) {
    nav.push(
        { to: "/profile", label: "Profile", icon: User }
    );
  }

  return (
      <div className="min-h-screen bg-slate-50 text-slate-900">
        <header className="sticky top-0 z-20 border-b bg-white/90 backdrop-blur">
          <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3">
            <Link to="/" className="text-xl font-semibold tracking-tight">
              Cosmetics Shop
            </Link>

            <div className="hidden gap-2 md:flex">
              {nav.map(({ to, label, icon: Icon }) => {
                const active =
                    location.pathname === to ||
                    (to !== "/" && location.pathname.startsWith(to));

                return (
                    <Link
                        key={to}
                        to={to}
                        className={`flex items-center gap-2 rounded-2xl px-4 py-2 text-sm shadow-sm transition ${
                            active
                                ? "bg-slate-900 text-white"
                                : "bg-slate-100 hover:bg-slate-200"
                        }`}
                    >
                      <Icon className="h-4 w-4" />
                      {label}
                    </Link>
                );
              })}
            </div>

            <div className="flex gap-2">
              {!user && (
                  <>
                    <Link
                        to="/login"
                        className="rounded-2xl border px-4 py-2 text-sm"
                    >
                      Login
                    </Link>

                    <Link
                        to="/register"
                        className="rounded-2xl bg-slate-900 px-4 py-2 text-sm text-white"
                    >
                      Register
                    </Link>
                  </>
              )}

              {user && (
                  <button
                      onClick={() => {
                        localStorage.removeItem("user");
                        window.location.href = "/login";
                      }}
                      className="rounded-2xl border px-4 py-2 text-sm"
                  >
                    Logout
                  </button>
              )}
            </div>
          </div>
        </header>

        <main className="mx-auto max-w-7xl px-4 py-6">{children}</main>
      </div>
  );
}