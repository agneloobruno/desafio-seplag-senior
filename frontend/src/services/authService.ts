import api from './api';
import { LoginResponse } from '../types/auth';

export const authService = {
  login: async (login: string, password: string): Promise<LoginResponse> => {
    // O endpoint deve bater com o que está no seu AuthController.java
    const { data } = await api.post<LoginResponse>('/auth/login', { login, senha: password });
    return data;
  },
  
  logout: () => {
    localStorage.removeItem('authToken');
    window.location.href = '/login';
  },
};
