import axios from 'axios';
import toast from 'react-hot-toast';

let refreshPromise = null;

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 10000, // 10 segundos timeout
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Manejo de 401: intentar refresh token
    if (error.response?.status === 401 && !originalRequest._retry && !originalRequest.url?.includes('/auth/refresh')) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) {
          throw new Error('No refresh token');
        }

        // Usar promise singleton para evitar múltiples refresh simultáneos
        if (!refreshPromise) {
          refreshPromise = api.post('/auth/refresh', { refreshToken }).then(res => res.data);
        }

        const { accessToken, refreshToken: newRefreshToken } = await refreshPromise;

        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', newRefreshToken);
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;

        refreshPromise = null;
        return api(originalRequest);

      } catch (refreshError) {
        refreshPromise = null;
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('userRole');

        // Solo redirigir si no estamos ya en login
        if (!window.location.pathname.includes('/login')) {
          toast.error('Sesión expirada. Inicie sesión nuevamente.');
          window.location.href = '/login';
        }

        return Promise.reject(refreshError);
      }
    }

    // FIX: Manejar 403 Forbidden
    if (error.response?.status === 403) {
      const message = error.response?.data?.message || 'No tiene permisos para esta acción';
      toast.error(message);
      return Promise.reject(error);
    }

    // FIX: Manejar errores de red (sin respuesta del servidor)
    if (!error.response) {
      toast.error('Error de conexión. Verifique su conexión a internet.');
      return Promise.reject(error);
    }

    // Error genérico con mensaje del servidor
    const errorMessage = error.response?.data?.message || error.message || 'Error de conexión';
    if (error.response?.status !== 401) { // No mostrar toast para 401 (ya manejado arriba)
      toast.error(errorMessage);
    }

    return Promise.reject(error);
  }
);

export default api;
