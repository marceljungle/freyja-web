import type { TrajectoryQuery } from "@/domain/telemetry";

interface Preset {
  key: string;
  label: string;
  hours: number;
}

const PRESETS: Preset[] = [
  { key: "1h", label: "1 hour", hours: 1 },
  { key: "6h", label: "6 hours", hours: 6 },
  { key: "24h", label: "24 hours", hours: 24 },
  { key: "7d", label: "7 days", hours: 24 * 7 },
];

export const DEFAULT_RANGE = "24h";

export function trajectoryRange(presetKey: string): TrajectoryQuery {
  const preset = PRESETS.find((p) => p.key === presetKey) ?? PRESETS[2];
  const to = new Date();
  const from = new Date(to.getTime() - preset.hours * 60 * 60 * 1000);
  return { from: from.toISOString(), to: to.toISOString(), limit: 2000 };
}

interface Props {
  value: string;
  onChange: (key: string) => void;
}

export function TimeRangeFilter({ value, onChange }: Props) {
  return (
    <div className="inline-flex rounded-lg border border-slate-200 bg-white p-0.5">
      {PRESETS.map((preset) => (
        <button
          key={preset.key}
          onClick={() => onChange(preset.key)}
          className={`rounded-md px-3 py-1 text-xs font-medium transition ${
            value === preset.key
              ? "bg-freyja-500 text-white"
              : "text-slate-600 hover:bg-slate-100"
          }`}
        >
          {preset.label}
        </button>
      ))}
    </div>
  );
}
