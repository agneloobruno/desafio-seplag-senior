import axios from 'axios';

// URL base da API - pode ser configurada via variável de ambiente
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/';

// Instância configurada do Axios
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 segundos
});

// Interceptor de Request - Injeta o token JWT automaticamente
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor de Response - Tratamento de erros globais
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // Tratamento específico para erro 401 (Não autorizado)
    if (error.response?.status === 401) {
      // Remove o token inválido
      localStorage.removeItem('authToken');
      
      // Redireciona para login (será implementado com React Router)
      window.location.href = '/login';
    }
    
    // Tratamento específico para erro 403 (Proibido)
    if (error.response?.status === 403) {
      console.error('Acesso negado:', error.response.data);
    }
    
    // Tratamento específico para erro 500 (Erro do servidor)
    if (error.response?.status === 500) {
      console.error('Erro interno do servidor');
    }
    
    return Promise.reject(error);
  }
);

export default api;
