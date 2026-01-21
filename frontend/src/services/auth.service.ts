import api from './api';
import type { LoginRequest, LoginResponse, RegisterRequest, User } from '../types/auth';

/**
 * Serviço de Autenticação
 * Gerencia login, registro e decodificação de tokens JWT
 */
class AuthService {
  /**
   * Realiza o login do usuário
   * @param credentials - Login e senha do usuário
   * @returns Token JWT e informações do usuário
   */
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response = await api.post<LoginResponse>('/auth/login', credentials);
    
    if (response.data.token) {
      // Armazena o token no localStorage
      this.setToken(response.data.token);
    }
    
    return response.data;
  }

  /**
   * Registra um novo usuário
   * @param data - Dados do novo usuário
   * @returns Token JWT e informações do usuário
   */
  async register(data: RegisterRequest): Promise<LoginResponse> {
    const response = await api.post<LoginResponse>('/auth/register', data);
    
    if (response.data.token) {
      this.setToken(response.data.token);
    }
    
    return response.data;
  }

  /**
   * Remove o token e faz logout
   */
  logout(): void {
    localStorage.removeItem('authToken');
  }

  /**
   * Armazena o token no localStorage
   */
  setToken(token: string): void {
    localStorage.setItem('authToken', token);
  }

  /**
   * Recupera o token do localStorage
   */
  getToken(): string | null {
    return localStorage.getItem('authToken');
  }

  /**
   * Verifica se existe um token válido
   */
  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;

    // Verifica se o token não expirou
    try {
      const payload = this.decodeToken(token);
      const now = Date.now() / 1000;
      return payload.exp > now;
    } catch {
      return false;
    }
  }

  /**
   * Decodifica o payload do token JWT (sem validar assinatura)
   * ATENÇÃO: Isso é apenas para ler dados do token no frontend.
   * A validação real deve ser feita no backend.
   */
  decodeToken(token: string): any {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch (error) {
      console.error('Erro ao decodificar token:', error);
      return null;
    }
  }

  /**
   * Extrai as informações do usuário a partir do token JWT
   */
  getUserFromToken(): User | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      const payload = this.decodeToken(token);
      
      return {
        login: payload.sub || payload.login || '',
        role: payload.role || 'USER',
      };
    } catch (error) {
      console.error('Erro ao extrair usuário do token:', error);
      return null;
    }
  }
}

// Exporta uma instância única (Singleton)
export default new AuthService();
