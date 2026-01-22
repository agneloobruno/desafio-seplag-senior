import axios from 'axios';

export const api = axios.create({
  baseURL: 'http://localhost:8080', // Endereço da sua API no Docker
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
