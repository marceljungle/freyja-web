import { useEffect, useMemo } from "react";
import { Circle, MapContainer, Marker, Polyline, Popup, TileLayer, useMap } from "react-leaflet";
import type { LatLngExpression } from "leaflet";
import type { Telemetry } from "@/domain/telemetry";
import { formatDateTime } from "@/shared/format";
import "./leafletSetup";

const DEFAULT_CENTER: LatLngExpression = [20, 0];
const DEFAULT_ZOOM = 2;
const FOCUS_ZOOM = 15;
const APPROX_ZOOM = 11;
const DEFAULT_APPROX_RADIUS_M = 1000;

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
  // Chronological list of GPS fixes (backend returns newest-first).
  const points = useMemo<LatLngExpression[]>(
    () =>
      [...trajectory]
        .reverse()
        .filter((t) => t.hasFix && t.latitude != null && t.longitude != null)
        .map((t) => [t.latitude as number, t.longitude as number]),
    [trajectory],
  );

  const hasCoords = latest != null && latest.latitude != null && latest.longitude != null;
  const fixPoint: LatLngExpression | null =
    hasCoords && latest!.hasFix ? [latest!.latitude!, latest!.longitude!] : null;
  const approxPoint: LatLngExpression | null =
    hasCoords && !latest!.hasFix && latest!.approximate
      ? [latest!.latitude!, latest!.longitude!]
      : null;

  const center = fixPoint ?? approxPoint ?? points[points.length - 1] ?? null;
  const zoom = fixPoint ? FOCUS_ZOOM : approxPoint ? APPROX_ZOOM : DEFAULT_ZOOM;

  return (
    <MapContainer center={DEFAULT_CENTER} zoom={DEFAULT_ZOOM} scrollWheelZoom className="h-full w-full">
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      <Recenter center={center} zoom={center ? zoom : DEFAULT_ZOOM} />
      {points.length > 1 && <Polyline positions={points} pathOptions={{ color: "#1f63c4", weight: 3 }} />}
      {fixPoint && (
        <Marker position={fixPoint}>
          <Popup>
            <div className="text-xs">
              <div className="font-semibold">Last fix (GPS)</div>
              <div>{formatDateTime(latest?.deviceTime ?? latest?.receivedAt)}</div>
              {latest?.accuracy != null && <div>± {Math.round(latest.accuracy)} m</div>}
            </div>
          </Popup>
        </Marker>
      )}
      {approxPoint && (
        <Circle
          center={approxPoint}
          radius={latest?.accuracy ?? DEFAULT_APPROX_RADIUS_M}
          pathOptions={{ color: "#2f7be0", weight: 1, fillColor: "#2f7be0", fillOpacity: 0.15 }}
        >
          <Popup>
            <div className="text-xs">
              <div className="font-semibold">Approximate location</div>
              <div>Resolved from cell tower (no GPS fix)</div>
              <div>~ {Math.round(latest?.accuracy ?? DEFAULT_APPROX_RADIUS_M)} m radius</div>
              <div>{formatDateTime(latest?.receivedAt)}</div>
            </div>
          </Popup>
        </Circle>
      )}
    </MapContainer>
  );
}
