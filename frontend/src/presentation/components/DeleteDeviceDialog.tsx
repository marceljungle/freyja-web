interface Props {
  deviceName: string;
  pending: boolean;
  error?: string | null;
  onConfirm: () => void;
  onCancel: () => void;
}

export function DeleteDeviceDialog({ deviceName, pending, error, onConfirm, onCancel }: Props) {
  return (
    <div className="fixed inset-0 z-[1000] flex items-center justify-center bg-slate-900/50 p-4">
      <div className="w-full max-w-md rounded-xl bg-white p-6 shadow-xl">
        <h2 className="text-lg font-semibold text-slate-800">Delete device</h2>
        <p className="mt-2 text-sm text-slate-600">
          This permanently deletes <span className="font-semibold">{deviceName}</span> and all of its
          telemetry history and queued commands. This action cannot be undone.
        </p>

        {error && <p className="mt-3 rounded-md bg-red-50 p-3 text-sm text-red-700">{error}</p>}

        <div className="mt-5 flex justify-end gap-3">
          <button
            onClick={onCancel}
            disabled={pending}
            className="rounded-lg border border-slate-200 px-4 py-2 text-sm font-medium text-slate-600 hover:bg-slate-50 disabled:opacity-50"
          >
            Cancel
          </button>
          <button
            onClick={onConfirm}
            disabled={pending}
            className="rounded-lg bg-red-600 px-4 py-2 text-sm font-medium text-white hover:bg-red-700 disabled:opacity-50"
          >
            {pending ? "Deleting…" : "Delete device"}
          </button>
        </div>
      </div>
    </div>
  );
}
