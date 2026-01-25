import axios from 'axios';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080', // use VITE_API_BASE_URL ou fallback
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para adicionar o Token em todas as requisições
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    // @ts-ignore
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
      }
    }

    return Promise.reject(error);
  }
);

export default api;
