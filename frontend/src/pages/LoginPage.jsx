import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { LogIn } from "lucide-react";
import { PageTitle } from "../components/layout/PageTitle.jsx";
import { Card } from "../components/ui/Card.jsx";
import { login } from "../services/authService.js";

export function LoginPage() {
    const [mail, setMail] = useState("");
    const [hashedPassword, setHashedPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");

        try {
            const user = await login(mail, hashedPassword);
            localStorage.setItem("user", JSON.stringify(user));
            navigate("/");
        } catch (err) {
            console.error(err);
            setError("Неверный логин или пароль");
        }
    }

    return (
        <div className="mx-auto max-w-md">
            <PageTitle title="Login" subtitle="Sign in to customer, seller, or admin account." />
            <Card>
                <form className="space-y-4" onSubmit={handleSubmit}>
                    <label className="block space-y-2">
                        <span className="text-sm font-medium text-slate-700">Email</span>
                        <input
                            value={mail}
                            onChange={(e) => setMail(e.target.value)}
                            type="email"
                            className="w-full rounded-2xl border px-4 py-3"
                        />
                    </label>

                    <label className="block space-y-2">
                        <span className="text-sm font-medium text-slate-700">Password</span>
                        <input
                            value={hashedPassword}
                            onChange={(e) => setHashedPassword(e.target.value)}
                            type="password"
                            className="w-full rounded-2xl border px-4 py-3"
                        />
                    </label>

                    {error && <p className="text-sm text-red-600">{error}</p>}

                    <button className="flex w-full items-center justify-center gap-2 rounded-2xl bg-slate-900 px-5 py-3 text-white">
                        <LogIn className="h-4 w-4" />
                        Login
                    </button>
                </form>
            </Card>
        </div>
    );
}