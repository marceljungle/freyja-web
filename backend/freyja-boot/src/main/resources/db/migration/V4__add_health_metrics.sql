-- Freyja V4: device health metrics (LTE signal + GNSS) and the buffered-fix flag.
ALTER TABLE telemetry
    ADD COLUMN rsrp_dbm    INTEGER,
    ADD COLUMN tracked_svs INTEGER,
    ADD COLUMN svs_used    INTEGER,
    ADD COLUMN cn0         DOUBLE PRECISION,
    ADD COLUMN buffered    BOOLEAN NOT NULL DEFAULT FALSE;
