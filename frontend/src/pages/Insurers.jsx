import React from 'react';
import { useInsurers } from '../hooks/useInsurers';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { useTranslation } from 'react-i18next';
import { motion } from 'framer-motion';

const Insurers = () => {
  const { t } = useTranslation();
  const { insurers, isLoading } = useInsurers();
  if (isLoading) return <LoadingSpinner />;
  return (
    <div>
      <h1 className="text-2xl font-bold mb-4">{t('insurers')}</h1>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {insurers.map((i, index) => (
          <motion.div key={i.id} initial={{ opacity: 0, scale: 0.9 }} animate={{ opacity: 1, scale: 1 }}
            transition={{ delay: index * 0.05 }} whileHover={{ scale: 1.02 }}
            className="bg-white dark:bg-gray-800 p-4 rounded-xl shadow flex items-center space-x-4 hover:shadow-lg transition-shadow">
            {i.logoUrl && <img src={i.logoUrl} alt={i.nombre} className="w-12 h-12 object-contain rounded" />}
            <div><h3 className="font-bold">{i.nombre}</h3><p className="text-sm text-gray-500">{i.contactoEmail}</p></div>
          </motion.div>
        ))}
      </div>
    </div>
  );
};

export default Insurers;