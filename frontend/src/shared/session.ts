import type { AuthSession } from "@/domain/user";

const STORAGE_KEY = "freyja.session";

/** Notifies listeners (e.g. the auth provider) when the session is cleared. */
type Listener = () => void;
const listeners = new Set<Listener>();

export function getSession(): AuthSession | null {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as AuthSession;
  } catch {
    return null;
  }
}

export function getToken(): string | null {
  return getSession()?.token ?? null;
}

export function setSession(session: AuthSession): void {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
}

export function clearSession(): void {
  localStorage.removeItem(STORAGE_KEY);
  listeners.forEach((l) => l());
}

export function onSessionCleared(listener: Listener): () => void {
  listeners.add(listener);
  return () => listeners.delete(listener);
}
