import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import api from '../services/api';
import toast from 'react-hot-toast';

export const usePolicies = (page = 0, size = 6, filters = {}) => {
  const queryClient = useQueryClient();

  // FIX: Limpiar filtros vacíos para evitar 400 Bad Request
  const cleanFilters = Object.fromEntries(
    Object.entries(filters).filter(([, v]) => v !== '' && v != null && v !== undefined)
  );

  const { data, isLoading, error } = useQuery({
    queryKey: ['policies', page, size, cleanFilters],
    queryFn: async () => {
      const token = localStorage.getItem('accessToken');
      if (!token) {
        return { content: [], totalPages: 0, totalElements: 0 };
      }
      
      // Todos los usuarios (incluyendo clientes) ahora ven todas las pólizas del sistema
      const endpoint = '/polizas';
      const params = new URLSearchParams({ page, size, ...cleanFilters });
      const res = await api.get(`${endpoint}?${params}`);
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

  const createPolicy = useMutation({
    mutationFn: (newPolicy) => api.post('/polizas', newPolicy),
    onSuccess: () => {
      toast.success('Póliza agregada');
      queryClient.invalidateQueries({ queryKey: ['policies'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard-stats'] });
    },
    onError: (err) => {
      const message = err.response?.data?.message || 'Error al crear póliza';
      toast.error(message);
    },
  });

  const deletePolicy = useMutation({
    mutationFn: (id) => api.delete(`/polizas/${id}`),
    onSuccess: () => {
      toast.success('Póliza eliminada');
      queryClient.invalidateQueries({ queryKey: ['policies'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard-stats'] });
    },
    onError: (err) => {
      const message = err.response?.data?.message || 'Error al eliminar póliza';
      toast.error(message);
    },
  });

  return { 
    policies: data?.content || [], 
    totalPages: data?.totalPages || 0,
    totalElements: data?.totalElements || 0,
    isLoading, 
    error,
    createPolicy, 
    deletePolicy 
  };
};
