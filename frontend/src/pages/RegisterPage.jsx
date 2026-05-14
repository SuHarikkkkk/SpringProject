import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { UserPlus } from "lucide-react";
import { PageTitle } from "../components/layout/PageTitle.jsx";
import { Card } from "../components/ui/Card.jsx";
import { register } from "../services/authService.js";

export function RegisterPage() {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [mail, setMail] = useState("");
  const [hashedPassword, setHashedPassword] = useState("");
  const [role, setRole] = useState("CUSTOMER");
  const [companyName, setCompanyName] = useState("");
  const [error, setError] = useState("");

  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");

    try {
      const payload = {
        firstName,
        lastName,
        mail,
        hashedPassword,
        role,
        companyName: role === "SELLER" ? companyName : null,
      };

      const user = await register(payload);
      localStorage.setItem("user", JSON.stringify(user));
      navigate("/");
    } catch (err) {
      console.error(err);
      setError("Не удалось зарегистрироваться");
    }
  }

  return (
      <div className="mx-auto max-w-xl">
        <PageTitle title="Register" subtitle="Create customer or seller account." />

        <Card>
          <form onSubmit={handleSubmit} className="grid gap-4 md:grid-cols-2">
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
              <span className="text-sm font-medium text-slate-700">Password</span>
              <input
                  type="password"
                  value={hashedPassword}
                  onChange={(e) => setHashedPassword(e.target.value)}
                  className="w-full rounded-2xl border px-4 py-3"
              />
            </label>

            <label className="block space-y-2">
              <span className="text-sm font-medium text-slate-700">Role</span>
              <select
                  value={role}
                  onChange={(e) => setRole(e.target.value)}
                  className="w-full rounded-2xl border px-4 py-3"
              >
                <option value="CUSTOMER">CUSTOMER</option>
                <option value="SELLER">SELLER</option>
              </select>
            </label>

            <label className="block space-y-2">
              <span className="text-sm font-medium text-slate-700">Company name</span>
              <input
                  value={companyName}
                  onChange={(e) => setCompanyName(e.target.value)}
                  disabled={role !== "SELLER"}
                  className="w-full rounded-2xl border px-4 py-3 disabled:bg-slate-100"
              />
            </label>

            {error && (
                <p className="md:col-span-2 text-sm text-red-600">{error}</p>
            )}

            <div className="md:col-span-2">
              <button className="mt-2 flex items-center gap-2 rounded-2xl bg-slate-900 px-5 py-3 text-white">
                <UserPlus className="h-4 w-4" />
                Create account
              </button>
            </div>
          </form>
        </Card>
      </div>
  );
}