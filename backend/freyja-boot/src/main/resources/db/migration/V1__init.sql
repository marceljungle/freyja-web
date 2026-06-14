-- Freyja initial schema
-- Self-hosted IoT asset tracker: users, devices, telemetry, async command queue.

CREATE
EXTENSION IF NOT EXISTS pgcrypto;

-- ---------------------------------------------------------------------------
-- Users
-- ---------------------------------------------------------------------------
CREATE TABLE users
(
    id            UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name  VARCHAR(120),
    role          VARCHAR(32)  NOT NULL DEFAULT 'USER',
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX idx_users_email ON users (lower(email));

-- ---------------------------------------------------------------------------
-- Devices (owned by a user; identified by the firmware IMEI)
-- ---------------------------------------------------------------------------
CREATE TABLE devices
(
    id           UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    imei         VARCHAR(20)  NOT NULL,
    name         VARCHAR(120) NOT NULL,
    fw_version   VARCHAR(32),
    owner_id     UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    apn          VARCHAR(64),
    broker_ip    VARCHAR(64),
    broker_port  INTEGER,
    last_seen_at TIMESTAMPTZ,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX idx_devices_imei ON devices (imei);
CREATE INDEX idx_devices_owner ON devices (owner_id);

-- ---------------------------------------------------------------------------
-- Telemetry (time-series of position fixes / status reports)
-- ---------------------------------------------------------------------------
CREATE TABLE telemetry
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    device_id   UUID        NOT NULL REFERENCES devices (id) ON DELETE CASCADE,
    reason      VARCHAR(32),
    has_fix     BOOLEAN     NOT NULL,
    latitude    DOUBLE PRECISION,
    longitude   DOUBLE PRECISION,
    accuracy    DOUBLE PRECISION,
    battery_mv  INTEGER,
    device_time TIMESTAMPTZ,
    received_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_telemetry_device_time ON telemetry (device_id, received_at DESC);

-- ---------------------------------------------------------------------------
-- Device commands (Flow 4: async command queue / device shadow)
-- ---------------------------------------------------------------------------
CREATE TABLE device_commands
(
    id              UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    device_id       UUID        NOT NULL REFERENCES devices (id) ON DELETE CASCADE,
    type            VARCHAR(32) NOT NULL,
    status          VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    payload         TEXT,
    issued_by       UUID        REFERENCES users (id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    sent_at         TIMESTAMPTZ,
    acknowledged_at TIMESTAMPTZ
);

CREATE INDEX idx_commands_device_status ON device_commands (device_id, status);
