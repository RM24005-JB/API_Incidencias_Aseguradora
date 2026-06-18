import { useQuery } from '@tanstack/react-query';
import api from '../services/api';

export const useDashboardStats = () => {
  return useQuery({
    queryKey: ['dashboard-stats'],
    queryFn: async () => {
      const token = localStorage.getItem('accessToken');
      if (!token) {
        // Retornar datos vacíos si no hay token
        return {
          totalPolicies: 0,
          openClaims: 0,
          approvedClaims: 0,
          totalAmount: 0,
          monthlyClaims: [],
          reclamosPorEstado: {},
          recentClaims: []
        };
      }
      const res = await api.get('/dashboard/stats');
      return res.data;
    },
    staleTime: 2 * 60 * 1000,
    // FIX: Solo ejecutar si hay token (evita 403 al cargar sin sesión)
    enabled: !!localStorage.getItem('accessToken'),
    retry: (failureCount, error) => {
      // No reintentar si es 401 o 403
      if (error.response?.status === 401 || error.response?.status === 403) {
        return false;
      }
      return failureCount < 3;
    },
  });
};
