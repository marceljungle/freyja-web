import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import type { ReactNode } from "react";
import type { AuthSession, User } from "@/domain/user";
import { authApi } from "@/infrastructure/api/authApi";
import { clearSession, getSession, onSessionCleared, setSession } from "@/shared/session";

interface AuthContextValue {
  user: User | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, password: string, displayName?: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSessionState] = useState<AuthSession | null>(() => getSession());

  // React to forced logouts triggered by the 401 interceptor.
  useEffect(() => onSessionCleared(() => setSessionState(null)), []);

  const login = useCallback(async (email: string, password: string) => {
    const result = await authApi.login({ email, password });
    setSession(result);
    setSessionState(result);
  }, []);

  const register = useCallback(
    async (email: string, password: string, displayName?: string) => {
      await authApi.register({ email, password, displayName });
      // Auto-login after a successful registration.
      const result = await authApi.login({ email, password });
      setSession(result);
      setSessionState(result);
    },
    [],
  );

  const logout = useCallback(() => {
    clearSession();
    setSessionState(null);
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      user: session?.user ?? null,
      isAuthenticated: Boolean(session?.token),
      login,
      register,
      logout,
    }),
    [session, login, register, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within an AuthProvider");
  return ctx;
}
