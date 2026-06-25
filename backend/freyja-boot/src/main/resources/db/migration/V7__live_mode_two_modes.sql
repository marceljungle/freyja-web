-- Freyja V7: two live-mode modes — bounded (a firmware-timed window, tracked by
-- live_mode_until) and persistent (kept alive by the backend, live_persistent).
-- Rename the previous boolean to live_persistent and add the bounded deadline.
ALTER TABLE devices
    RENAME COLUMN live_mode_enabled TO live_persistent;

ALTER TABLE devices
    ADD COLUMN live_mode_until TIMESTAMPTZ;
