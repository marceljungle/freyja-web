-- Freyja V5: live (real-time) mode replaces the request-location command queue.
-- Track the live-mode auto-off deadline on the device, and drop the now-unused
-- device_commands table (the request/shadow command flow has been removed).
ALTER TABLE devices
    ADD COLUMN live_mode_until TIMESTAMPTZ;

DROP TABLE IF EXISTS device_commands;
