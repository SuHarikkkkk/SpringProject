export function PageTitle({ title, subtitle }) {
  return (
    <div className="mb-6">
      <h1 className="text-3xl font-semibold tracking-tight">{title}</h1>
      {subtitle && <p className="mt-2 text-sm text-slate-600">{subtitle}</p>}
    </div>
  );
}
