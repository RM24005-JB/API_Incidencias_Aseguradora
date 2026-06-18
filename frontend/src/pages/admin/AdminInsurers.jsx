import React, { useState } from 'react';
import { useInsurers } from '../../hooks/useInsurers';
import { useForm } from 'react-hook-form';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import Modal from '../../components/common/Modal';
import { useTranslation } from 'react-i18next';
import { motion, AnimatePresence } from 'framer-motion';
import toast from 'react-hot-toast';

const AdminInsurers = () => {
  const { t } = useTranslation();
  const { insurers, isLoading, createInsurer, updateInsurer, deleteInsurer } = useInsurers();
  const [editing, setEditing] = useState(null);
  const [deleteModal, setDeleteModal] = useState({ isOpen: false, id: null });
  const { register, handleSubmit, reset, setValue } = useForm();

  const onSubmit = async (data) => {
    try {
      if (editing) { await updateInsurer.mutateAsync({ id: editing.id, data }); setEditing(null); }
      else { await createInsurer.mutateAsync(data); }
      reset();
    } catch (err) { toast.error(err.response?.data?.message || t('error')); }
  };

  const editInsurer = (insurer) => {
    setEditing(insurer);
    setValue('nombre', insurer.nombre); setValue('nit', insurer.nit);
    setValue('contactoEmail', insurer.contactoEmail); setValue('logoUrl', insurer.logoUrl);
  };

  const confirmDelete = () => { deleteInsurer.mutate(deleteModal.id); setDeleteModal({ isOpen: false, id: null }); };

  if (isLoading) return <LoadingSpinner />;

  return (
    <div>
      <h1 className="text-2xl font-bold mb-4">{t('adminInsurers')}</h1>
      <form onSubmit={handleSubmit(onSubmit)} className="bg-white dark:bg-gray-800 p-4 rounded-xl shadow-md mb-6">
        <input {...register('nombre')} placeholder={t('name')} required className="w-full mb-2 border p-2 rounded dark:bg-gray-700" />
        <input {...register('nit')} placeholder="NIT" required className="w-full mb-2 border p-2 rounded dark:bg-gray-700" />
        <input {...register('contactoEmail')} placeholder={t('email')} type="email" required className="w-full mb-2 border p-2 rounded dark:bg-gray-700" />
        <input {...register('logoUrl')} placeholder="URL del logo" className="w-full mb-2 border p-2 rounded dark:bg-gray-700" />
        <div className="flex gap-2">
          <button type="submit" className="bg-primary-600 text-white px-4 py-2 rounded hover:bg-primary-700 transition">{editing ? t('edit') : t('save')}</button>
          {editing && <button type="button" onClick={() => { setEditing(null); reset(); }} className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600 transition">{t('cancel')}</button>}
        </div>
      </form>
      <div className="grid gap-4">
        <AnimatePresence>
          {insurers.map((i, index) => (
            <motion.div key={i.id} initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -10 }}
              transition={{ delay: index * 0.05 }} className="bg-white dark:bg-gray-800 p-4 rounded-xl shadow flex justify-between items-center">
              <div><strong>{i.nombre}</strong> - {i.contactoEmail}</div>
              <div>
                <button onClick={() => editInsurer(i)} className="bg-yellow-600 text-white px-3 py-1 rounded mr-2 hover:bg-yellow-700 transition">{t('edit')}</button>
                <button onClick={() => setDeleteModal({ isOpen: true, id: i.id })} className="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700 transition">{t('delete')}</button>
              </div>
            </motion.div>
          ))}
        </AnimatePresence>
      </div>
      <Modal isOpen={deleteModal.isOpen} onClose={() => setDeleteModal({ isOpen: false, id: null })} 
        onConfirm={confirmDelete} title={t('confirmDelete')} message={t('confirmDelete')} />
    </div>
  );
};

export default AdminInsurers;