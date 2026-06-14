import { batteryTone } from "@/shared/format";

interface Props {
  percent: number | null;
  mv: number | null;
}

export function BatteryBadge({ percent, mv }: Props) {
  const label = percent == null ? "—" : `${percent}%`;
  return (
    <span
      className={`inline-flex items-center gap-1 rounded-full px-2 py-0.5 text-xs font-medium ${batteryTone(percent)}`}
      title={mv != null ? `${mv} mV` : "No battery reading"}
    >
      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <rect x="2" y="7" width="16" height="10" rx="2" />
        <line x1="22" y1="11" x2="22" y2="13" />
      </svg>
      {label}
    </span>
  );
}
