import { apiClient } from "@/infrastructure/http/apiClient";
import type { AuthSession } from "@/domain/user";

export interface RegisterPayload {
  email: string;
  password: string;
  displayName?: string;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export const authApi = {
  async register(payload: RegisterPayload): Promise<void> {
    await apiClient.post("/auth/register", payload);
  },

  async login(payload: LoginPayload): Promise<AuthSession> {
    const { data } = await apiClient.post<AuthSession>("/auth/login", payload);
    return data;
  },
};
