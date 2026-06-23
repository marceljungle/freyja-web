import L from "leaflet";
import iconUrl from "leaflet/dist/images/marker-icon.png";
import iconRetinaUrl from "leaflet/dist/images/marker-icon-2x.png";
import shadowUrl from "leaflet/dist/images/marker-shadow.png";

// Leaflet's default icon prepends an auto-detected `imagePath` to the icon URLs.
// Under Vite the imported URLs are already absolute, so that prepend produces a
// doubled, broken path. Deleting the overridden _getIconUrl makes Leaflet use
// the bundler-resolved URLs verbatim.
delete (L.Icon.Default.prototype as unknown as { _getIconUrl?: unknown })._getIconUrl;

L.Icon.Default.mergeOptions({
  iconUrl,
  iconRetinaUrl,
  shadowUrl,
});

export {};
