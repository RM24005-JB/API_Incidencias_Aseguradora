import React from 'react';
import { useParams, Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import api from '../services/api';
import StatusBadge from '../components/common/StatusBadge';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { format } from 'date-fns';
import { useTranslation } from 'react-i18next';
import { motion } from 'framer-motion';

const ClaimDetail = () => {
  const { id } = useParams();
  const { t } = useTranslation();
  const { data: claim, isLoading } = useQuery({
    queryKey: ['claim', id],
    queryFn: () => api.get(`/reclamos/${id}`).then(res => res.data),
  });

  if (isLoading) return <LoadingSpinner />;
  if (!claim) return (
    <div className="text-center py-10">
      <p className="text-gray-500">{t('noData')}</p>
      <Link to="/reclamos" className="text-primary-600 hover:underline mt-2 inline-block">{t('back')}</Link>
    </div>
  );

  return (
    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="max-w-4xl mx-auto">
      <div className="flex items-center gap-4 mb-4">
        <Link to="/reclamos" className="text-primary-600 hover:underline">&larr; {t('claims')}</Link>
        <h1 className="text-2xl font-bold">Reclamo #{claim.id}</h1>
        <StatusBadge status={claim.estado} />
      </div>
      <div className="bg-white dark:bg-gray-800 p-6 rounded-xl shadow-md space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div><strong>{t('insurer')}:</strong> {claim.aseguradoraNombre}</div>
          <div><strong>{t('policyNumber')}:</strong> <Link to={`/polizas/${claim.polizaId}`} className="text-primary-600 hover:underline">{claim.polizaNumero}</Link></div>
          <div><strong>{t('policyType')}:</strong> {claim.tipoSeguro}</div>
          <div><strong>{t('claimDate')}:</strong> {format(new Date(claim.fechaSiniestro), 'dd/MM/yyyy HH:mm')}</div>
          <div><strong>{t('estimatedAmount')}:</strong> ${claim.montoEstimado}</div>
          <div><strong>{t('changeDate')}:</strong> {format(new Date(claim.fechaCreacion), 'dd/MM/yyyy HH:mm')}</div>
        </div>
        <div><strong>{t('description')}:</strong><p className="mt-1">{claim.descripcion}</p></div>
      </div>

      <div className="mt-8">
        <h2 className="text-xl font-bold mb-4">{t('history')}</h2>
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow overflow-hidden">
          <table className="min-w-full">
            <thead className="bg-gray-100 dark:bg-gray-700">
              <tr><th className="p-3 text-left">{t('previousState')}</th><th className="p-3 text-left">{t('newState')}</th>
                <th className="p-3 text-left">{t('changedBy')}</th><th className="p-3 text-left">{t('changeDate')}</th></tr>
            </thead>
            <tbody>
              {claim.historialEstados?.map((h, index) => (
                <motion.tr key={h.id} initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: index * 0.05 }} className="border-b">
                  <td className="p-3"><StatusBadge status={h.estadoAnterior} /></td>
                  <td className="p-3"><StatusBadge status={h.estadoNuevo} /></td>
                  <td className="p-3">{h.cambiadoPor}</td>
                  <td className="p-3">{format(new Date(h.fechaCambio), 'dd/MM/yyyy HH:mm')}</td>
                </motion.tr>
              )) || <tr><td colSpan="4" className="p-4 text-center text-gray-500">{t('noData')}</td></tr>}
            </tbody>
          </table>
        </div>
      </div>

      {claim.documentos?.length > 0 && (
        <div className="mt-8">
          <h2 className="text-xl font-bold mb-4">{t('documents')}</h2>
          <div className="bg-white dark:bg-gray-800 rounded-xl shadow p-4 space-y-2">
            {claim.documentos.map((doc, index) => (
              <motion.div key={doc.id} initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }}
                transition={{ delay: index * 0.05 }} className="flex justify-between items-center border-b pb-2">
                <span>{doc.nombreOriginal}</span>
                <a href={`${import.meta.env.VITE_API_URL}/upload/download/${doc.id}`} 
                  className="text-primary-600 hover:underline" target="_blank" rel="noopener noreferrer">{t('download')}</a>
              </motion.div>
            ))}
          </div>
        </div>
      )}
    </motion.div>
  );
};

export default ClaimDetail;