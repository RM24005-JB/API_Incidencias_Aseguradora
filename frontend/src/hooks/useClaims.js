import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import api from '../services/api';
import toast from 'react-hot-toast';

export const useClaims = (page = 0, size = 5, filters = {}) => {
  const queryClient = useQueryClient();

  // FIX: Limpiar filtros vacíos para evitar 400 Bad Request en Spring
  const cleanFilters = Object.fromEntries(
    Object.entries(filters).filter(([, v]) => v !== '' && v != null && v !== undefined)
  );

  const { data, isLoading, error } = useQuery({
    queryKey: ['claims', page, size, cleanFilters],
    queryFn: async () => {
      const token = localStorage.getItem('accessToken');
      if (!token) {
        return { content: [], totalPages: 0, totalElements: 0 };
      }
      
      // Verificar si es admin para usar endpoint correcto
      const userRole = localStorage.getItem('userRole');
      const isAdmin = userRole === 'ADMIN';
      
      const endpoint = isAdmin ? '/admin/reclamos' : '/reclamos';
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

  const createClaim = useMutation({
    mutationFn: async (claimData) => {
      const { files, ...jsonData } = claimData;
      const response = await api.post('/reclamos', jsonData);
      const newClaim = response.data;

      if (files && files.length > 0) {
        for (const file of files) {
          const formData = new FormData();
          formData.append('file', file);
          await api.post(`/upload/reclamo/${newClaim.id}`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
          });
        }
      }

      return newClaim;
    },
    onSuccess: () => {
      toast.success('Reclamo creado exitosamente');
      queryClient.invalidateQueries({ queryKey: ['claims'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard-stats'] });
    },
    onError: (err) => {
      const message = err.response?.data?.message || 'Error al crear reclamo';
      toast.error(message);
    },
  });

  return { 
    claims: data?.content || [], 
    totalPages: data?.totalPages || 0, 
    totalElements: data?.totalElements || 0,
    isLoading, 
    error,
    createClaim 
  };
};
