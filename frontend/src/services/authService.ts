import { api } from './api';
import type { LoginResponse } from '../types/auth';

export const authService = {
  login: async (login: string, password: string): Promise<LoginResponse> => {
    // O endpoint deve bater com o que está no seu AuthController.java
    const { data } = await api.post<LoginResponse>('/v1/auth/login', { login, senha: password });
    return data;
  },

  refreshToken: async (): Promise<LoginResponse> => {
    try {
      const refreshToken = localStorage.getItem('refreshToken');
      if (!refreshToken) {
        throw new Error('Refresh token não encontrado');
      }
      const { data } = await api.post<LoginResponse>('/v1/auth/refresh', { refreshToken });
      return data;
    } catch (error) {
      console.error('Erro ao renovar token:', error);
      throw error;
    }
  },

  hasValidToken: (): boolean => {
    const token = localStorage.getItem('token');
    const refreshToken = localStorage.getItem('refreshToken');
    return !!(token || refreshToken);
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    window.location.href = '/login';
  },

  logoutWithMessage: (message: string) => {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    // sinaliza para a UI exibir uma mensagem de sessão expirada
    localStorage.setItem('sessionExpiredMessage', message);
    window.location.href = '/login';
  },

  refreshToken: async (refreshToken: string) => {
    const { data } = await api.post<{ token: string }>('/v1/auth/refresh', { refreshToken });
    return data.token;
  }
};
