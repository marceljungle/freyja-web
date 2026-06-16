-- Freyja V2: store the firmware temperature reading (temp_c).
ALTER TABLE telemetry
    ADD COLUMN temperature_c DOUBLE PRECISION;
