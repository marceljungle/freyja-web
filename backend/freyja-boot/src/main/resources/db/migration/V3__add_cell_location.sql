-- Freyja V3: serving-cell identifiers (for cell-tower location fallback) and a
-- flag marking coordinates that were resolved from a cell tower rather than GPS.
ALTER TABLE telemetry
    ADD COLUMN approximate BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN mcc         INTEGER,
    ADD COLUMN mnc         INTEGER,
    ADD COLUMN tac         INTEGER,
    ADD COLUMN cell_id     INTEGER;
