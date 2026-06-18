import api from './api';

export const login = async (credentials) => {
  try {
    const response = await api.post('/auth/login', credentials);
    const { accessToken, refreshToken } = response.data;

    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);

    try {
      const payload = JSON.parse(atob(accessToken.split('.')[1]));
      localStorage.setItem('userRole', payload.role?.replace('ROLE_', ''));
    } catch (e) {
      console.warn('Error decodificando token:', e);
    }

    return response.data;
  } catch (error) {
    console.error('Error en login:', error);
    throw error;
  }
};

export const register = async (userData) => {
  try {
    const response = await api.post('/auth/register', userData);
    const { accessToken, refreshToken } = response.data;

    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);

    // FIX: Guardar userRole también en registro (antes solo login lo hacía)
    try {
      const payload = JSON.parse(atob(accessToken.split('.')[1]));
      localStorage.setItem('userRole', payload.role?.replace('ROLE_', ''));
    } catch (e) {
      console.warn('Error decodificando token:', e);
    }

    return response.data;
  } catch (error) {
    console.error('Error en registro:', error);
    throw error;
  }
};

export const logout = async () => {
  try {
    const refreshToken = localStorage.getItem('refreshToken');
    if (refreshToken) {
      await api.post('/auth/logout', { refreshToken });
    }
  } catch (e) {
    console.warn('Error en logout del servidor:', e);
  } finally {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userRole');
  }
};
