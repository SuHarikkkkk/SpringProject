export function Card({ children, className = "" }) {
  return <div className={`rounded-3xl border bg-white p-5 shadow-sm ${className}`}>{children}</div>;
}
