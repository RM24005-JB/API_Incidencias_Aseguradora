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

    // Manejo de errores de red (sin respuesta del servidor) - verificar primero
    if (!error.response) {
      let message = 'Error de conexión. Verifique su conexión a internet.';
      if (error.code === 'ECONNABORTED') {
        message = 'Tiempo de espera agotado. Verifique su conexión a internet.';
      } else if (error.code === 'ECONNREFUSED') {
        message = 'No se puede conectar al servidor. Verifique su conexión a internet.';
      } else if (error.message && error.message.includes('Network Error')) {
        message = 'Error de red. Verifique su conexión a internet.';
      }
      toast.error(message);
      return Promise.reject(error);
    }

    // Manejo de 401: intentar refresh token solo si no es login
    if (error.response?.status === 401 && !originalRequest._retry && !originalRequest.url?.includes('/auth/refresh')) {
      // Si es el endpoint de login, no intentar refresh token, mostrar mensaje específico
      if (originalRequest.url?.includes('/auth/login')) {
        const message = error.response?.data?.message || 'Credenciales inválidas';
        toast.error(message);
        return Promise.reject(error);
      }

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

    // Manejo de 403 Forbidden
    if (error.response?.status === 403) {
      const message = error.response?.data?.message || 'No tiene permisos para esta acción';
      toast.error(message);
      return Promise.reject(error);
    }

    // Manejo de 400 Bad Request (validación)
    if (error.response?.status === 400) {
      const message = error.response?.data?.message || 'Datos inválidos. Verifique la información ingresada.';
      toast.error(message);
      return Promise.reject(error);
    }

    // Manejo de 404 Not Found
    if (error.response?.status === 404) {
      const message = error.response?.data?.message || 'Recurso no encontrado';
      toast.error(message);
      return Promise.reject(error);
    }

    // Manejo de 500 Internal Server Error
    if (error.response?.status === 500) {
      toast.error('Error del servidor. Intente nuevamente más tarde.');
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
