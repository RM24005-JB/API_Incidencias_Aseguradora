import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { useTranslation } from 'react-i18next';
import toast from 'react-hot-toast';
import { motion } from 'framer-motion';

const loginSchema = z.object({
  email: z.string().email('Email inválido'),
  password: z.string().min(6, 'Mínimo 6 caracteres'),
});

const Login = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { login, isLoginPending } = useAuth();
  const { register, handleSubmit, formState: { errors } } = useForm({ resolver: zodResolver(loginSchema) });

  const onSubmit = async (data) => {
    try {
      await login(data);
      navigate('/dashboard');
    } catch (err) {
      // No mostrar toast.error aquí porque el interceptor de Axios ya muestra el mensaje específico
      // Solo loguear el error para debugging
      console.error('Login error:', err);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 dark:bg-gray-900 p-4">
      <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.5 }}
        className="bg-white dark:bg-gray-800 p-8 rounded-xl shadow-md w-full max-w-md">
        <h2 className="text-2xl font-bold mb-6 text-center">{t('login')}</h2>
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="mb-4">
            <label className="block mb-1 text-sm">{t('email')}</label>
            <input {...register('email')} className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700 focus:ring-2 focus:ring-primary-500 outline-none transition" />
            {errors.email && <p className="text-red-500 text-sm mt-1">{errors.email.message}</p>}
          </div>
          <div className="mb-6">
            <label className="block mb-1 text-sm">{t('password')}</label>
            <input type="password" {...register('password')} className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700 focus:ring-2 focus:ring-primary-500 outline-none transition" />
            {errors.password && <p className="text-red-500 text-sm mt-1">{errors.password.message}</p>}
          </div>
          <button type="submit" disabled={isLoginPending} className="w-full bg-primary-600 text-white py-2 rounded-lg hover:bg-primary-700 transition disabled:opacity-50">
            {isLoginPending ? t('loading') : t('login')}
          </button>
        </form>
        <p className="mt-4 text-center text-sm">{t('noAccount')} <Link to="/register" className="text-primary-600 hover:underline">{t('register')}</Link></p>
      </motion.div>
    </div>
  );
};

export default Login;