import React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import api from '../services/api';  // ← CORREGIDO: era ../../services/api
import toast from 'react-hot-toast';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { useTranslation } from 'react-i18next';
import { motion } from 'framer-motion';

const AdminUsers = () => {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const { data: users, isLoading } = useQuery({
    queryKey: ['admin-users'],
    queryFn: () => api.get('/admin/usuarios').then(res => res.data),
  });

  const updateRole = useMutation({
    mutationFn: ({ id, role }) => api.put(`/admin/usuarios/${id}/rol`, { nuevoRol: role }),
    onSuccess: () => { 
      toast.success(t('success')); 
      queryClient.invalidateQueries(['admin-users']); 
    },
    onError: (err) => toast.error(err.response?.data?.message || t('error'))
  });

  const toggleEnable = useMutation({
    mutationFn: (id) => api.put(`/admin/usuarios/${id}/toggle`),
    onSuccess: () => { 
      toast.success(t('success')); 
      queryClient.invalidateQueries(['admin-users']); 
    },
    onError: (err) => toast.error(err.response?.data?.message || t('error'))
  });

  const deleteUser = useMutation({
    mutationFn: (id) => api.delete(`/admin/usuarios/${id}`),
    onSuccess: () => { 
      toast.success(t('success')); 
      queryClient.invalidateQueries(['admin-users']); 
    },
    onError: (err) => toast.error(err.response?.data?.message || t('error'))
  });

  if (isLoading) return <LoadingSpinner />;

  return (
    <div>
      <h1 className="text-2xl font-bold mb-4">{t('adminUsers')}</h1>
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white dark:bg-gray-800 rounded-xl shadow">
          <thead className="bg-gray-100 dark:bg-gray-700">
            <tr>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">{t('id')}</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">{t('email')}</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">{t('name')}</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">{t('role')}</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">{t('status')}</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">{t('actions')}</th>
            </tr>
          </thead>
          <tbody>
            {users?.map((u, index) => (
              <motion.tr 
                key={u.id}
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.03 }}
                className="border-b hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
              >
                <td className="p-2">{u.id}</td>
                <td className="p-2">{u.email}</td>
                <td className="p-2">{u.nombre}</td>
                <td className="p-2">
                  <select 
                    value={u.role} 
                    onChange={e => updateRole.mutate({ id: u.id, role: e.target.value })}
                    className="border rounded px-2 py-1 dark:bg-gray-700"
                  >
                    <option value="USER">USER</option>
                    <option value="ADMIN">ADMIN</option>
                  </select>
                </td>
                <td className="p-2">
                  <span className={`px-2 py-1 rounded-full text-xs ${u.enabled ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                    {u.enabled ? t('active') : t('inactive')}
                  </span>
                </td>
                <td className="p-2">
                  <div className="flex gap-2">
                    <button 
                      onClick={() => toggleEnable.mutate(u.id)} 
                      className="bg-yellow-600 text-white px-2 py-1 rounded hover:bg-yellow-700 transition"
                    >
                      {u.enabled ? t('deactivate') : t('activate')}
                    </button>
                    <button 
                      onClick={() => deleteUser.mutate(u.id)} 
                      className="bg-red-600 text-white px-2 py-1 rounded hover:bg-red-700 transition"
                    >
                      {t('delete')}
                    </button>
                  </div>
                </td>
              </motion.tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AdminUsers;