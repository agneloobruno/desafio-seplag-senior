export interface User {
  login: string;
  role: 'ADMIN' | 'USER';
}

export interface LoginResponse {
  token: string;
  refreshToken?: string;
  refreshToken: string;
  // O backend pode retornar informações adicionais
  // Ajuste conforme o JSON real de resposta do endpoint /auth/login
}

export interface LoginRequest {
  login: string;
  password: string;
}

export interface RegisterRequest {
  login: string;
  password: string;
  role?: 'ADMIN' | 'USER';
}

export interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  logout: () => void;
}
