-- Freyja V6: live mode is now persistent (on until turned off), kept alive by a
-- backend scheduler that re-sends live_on. Replace the auto-off deadline with an
-- enabled flag plus the requested update interval.
ALTER TABLE devices
    DROP COLUMN live_mode_until,
    ADD COLUMN live_mode_enabled  BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN live_mode_interval INTEGER;
