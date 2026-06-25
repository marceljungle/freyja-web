import { useEffect, useMemo, useRef } from "react";
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

// Recenters only when the target coordinates (or zoom) actually change, so a
// no-fix report keeps the last position and manual pan/zoom isn't overridden
// on every telemetry poll.
function Recenter({ lat, lon, zoom }: { lat: number | null; lon: number | null; zoom: number }) {
  const map = useMap();
  useEffect(() => {
    if (lat != null && lon != null) {
      map.setView([lat, lon], zoom, { animate: true });
    }
  }, [lat, lon, zoom, map]);
  return null;
}

interface Props {
  latest: Telemetry | null;
  trajectory?: Telemetry[];
}

export function DeviceMap({ latest, trajectory = [] }: Props) {
  // Chronological list of GPS fixes (backend returns newest-first).
  const points = useMemo<[number, number][]>(
    () =>
      [...trajectory]
        .reverse()
        .filter((t) => t.hasFix && t.latitude != null && t.longitude != null)
        .map((t) => [t.latitude as number, t.longitude as number]),
    [trajectory],
  );

  const livePoint: [number, number] | null =
    latest != null && latest.latitude != null && latest.longitude != null
      ? [latest.latitude, latest.longitude]
      : null;
  const liveFix = livePoint != null && (latest?.hasFix ?? false);
  const liveApprox = livePoint != null && !(latest?.hasFix ?? false) && (latest?.approximate ?? false);

  // Remember the last position we actually had coordinates for, so a no-fix
  // report keeps the map there instead of zooming out to the world.
  const lastKnownRef = useRef<[number, number] | null>(null);
  useEffect(() => {
    if (latest?.latitude != null && latest?.longitude != null) {
      lastKnownRef.current = [latest.latitude, latest.longitude];
    }
  }, [latest?.latitude, latest?.longitude]);

  const lastKnownPoint = lastKnownRef.current ?? (points.length > 0 ? points[points.length - 1] : null);

  // Center target + zoom: never fall back to the world view when we have any
  // known position — a report without a fix should keep the last location.
  let centerLat: number | null = null;
  let centerLon: number | null = null;
  let zoom = DEFAULT_ZOOM;
  if (livePoint) {
    [centerLat, centerLon] = livePoint;
    zoom = liveApprox ? APPROX_ZOOM : FOCUS_ZOOM;
  } else if (lastKnownPoint) {
    [centerLat, centerLon] = lastKnownPoint;
    zoom = FOCUS_ZOOM;
  }

  // When the latest report itself has no location, show a muted marker at the
  // last known position.
  const stalePoint = livePoint == null ? lastKnownPoint : null;

  return (
    <MapContainer center={DEFAULT_CENTER} zoom={DEFAULT_ZOOM} scrollWheelZoom className="h-full w-full">
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      <Recenter lat={centerLat} lon={centerLon} zoom={zoom} />
      {points.length > 1 && <Polyline positions={points} pathOptions={{ color: "#1f63c4", weight: 3 }} />}
      {liveFix && livePoint && (
        <Marker position={livePoint}>
          <Popup>
            <div className="text-xs">
              <div className="font-semibold">Last fix (GPS)</div>
              <div>{formatDateTime(latest?.deviceTime ?? latest?.receivedAt)}</div>
              {latest?.accuracy != null && <div>± {Math.round(latest.accuracy)} m</div>}
            </div>
          </Popup>
        </Marker>
      )}
      {liveApprox && livePoint && (
        <Circle
          center={livePoint}
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
      {stalePoint && (
        <Marker position={stalePoint} opacity={0.6}>
          <Popup>
            <div className="text-xs">
              <div className="font-semibold">Last known position</div>
              <div>The latest report had no GPS fix.</div>
              <div>{formatDateTime(latest?.receivedAt)}</div>
            </div>
          </Popup>
        </Marker>
      )}
    </MapContainer>
  );
}
