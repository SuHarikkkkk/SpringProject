import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { AppShell } from "./components/layout/AppShell.jsx";
import { AdminDashboard } from "./pages/AdminDashboard.jsx";
import { CartPage } from "./pages/CartPage.jsx";
import { CatalogPage } from "./pages/CatalogPage.jsx";
import { LoginPage } from "./pages/LoginPage.jsx";
import { OrdersPage } from "./pages/OrdersPage.jsx";
import { ProductPage } from "./pages/ProductPage.jsx";
import { ProfilePage } from "./pages/ProfilePage.jsx";
import { RegisterPage } from "./pages/RegisterPage.jsx";
import { SellerProductsPage } from "./pages/SellerProductsPage.jsx";

function AppRoutes() {
  return (
      <AppShell>
        <Routes>
          <Route path="/" element={<CatalogPage />} />
          <Route path="/products/:id" element={<ProductPage />} />

          <Route path="/cart" element={<CartPage />} />
          <Route path="/orders" element={<OrdersPage />} />

          <Route path="/seller/products" element={<SellerProductsPage />} />

          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/admin" element={<AdminDashboard />} />

          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AppShell>
  );
}

export default function App() {
  return (
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
  );
}