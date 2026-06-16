interface Props {
  celsius: number | null;
}

export function TemperatureBadge({ celsius }: Props) {
  if (celsius == null) return null;
  return (
    <span
      className="inline-flex items-center gap-1 rounded-full bg-sky-100 px-2 py-0.5 text-xs font-medium text-sky-700"
      title="Temperature"
    >
      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M14 14.76V5a2 2 0 0 0-4 0v9.76a4 4 0 1 0 4 0Z" />
      </svg>
      {celsius.toFixed(1)} °C
    </span>
  );
}
