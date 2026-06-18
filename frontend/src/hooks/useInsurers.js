import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import api from '../services/api';
import toast from 'react-hot-toast';
import { useTranslation } from 'react-i18next';

export const useInsurers = () => {
  const { t } = useTranslation();
  const queryClient = useQueryClient();

  const { data: insurers, isLoading, error } = useQuery({
    queryKey: ['insurers'],
    queryFn: () => api.get('/aseguradoras').then(res => res.data),
    staleTime: 5 * 60 * 1000,
    retry: 2,
  });

  const createInsurer = useMutation({
    mutationFn: (data) => api.post('/aseguradoras', data),
    onSuccess: () => {
      toast.success(t('success'));
      queryClient.invalidateQueries({ queryKey: ['insurers'] });
    },
    onError: (err) => {
      const message = err.response?.data?.message || t('error');
      toast.error(message);
    },
  });

  const updateInsurer = useMutation({
    mutationFn: ({ id, data }) => api.put(`/aseguradoras/${id}`, data),
    onSuccess: () => {
      toast.success(t('success'));
      queryClient.invalidateQueries({ queryKey: ['insurers'] });
    },
    onError: (err) => {
      const message = err.response?.data?.message || t('error');
      toast.error(message);
    },
  });

  const deleteInsurer = useMutation({
    mutationFn: (id) => api.delete(`/aseguradoras/${id}`),
    onSuccess: () => {
      toast.success(t('success'));
      queryClient.invalidateQueries({ queryKey: ['insurers'] });
    },
    onError: (err) => {
      const message = err.response?.data?.message || t('error');
      toast.error(message);
    },
  });

  return { 
    insurers: insurers || [], 
    isLoading, 
    error,
    createInsurer, 
    updateInsurer, 
    deleteInsurer 
  };
};
