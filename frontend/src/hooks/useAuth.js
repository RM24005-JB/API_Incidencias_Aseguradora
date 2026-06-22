import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import api from '../services/api';
import { login as loginService, register as registerService, logout as logoutService } from '../services/auth';

export const useAuth = () => {
  const queryClient = useQueryClient();

  const { data: user, isLoading, error } = useQuery({
    queryKey: ['me'],
    queryFn: async () => {
      const token = localStorage.getItem('accessToken');
      if (!token) {
        return null;
      }
      try {
        const res = await api.get('/auth/me');
        return res.data;
      } catch (err) {
        if (err.response?.status === 401) {
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('userRole');
        }
        return null;
      }
    },
    retry: false,
    refetchOnWindowFocus: false,
    staleTime: 0,
    gcTime: 0,
  });

  const loginMutation = useMutation({
    mutationFn: loginService,
    onSuccess: (data) => {
      // FIX: auth.js ya guarda los tokens, solo invalidar queries
      queryClient.invalidateQueries({ queryKey: ['me'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard-stats'] });
    },
  });

  const registerMutation = useMutation({
    mutationFn: registerService,
    onSuccess: (data) => {
      // FIX: auth.js ya guarda los tokens, solo invalidar queries
      queryClient.invalidateQueries({ queryKey: ['me'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard-stats'] });
    },
  });

  const logoutMutation = useMutation({
    mutationFn: logoutService,
    onSuccess: () => {
      queryClient.clear();
      queryClient.removeQueries({ queryKey: ['me'] });
      queryClient.removeQueries({ queryKey: ['dashboard-stats'] });
    },
    onError: () => {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('userRole');
      queryClient.clear();
    }
  });

  const isAuthenticated = !!user && !!localStorage.getItem('accessToken');

  return {
    user,
    isLoading,
    isAuthenticated,
    isAdmin: user?.role === 'ADMIN',
    login: loginMutation.mutateAsync,
    register: registerMutation.mutateAsync,
    logout: logoutMutation.mutateAsync,
    isLoginPending: loginMutation.isPending,
    isRegisterPending: registerMutation.isPending,
  };
};
