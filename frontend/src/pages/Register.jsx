import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { useTranslation } from 'react-i18next';
import toast from 'react-hot-toast';
import { motion } from 'framer-motion';

const registerSchema = z.object({
  email: z.string().email('Email inválido'),
  password: z.string().min(6, 'Mínimo 6 caracteres'),
  nombre: z.string().min(2, 'Nombre requerido'),
  telefono: z.string().optional(),
});

const Register = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { register: registerUser, isRegisterPending } = useAuth();
  const { register, handleSubmit, formState: { errors } } = useForm({ resolver: zodResolver(registerSchema) });

  const onSubmit = async (data) => {
    try {
      await registerUser(data);
      navigate('/dashboard');
    } catch (err) {
      let errorMessage = t('error');
      if (err.response?.status === 409) {
        errorMessage = t('emailAlreadyExists');
      } else if (err.response?.status === 400) {
        errorMessage = err.response?.data?.message || t('error');
      } else if (err.response?.data?.message) {
        errorMessage = err.response.data.message;
      } else if (!err.response) {
        errorMessage = t('networkError');
      }
      toast.error(errorMessage);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 dark:bg-gray-900 p-4">
      <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.5 }}
        className="bg-white dark:bg-gray-800 p-8 rounded-xl shadow-md w-full max-w-md">
        <h2 className="text-2xl font-bold mb-6 text-center">{t('register')}</h2>
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="mb-4">
            <label className="block mb-1 text-sm">{t('email')}</label>
            <input {...register('email')} className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700 focus:ring-2 focus:ring-primary-500 outline-none transition" />
            {errors.email && <p className="text-red-500 text-sm mt-1">{errors.email.message}</p>}
          </div>
          <div className="mb-4">
            <label className="block mb-1 text-sm">{t('name')}</label>
            <input {...register('nombre')} className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700 focus:ring-2 focus:ring-primary-500 outline-none transition" />
            {errors.nombre && <p className="text-red-500 text-sm mt-1">{errors.nombre.message}</p>}
          </div>
          <div className="mb-4">
            <label className="block mb-1 text-sm">{t('phone')} ({t('optional')})</label>
            <input {...register('telefono')} className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700" />
          </div>
          <div className="mb-6">
            <label className="block mb-1 text-sm">{t('password')}</label>
            <input type="password" {...register('password')} className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700 focus:ring-2 focus:ring-primary-500 outline-none transition" />
            {errors.password && <p className="text-red-500 text-sm mt-1">{errors.password.message}</p>}
          </div>
          <button type="submit" disabled={isRegisterPending} className="w-full bg-primary-600 text-white py-2 rounded-lg hover:bg-primary-700 transition disabled:opacity-50">
            {isRegisterPending ? t('loading') : t('register')}
          </button>
        </form>
        <p className="mt-4 text-center text-sm">{t('haveAccount')} <Link to="/login" className="text-primary-600 hover:underline">{t('login')}</Link></p>
      </motion.div>
    </div>
  );
};

export default Register;