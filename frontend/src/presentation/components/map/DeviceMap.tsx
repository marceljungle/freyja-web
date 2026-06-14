import { useEffect, useMemo } from "react";
import { MapContainer, Marker, Polyline, Popup, TileLayer, useMap } from "react-leaflet";
import type { LatLngExpression } from "leaflet";
import type { Telemetry } from "@/domain/telemetry";
import { formatDateTime } from "@/shared/format";
import "./leafletSetup";

const DEFAULT_CENTER: LatLngExpression = [20, 0];
const DEFAULT_ZOOM = 2;
const FOCUS_ZOOM = 15;

function Recenter({ center, zoom }: { center: LatLngExpression | null; zoom: number }) {
  const map = useMap();
  useEffect(() => {
    if (center) map.setView(center, zoom, { animate: true });
  }, [center, zoom, map]);
  return null;
}

interface Props {
  latest: Telemetry | null;
  trajectory?: Telemetry[];
}

export function DeviceMap({ latest, trajectory = [] }: Props) {
  // Chronological list of fixes (backend returns newest-first).
  const points = useMemo<LatLngExpression[]>(
    () =>
      [...trajectory]
        .reverse()
        .filter((t) => t.hasFix && t.latitude != null && t.longitude != null)
        .map((t) => [t.latitude as number, t.longitude as number]),
    [trajectory],
  );

  const latestPoint: LatLngExpression | null =
    latest?.hasFix && latest.latitude != null && latest.longitude != null
      ? [latest.latitude, latest.longitude]
      : null;

  const center = latestPoint ?? points[points.length - 1] ?? null;

  return (
    <MapContainer center={DEFAULT_CENTER} zoom={DEFAULT_ZOOM} scrollWheelZoom className="h-full w-full">
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      <Recenter center={center} zoom={center ? FOCUS_ZOOM : DEFAULT_ZOOM} />
      {points.length > 1 && <Polyline positions={points} pathOptions={{ color: "#1f63c4", weight: 3 }} />}
      {latestPoint && (
        <Marker position={latestPoint}>
          <Popup>
            <div className="text-xs">
              <div className="font-semibold">Last fix</div>
              <div>{formatDateTime(latest?.deviceTime ?? latest?.receivedAt)}</div>
              {latest?.accuracy != null && <div>± {Math.round(latest.accuracy)} m</div>}
            </div>
          </Popup>
        </Marker>
      )}
    </MapContainer>
  );
}
