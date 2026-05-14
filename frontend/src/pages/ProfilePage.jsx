import { useEffect, useState } from "react";
import { PageTitle } from "../components/layout/PageTitle.jsx";
import { Card } from "../components/ui/Card.jsx";
import { getUserById, updateUser } from "../services/userService.js";

export function ProfilePage() {
  const rawUser = localStorage.getItem("user");
  const currentUser = rawUser ? JSON.parse(rawUser) : null;

  const [user, setUser] = useState(null);
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [mail, setMail] = useState("");
  const [companyName, setCompanyName] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    async function loadProfile() {
      if (!currentUser) {
        setLoading(false);
        setError("Сначала войдите в аккаунт");
        return;
      }

      try {
        const data = await getUserById(currentUser.id);
        setUser(data);
        setFirstName(data.firstName || "");
        setLastName(data.lastName || "");
        setMail(data.mail || "");
        setCompanyName(data.companyName || "");
      } catch (err) {
        console.error(err);
        setError("Не удалось загрузить профиль");
      } finally {
        setLoading(false);
      }
    }

    loadProfile();
  }, []);

  async function handleSave() {
    if (!user) return;

    setError("");
    setSuccess("");

    try {
      const updatedUser = await updateUser(user.id, {
        ...user,
        firstName,
        lastName,
        mail,
        companyName,
      });

      setUser(updatedUser);
      localStorage.setItem("user", JSON.stringify(updatedUser));
      setSuccess("Профиль обновлен");
    } catch (err) {
      console.error(err);
      setError("Не удалось обновить профиль");
    }
  }

  if (loading) {
    return <p>Загрузка профиля...</p>;
  }

  if (error && !user) {
    return <p className="text-red-600">{error}</p>;
  }

  return (
      <div>
        <PageTitle title="Profile" subtitle="View and edit user data." />

        <div className="grid gap-6 lg:grid-cols-[2fr_1fr]">
          <Card>
            <div className="grid gap-4 md:grid-cols-2">
              <label className="block space-y-2">
                <span className="text-sm font-medium text-slate-700">First name</span>
                <input
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                    className="w-full rounded-2xl border px-4 py-3"
                />
              </label>

              <label className="block space-y-2">
                <span className="text-sm font-medium text-slate-700">Last name</span>
                <input
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                    className="w-full rounded-2xl border px-4 py-3"
                />
              </label>

              <label className="block space-y-2">
                <span className="text-sm font-medium text-slate-700">Email</span>
                <input
                    type="email"
                    value={mail}
                    onChange={(e) => setMail(e.target.value)}
                    className="w-full rounded-2xl border px-4 py-3"
                />
              </label>

              <label className="block space-y-2">
                <span className="text-sm font-medium text-slate-700">Role</span>
                <input
                    value={user?.role || ""}
                    disabled
                    className="w-full rounded-2xl border px-4 py-3 bg-slate-100"
                />
              </label>

              {user?.role === "SELLER" && (
                  <label className="block space-y-2 md:col-span-2">
                    <span className="text-sm font-medium text-slate-700">Company name</span>
                    <input
                        value={companyName}
                        onChange={(e) => setCompanyName(e.target.value)}
                        className="w-full rounded-2xl border px-4 py-3"
                    />
                  </label>
              )}
            </div>

            {error && <p className="mt-4 text-sm text-red-600">{error}</p>}
            {success && <p className="mt-4 text-sm text-green-600">{success}</p>}

            <button
                onClick={handleSave}
                className="mt-6 rounded-2xl bg-slate-900 px-5 py-3 text-white"
            >
              Save changes
            </button>
          </Card>

          <Card>
            <h3 className="text-lg font-semibold">Quick actions</h3>
            <div className="mt-4 space-y-3">
              <button
                  onClick={() => (window.location.href = "/orders")}
                  className="w-full rounded-2xl border px-4 py-3 text-left"
              >
                View orders
              </button>

              <button
                  onClick={() => (window.location.href = "/cart")}
                  className="w-full rounded-2xl border px-4 py-3 text-left"
              >
                Open cart
              </button>

              <button
                  onClick={() => {
                    localStorage.removeItem("user");
                    window.location.href = "/login";
                  }}
                  className="w-full rounded-2xl border px-4 py-3 text-left"
              >
                Logout
              </button>
            </div>
          </Card>
        </div>
      </div>
  );
}