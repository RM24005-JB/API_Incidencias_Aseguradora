import React, { useState } from 'react';
import { usePolicies } from '../hooks/usePolicies';
import { useInsurers } from '../hooks/useInsurers';
import { useAuth } from '../hooks/useAuth';
import { useForm } from 'react-hook-form';
import LoadingSpinner from '../components/common/LoadingSpinner';
import Pagination from '../components/common/Pagination';
import Modal from '../components/common/Modal';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import toast from 'react-hot-toast';
import { motion, AnimatePresence } from 'framer-motion';

const Policies = () => {
  const { t } = useTranslation();
  const { user } = useAuth();
  const [page, setPage] = useState(0);
  const [filters, setFilters] = useState({ aseguradoraId: '' });
  const { policies, totalPages, isLoading, createPolicy, deletePolicy } = usePolicies(page, 6, filters);
  const { insurers } = useInsurers();
  const [showForm, setShowForm] = useState(false);
  const [deleteModal, setDeleteModal] = useState({ isOpen: false, policyId: null });
  const { register, handleSubmit, reset, formState: { errors } } = useForm();
  const isAdmin = user?.role === 'ADMIN';

  const onSubmit = async (data) => {
    try {
      await createPolicy.mutateAsync({
        ...data, aseguradoraId: parseInt(data.aseguradoraId),
        fechaInicio: data.fechaInicio, fechaFin: data.fechaFin,
      });
      setShowForm(false); reset();
      toast.success(t('success'));
    } catch (error) {
      toast.error(error.response?.data?.message || t('error'));
    }
  };

  const handleDelete = (id) => setDeleteModal({ isOpen: true, policyId: id });
  const confirmDelete = async () => {
    await deletePolicy.mutateAsync(deleteModal.policyId);
    setDeleteModal({ isOpen: false, policyId: null });
  };

  if (isLoading) return <LoadingSpinner />;

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold">{t('policies')}</h1>
        {isAdmin && (
          <button onClick={() => setShowForm(true)} 
            className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition">+ {t('newPolicy')}</button>
        )}
      </div>

      <div className="bg-white dark:bg-gray-800 p-4 rounded-xl shadow-md mb-6 flex flex-wrap gap-4 items-end">
        <div className="flex-1 min-w-[150px]">
          <label className="block text-sm font-medium mb-1">{t('insurer')}</label>
          <select value={filters.aseguradoraId} 
            onChange={(e) => setFilters({ ...filters, aseguradoraId: e.target.value })} 
            className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700">
            <option value="">{t('all')}</option>
            {insurers.map(i => <option key={i.id} value={i.id}>{i.nombre}</option>)}
          </select>
        </div>
        <button onClick={() => setFilters({ aseguradoraId: '' })} 
          className="bg-gray-500 text-white px-4 py-2 rounded-lg hover:bg-gray-600 transition">{t('clearFilters')}</button>
      </div>

      <AnimatePresence>
        {showForm && (
          <motion.div initial={{ opacity: 0, height: 0 }} animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }} className="bg-white dark:bg-gray-800 p-6 rounded-xl shadow-md mb-6 overflow-hidden">
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-1">{t('insurer')}</label>
                <select {...register('aseguradoraId', { required: true })} className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700">
                  <option value="">{t('select')}</option>
                  {insurers.map(i => <option key={i.id} value={i.id}>{i.nombre}</option>)}
                </select>
                {errors.aseguradoraId && <p className="text-red-500 text-sm">{t('required')}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">{t('policyNumber')}</label>
                <input {...register('numeroPoliza', { required: true })} className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700" />
                {errors.numeroPoliza && <p className="text-red-500 text-sm">{t('required')}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">{t('policyType')}</label>
                <select {...register('tipo', { required: true })} className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700">
                  <option value="AUTO">Auto</option><option value="SALUD">Salud</option>
                  <option value="VIDA">Vida</option><option value="DANIOS">Daños</option>
                </select>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div><label className="block text-sm font-medium mb-1">{t('startDate')}</label>
                  <input type="date" {...register('fechaInicio', { required: true })} className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700" /></div>
                <div><label className="block text-sm font-medium mb-1">{t('endDate')}</label>
                  <input type="date" {...register('fechaFin', { required: true })} className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700" /></div>
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">{t('coverage')}</label>
                <textarea {...register('coberturas')} rows="3" className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700" />
              </div>
              <div className="flex gap-3">
                <button type="submit" className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition">{t('save')}</button>
                <button type="button" onClick={() => setShowForm(false)} className="bg-gray-500 text-white px-4 py-2 rounded-lg hover:bg-gray-600 transition">{t('cancel')}</button>
              </div>
            </form>
          </motion.div>
        )}
      </AnimatePresence>

      {policies.length === 0 ? (
        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}
          className="bg-white dark:bg-gray-800 p-8 rounded-xl shadow text-center">
          <p className="text-gray-500 mb-4">{t('noPolicies')}</p>
          {isAdmin && (
            <button onClick={() => setShowForm(true)} className="bg-primary-600 text-white px-4 py-2 rounded-lg">{t('createFirst')}</button>
          )}
        </motion.div>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {policies.map((p, index) => (
            <motion.div key={p.id} initial={{ opacity: 0, scale: 0.95 }} animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: index * 0.05 }} className="bg-white dark:bg-gray-800 p-4 rounded-xl shadow hover:shadow-lg transition-all duration-300">
              <Link to={`/polizas/${p.id}`} className="block">
                <div className="font-bold text-lg">{p.nombreAseguradora}</div>
                <div className="text-sm text-gray-600 dark:text-gray-400">{p.numeroPoliza} - {p.tipo}</div>
                <div className="text-xs mt-2 text-gray-500">{p.fechaInicio} → {p.fechaFin}</div>
              </Link>
              {isAdmin && (
                <button onClick={() => handleDelete(p.id)} className="mt-3 bg-red-600 text-white px-3 py-1 rounded text-sm hover:bg-red-700 transition">{t('delete')}</button>
              )}
            </motion.div>
          ))}
        </div>
      )}

      <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
      <Modal isOpen={deleteModal.isOpen} onClose={() => setDeleteModal({ isOpen: false, policyId: null })} 
        onConfirm={confirmDelete} title={t('confirmDelete')} message={t('confirmDelete')} />
    </div>
  );
};

export default Policies;