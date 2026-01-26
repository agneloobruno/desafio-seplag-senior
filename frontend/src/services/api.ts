import axios from 'axios';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para adicionar o Token em todas as requisições
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor de resposta para tratar 401/403: tenta refresh uma vez, senão desloga com mensagem
api.interceptors.response.use(
  (resp) => resp,
  async (error) => {
    const originalRequest = error.config;
    if (!originalRequest || originalRequest._retry) {
      if (error.response && (error.response.status === 401 || error.response.status === 403)) {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        localStorage.setItem('sessionExpiredMessage', 'Ficou muito tempo inativo e precisa logar de novo');
        window.location.href = '/login';
      }
      return Promise.reject(error);
    }

    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      const refreshToken = localStorage.getItem('refreshToken');
      if (refreshToken) {
        try {
          originalRequest._retry = true;
          const baseURL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';
          const resp = await axios.post(`${baseURL}/v1/auth/refresh`, { refreshToken });
          const newToken = resp.data.token;
          localStorage.setItem('token', newToken);
          originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
          return api(originalRequest);
        } catch (e) {
          localStorage.removeItem('token');
          localStorage.removeItem('refreshToken');
          localStorage.setItem('sessionExpiredMessage', 'Ficou muito tempo inativo e precisa logar de novo');
          window.location.href = '/login';
          return Promise.reject(e);
        }
      } else {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        localStorage.setItem('sessionExpiredMessage', 'Ficou muito tempo inativo e precisa logar de novo');
        window.location.href = '/login';
// Interceptor para tratar respostas com erro 401 (token expirado)
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Se o erro for 401 e não for a primeira tentativa
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) {
          // Sem refresh token, vai para login
          window.location.href = '/login';
          return Promise.reject(error);
        }

        // Tenta renovar o token
        const response = await axios.post(
          `${import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'}/v1/auth/refresh`,
          { refreshToken },
          {
            headers: {
              'Content-Type': 'application/json',
            },
          }
        );

        const { token: newToken, refreshToken: newRefreshToken } = response.data;
        
        // Salva os novos tokens
        localStorage.setItem('token', newToken);
        if (newRefreshToken) {
          localStorage.setItem('refreshToken', newRefreshToken);
        }

        // Atualiza o header e tenta novamente
        originalRequest.headers.Authorization = `Bearer ${newToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        // Refresh falhou, vai para login
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
