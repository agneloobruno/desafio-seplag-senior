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

export default api;
