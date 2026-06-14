export interface User {
  id: string;
  email: string;
  displayName: string | null;
  role: string;
}

export interface AuthSession {
  token: string;
  expiresAt: string;
  user: User;
}
