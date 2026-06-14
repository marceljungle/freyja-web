# Freyja Cloud

Self-hosted backend and web dashboard for **Freyja**, a low-power, motion-triggered
IoT asset tracker. This repository is the cloud half of the system; the device
firmware (Zephyr / nRF9151) lives in a separate repository.

- **Backend** — Java 21 + Spring Boot 3.3, strict **hexagonal architecture**
  (Ports & Adapters) across four Maven modules. REST API, PostgreSQL, JWT auth,
  and MQTT (Spring Integration) for telemetry ingest and command dispatch.
- **Frontend** — React + TypeScript SPA (Vite), layered Clean / Feature-Sliced
  structure. TanStack Query, Leaflet + OpenStreetMap (no API keys), and the
  **Web Serial API** for USB device provisioning.
- **Deploy** — Docker Compose for local dependencies and a one-command
  production stack (backend + Nginx-served frontend + PostgreSQL + Mosquitto).

## Architecture

```
                       ┌──────────────────────────── Browser (SPA) ───────────────────────────┐
                       │  presentation → application(hooks) → infrastructure(axios, WebSerial)  │
                       └───────────────┬───────────────────────────────────┬───────────────────┘
                              REST /api │ (JWT)                  Web Serial  │ (USB, get_info/set_config)
                                        ▼                                    ▼
   MQTT  freyja/+/telemetry  ┌──────────────────────┐                 [ Freyja device ]
   ───────────────────────▶  │   Spring Boot backend │  ◀── freyja/{imei}/rx (QoS 1, queued)
                             │  boot │ infrastructure │
                             │  application │ domain   │
                             └─────────┬─────────────┘
                                       ▼
                              PostgreSQL   Mosquitto
```

### Backend layers (dependency direction: boot → infrastructure → application → domain)

| Module                 | Responsibility                                                                 |
| ---------------------- | ------------------------------------------------------------------------------ |
| `freyja-domain`        | Pure Java: entities, value objects, ports (interfaces). **No Spring.**         |
| `freyja-application`   | Use cases (`UseCase<I,O>`); each input self-validates before execution.        |
| `freyja-infrastructure`| Adapters: REST, Spring Security/JWT, JPA, Spring Integration MQTT, wiring.      |
| `freyja-boot`          | Spring Boot entry point, configuration, Flyway migrations.                     |

## Firmware contract (reference)

Telemetry is published by the device at **QoS 1** to `freyja/<imei>/telemetry`:

```jsonc
{"id":"352656100000000","reason":"motion","fix":true,
 "lat":12.345678,"lon":-1.234567,"acc":8.5,
 "utc":"2026-06-13T10:00:00Z","batt_mv":4012}
// no-fix variant omits lat/lon/acc/utc; the server uses its receive time
```

Web Serial onboarding protocol (115200 8N1, newline-delimited JSON):

```jsonc
{"cmd":"get_info"}                                                  → {"imei":"…","fw_version":"1.0.0"}
{"cmd":"set_config","apn":"…","broker_ip":"…","broker_port":1883}   → {"status":"ok"}  // device reboots
```

## Core flows

1. **Onboarding** — register / login (JWT).
2. **Provisioning (Web Serial)** — the browser talks to the device over USB
   (`get_info`, `set_config`), then registers the IMEI to the user via
   `POST /api/devices`.
3. **Telemetry** — the backend subscribes to `freyja/+/telemetry`, stores fixes
   and battery, and the dashboard shows the live position + trajectory on a map.
4. **Async command (device shadow)** — `Request location` publishes to
   `freyja/{imei}/rx` (QoS 1) so Mosquitto queues it for the sleeping device.

## Prerequisites

- JDK 21 (a `.sdkmanrc` pins `21.0.6-librca`), Maven 3.9+
- Node 20+, npm 10+
- Docker + Docker Compose

## Local development

Start dependencies (PostgreSQL + Mosquitto):

```sh
docker compose -f deploy/docker-compose.dev.yml up -d
```

Run the backend (port 8080):

```sh
cd backend
mvn spring-boot:run -pl freyja-boot
```

Run the frontend (port 5173, proxies `/api` → 8080):

```sh
cd frontend
npm install
npm run dev
```

Open http://localhost:5173. Register an account and add a device. (Web Serial
requires Chrome/Edge over `localhost` or HTTPS.)

Simulate a device without hardware:

```sh
docker exec freyja-mosquitto-dev mosquitto_pub -t "freyja/352656100000000/telemetry" -q 1 \
  -m '{"id":"352656100000000","reason":"motion","fix":true,"lat":40.4168,"lon":-3.7038,"acc":6,"utc":"2026-06-14T09:30:00Z","batt_mv":3850}'
```

## Production (one command)

```sh
cp deploy/.env.example deploy/.env      # set POSTGRES_PASSWORD and FREYJA_JWT_SECRET
docker compose -f deploy/docker-compose.prod.yml --env-file deploy/.env up -d --build
```

- Dashboard: http://localhost:8088
- MQTT broker for devices: `:1883`

Generate a JWT secret with `openssl rand -base64 48`.

## API summary

| Method & path                                          | Auth | Description                         |
| ------------------------------------------------------ | ---- | ----------------------------------- |
| `POST /api/auth/register`                              | —    | Create an account                   |
| `POST /api/auth/login`                                 | —    | Obtain a JWT                        |
| `POST /api/devices`                                    | JWT  | Register a provisioned device       |
| `GET  /api/devices`                                    | JWT  | List the user's devices             |
| `GET  /api/devices/{id}`                               | JWT  | Get a device                        |
| `GET  /api/devices/{id}/telemetry/latest`              | JWT  | Latest reading (204 if none)        |
| `GET  /api/devices/{id}/telemetry?from&to&limit`       | JWT  | Trajectory within a time range      |
| `POST /api/devices/{id}/commands/request-location`     | JWT  | Queue a location request (Flow 4)   |

## Project structure

```
freyja-web/
├── backend/        # Maven multi-module (domain, application, infrastructure, boot)
├── frontend/       # Vite React TS SPA (domain, infrastructure, application, presentation)
├── deploy/         # docker-compose.dev.yml, docker-compose.prod.yml, mosquitto/
└── README.md
```

## Notes & limitations

- **Web Serial** is Chromium-only and requires a secure context (localhost or HTTPS).
- **Flow 4 downlink** is implemented backend-side (publish + queue + shadow record).
  Actual delivery depends on the firmware subscribing to `freyja/{imei}/rx` with a
  persistent session — a documented firmware follow-up.
- Telemetry from an **unregistered IMEI is dropped**; provision the device first.
- MQTT transport is plain TCP (matches current firmware). TLS is a future step.
```
