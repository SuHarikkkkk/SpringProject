export function Field({ label, type = "text", placeholder }) {
  return (
    <label className="block space-y-2">
      <span className="text-sm font-medium text-slate-700">{label}</span>
      <input
        type={type}
        placeholder={placeholder}
        className="w-full rounded-2xl border px-4 py-3 outline-none transition focus:ring-2"
      />
    </label>
  );
}
