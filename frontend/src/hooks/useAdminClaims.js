import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import api from '../services/api';
import toast from 'react-hot-toast';

export const useAdminClaims = (page = 0, size = 10, filters = {}) => {
  const queryClient = useQueryClient();

  // FIX: Limpiar filtros vacíos para evitar 400 Bad Request
  const cleanFilters = Object.fromEntries(
    Object.entries(filters).filter(([, v]) => v !== '' && v != null && v !== undefined)
  );

  const { data, isLoading, error } = useQuery({
    queryKey: ['admin-claims', page, size, cleanFilters],
    queryFn: async () => {
      const token = localStorage.getItem('accessToken');
      if (!token) {
        return { content: [], totalPages: 0, totalElements: 0 };
      }
      const params = new URLSearchParams({ page, size, ...cleanFilters });
      const res = await api.get(`/admin/reclamos?${params}`);
      return res.data;
    },
    enabled: !!localStorage.getItem('accessToken'),
    retry: (failureCount, error) => {
      if (error.response?.status === 401 || error.response?.status === 403) {
        return false;
      }
      return failureCount < 2;
    },
  });

  const updateClaimStatus = useMutation({
    mutationFn: ({ id, estado }) => api.put(`/admin/reclamos/${id}/estado`, { nuevoEstado: estado }),
    onSuccess: () => {
      toast.success('Estado actualizado');
      queryClient.invalidateQueries({ queryKey: ['admin-claims'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard-stats'] });
      queryClient.invalidateQueries({ queryKey: ['claims'] });
    },
    onError: (err) => {
      const message = err.response?.data?.message || 'Error al actualizar estado';
      toast.error(message);
    },
  });

  return { 
    claims: data?.content || [], 
    totalPages: data?.totalPages || 0,
    totalElements: data?.totalElements || 0,
    isLoading, 
    error,
    updateClaimStatus 
  };
};
