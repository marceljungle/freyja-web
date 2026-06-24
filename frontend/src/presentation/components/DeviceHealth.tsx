import type { Telemetry } from "@/domain/telemetry";

interface Props {
  latest: Telemetry | null;
}

type Tone = "good" | "fair" | "poor" | "muted";

const TONE_BADGE: Record<Tone, string> = {
  good: "bg-emerald-100 text-emerald-700",
  fair: "bg-amber-100 text-amber-700",
  poor: "bg-red-100 text-red-700",
  muted: "bg-slate-100 text-slate-500",
};

const TONE_BAR: Record<Tone, string> = {
  good: "bg-emerald-500",
  fair: "bg-amber-500",
  poor: "bg-red-500",
  muted: "bg-slate-300",
};

function clampPct(value: number): number {
  return Math.max(0, Math.min(100, value));
}

// RSRP (dBm): ~-80 excellent, ~-110 terrible.
function rsrpQuality(rsrp: number): { label: string; tone: Tone; pct: number } {
  const pct = clampPct(((rsrp + 115) / 45) * 100); // -115 dBm -> 0%, -70 dBm -> 100%
  if (rsrp >= -90) return { label: "Excellent", tone: "good", pct };
  if (rsrp >= -100) return { label: "Good", tone: "good", pct };
  if (rsrp >= -110) return { label: "Fair", tone: "fair", pct };
  return { label: "Poor", tone: "poor", pct };
}

// C/N0 (dB-Hz): >35 healthy.
function cn0Quality(cn0: number): { label: string; tone: Tone; pct: number } {
  const pct = clampPct(((cn0 - 15) / 30) * 100); // 15 dB-Hz -> 0%, 45 dB-Hz -> 100%
  if (cn0 >= 35) return { label: "Healthy", tone: "good", pct };
  if (cn0 >= 28) return { label: "Fair", tone: "fair", pct };
  return { label: "Weak", tone: "poor", pct };
}

function Badge({ tone, children }: { tone: Tone; children: React.ReactNode }) {
  return (
    <span className={`rounded-full px-2 py-0.5 text-[11px] font-medium ${TONE_BADGE[tone]}`}>
      {children}
    </span>
  );
}

function Bar({ pct, tone }: { pct: number; tone: Tone }) {
  return (
    <div className="h-1.5 w-full overflow-hidden rounded-full bg-slate-100">
      <div className={`h-full rounded-full ${TONE_BAR[tone]}`} style={{ width: `${pct}%` }} />
    </div>
  );
}

export function DeviceHealth({ latest }: Props) {
  if (!latest) return null;

  const hasHealth =
    latest.rsrp != null ||
    latest.trackedSvs != null ||
    latest.svsUsed != null ||
    latest.cn0 != null;

  const rsrp = latest.rsrp != null ? rsrpQuality(latest.rsrp) : null;
  const cn0 = latest.cn0 != null ? cn0Quality(latest.cn0) : null;
  const satTone: Tone =
    latest.svsUsed == null ? "muted" : latest.svsUsed >= 4 ? "good" : latest.svsUsed >= 1 ? "fair" : "poor";

  return (
    <div className="card p-4 text-sm">
      <div className="mb-3 flex items-center justify-between">
        <h2 className="text-sm font-semibold text-slate-700">Device health</h2>
        {latest.buffered && (
          <span
            className="rounded-full bg-violet-100 px-2 py-0.5 text-[11px] font-medium text-violet-700"
            title="Replayed from the device's offline buffer"
          >
            buffered
          </span>
        )}
      </div>

      {!hasHealth ? (
        <p className="text-slate-400">No health data in the last report.</p>
      ) : (
        <div className="space-y-4">
          {/* LTE signal */}
          <div>
            <div className="mb-1 flex items-center justify-between">
              <span className="text-slate-500">LTE signal</span>
              <span className="flex items-center gap-2">
                <span className="font-medium text-slate-700">
                  {latest.rsrp != null ? `${latest.rsrp} dBm` : "—"}
                </span>
                {rsrp && <Badge tone={rsrp.tone}>{rsrp.label}</Badge>}
              </span>
            </div>
            <Bar pct={rsrp?.pct ?? 0} tone={rsrp?.tone ?? "muted"} />
          </div>

          {/* GNSS satellites */}
          <div className="flex items-center justify-between">
            <span className="text-slate-500">Satellites (used / visible)</span>
            <span className="flex items-center gap-2">
              <span className="font-medium text-slate-700">
                {latest.svsUsed ?? "—"} / {latest.trackedSvs ?? "—"}
              </span>
              <span className={`h-2 w-2 rounded-full ${TONE_BAR[satTone]}`} aria-hidden />
            </span>
          </div>

          {/* GNSS carrier-to-noise */}
          <div>
            <div className="mb-1 flex items-center justify-between">
              <span className="text-slate-500">GNSS C/N₀</span>
              <span className="flex items-center gap-2">
                <span className="font-medium text-slate-700">
                  {latest.cn0 != null ? `${latest.cn0.toFixed(1)} dB-Hz` : "—"}
                </span>
                {cn0 && <Badge tone={cn0.tone}>{cn0.label}</Badge>}
              </span>
            </div>
            <Bar pct={cn0?.pct ?? 0} tone={cn0?.tone ?? "muted"} />
          </div>
        </div>
      )}
    </div>
  );
}
