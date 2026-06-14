import { Navigate, Route, Routes } from "react-router-dom";
import { ProtectedRoute } from "@/presentation/components/ProtectedRoute";
import { Layout } from "@/presentation/components/Layout";
import { LoginPage } from "@/presentation/pages/LoginPage";
import { RegisterPage } from "@/presentation/pages/RegisterPage";
import { DashboardPage } from "@/presentation/pages/DashboardPage";
import { DeviceDetailPage } from "@/presentation/pages/DeviceDetailPage";

export function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route element={<ProtectedRoute />}>
        <Route element={<Layout />}>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/devices/:deviceId" element={<DeviceDetailPage />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
