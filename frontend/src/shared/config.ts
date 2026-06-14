/** Base URL for the REST API. Defaults to "/api" (Vite dev proxy / Nginx in prod). */
export const API_BASE_URL: string = import.meta.env.VITE_API_BASE_URL ?? "/api";

/** How often the dashboard refetches the latest position (ms). */
export const TELEMETRY_POLL_INTERVAL_MS = 10_000;

/** Default serial baud rate matching the firmware console (115200 8N1). */
export const SERIAL_BAUD_RATE = 115_200;
