import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import basicSsl from "@vitejs/plugin-basic-ssl";
import path from "node:path";

// Enable HTTPS for the dev server (self-signed cert) so the Web Serial API is
// available when the SPA is reached over a LAN IP instead of localhost.
//   VITE_HTTPS=true npm run dev      (or: npm run dev:https)
const useHttps = process.env.VITE_HTTPS === "true";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react(), ...(useHttps ? [basicSsl()] : [])],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "src"),
    },
  },
  server: {
    // Listen on all interfaces so the dev SPA is reachable from other hosts
    // (e.g. http://192.168.x.x:5173). Pair with the backend's `dev` profile.
    host: true,
    port: 5173,
    proxy: {
      // Proxy API calls to the Spring backend during development.
      "/api": {
        target: process.env.VITE_PROXY_TARGET ?? "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
