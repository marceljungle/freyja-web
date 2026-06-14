import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import path from "node:path";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
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
