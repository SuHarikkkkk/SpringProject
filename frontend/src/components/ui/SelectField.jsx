export function SelectField({ label, options }) {
  return (
    <label className="block space-y-2">
      <span className="text-sm font-medium text-slate-700">{label}</span>
      <select className="w-full rounded-2xl border px-4 py-3 outline-none transition focus:ring-2">
        {options.map((option) => (
          <option key={option} value={option}>{option}</option>
        ))}
      </select>
    </label>
  );
}
