import { createContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import { authService } from '../services/authService';

interface AuthContextType {
  isAuthenticated: boolean;
  login: (login: string, pass: string) => Promise<void>;
  logout: () => void;
  isLoading: boolean;
}

export const AuthContext = createContext({} as AuthContextType);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Verifica se há token ou refresh token válido
    const validateAuth = async () => {
      try {
        const token = localStorage.getItem('token');
        const refreshToken = localStorage.getItem('refreshToken');

        if (!token && !refreshToken) {
          // Sem tokens, redireciona para login silenciosamente
          setIsAuthenticated(false);
          setIsLoading(false);
          return;
        }

        if (token) {
          // Token disponível
          setIsAuthenticated(true);
          setIsLoading(false);
          return;
        }

        if (refreshToken) {
          // Tenta renovar o token usando o refresh token
          try {
            const data = await authService.refreshToken();
            localStorage.setItem('token', data.token);
            if (data.refreshToken) {
              localStorage.setItem('refreshToken', data.refreshToken);
            }
            setIsAuthenticated(true);
            setIsLoading(false);
            return;
          } catch (error) {
            // Refresh token inválido, limpa tudo e vai para login
            localStorage.removeItem('token');
            localStorage.removeItem('refreshToken');
            setIsAuthenticated(false);
            setIsLoading(false);
          }
        }
      } catch (error) {
        console.error('Erro ao validar autenticação:', error);
        setIsAuthenticated(false);
        setIsLoading(false);
      }
    };

    validateAuth();
  }, []);

  async function login(u: string, p: string) {
    try {
      const data = await authService.login(u, p);
      localStorage.setItem('token', data.token);
      if (data.refreshToken) {
        localStorage.setItem('refreshToken', data.refreshToken);
      }
      setIsAuthenticated(true);
    } catch (error) {
      console.error(error);
      throw new Error('Falha no login');
    }
  }

  function logout() {
    authService.logout();
    setIsAuthenticated(false);
  }

  return (
    <AuthContext.Provider value={{ isAuthenticated, login, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
}
