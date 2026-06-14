import { formatRelative } from "@/shared/format";

/** A device is considered "online" if it reported within the last 15 minutes. */
const ONLINE_THRESHOLD_MS = 15 * 60 * 1000;

export function DeviceStatus({ lastSeenAt }: { lastSeenAt: string | null }) {
  const online =
    lastSeenAt != null && Date.now() - new Date(lastSeenAt).getTime() < ONLINE_THRESHOLD_MS;
  return (
    <span className="inline-flex items-center gap-1.5 text-xs text-slate-500">
      <span
        className={`h-2 w-2 rounded-full ${online ? "bg-emerald-500" : "bg-slate-300"}`}
        aria-hidden
      />
      {online ? "Online" : `Seen ${formatRelative(lastSeenAt)}`}
    </span>
  );
}
