import { api } from './api';
import type { LoginResponse } from '../types/auth';

export const authService = {
  login: async (login: string, password: string): Promise<LoginResponse> => {
    // O endpoint deve bater com o que está no seu AuthController.java
    const { data } = await api.post<LoginResponse>('/v1/auth/login', { login, senha: password });
    return data;
  },
  
  logout: () => {
    localStorage.removeItem('token');
    window.location.href = '/login';
  },
};
