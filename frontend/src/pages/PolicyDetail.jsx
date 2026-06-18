import React from 'react';
import { useParams, Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import api from '../services/api';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { useTranslation } from 'react-i18next';
import { motion } from 'framer-motion';

const PolicyDetail = () => {
  const { id } = useParams();
  const { t } = useTranslation();
  const userRole = localStorage.getItem('userRole');
  const isAdmin = userRole === 'ADMIN';
  
  const { data: policy, isLoading } = useQuery({
    queryKey: ['policy', id],
    queryFn: () => {
      const endpoint = isAdmin ? `/admin/polizas/${id}` : `/polizas/${id}`;
      return api.get(endpoint).then(res => res.data);
    },
  });

  if (isLoading) return <LoadingSpinner />;
  if (!policy) return (
    <div className="text-center py-10">
      <p className="text-gray-500">{t('noData')}</p>
      <Link to="/polizas" className="text-primary-600 hover:underline mt-2 inline-block">{t('back')}</Link>
    </div>
  );

  return (
    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="max-w-3xl mx-auto">
      <div className="mb-4">
        <Link to="/polizas" className="text-primary-600 hover:underline flex items-center gap-2">
          <span>←</span> {t('back')}
        </Link>
      </div>
      <h1 className="text-2xl font-bold mb-4">{t('policyDetails')}</h1>
      <div className="bg-white dark:bg-gray-800 p-6 rounded-xl shadow-md space-y-6">
        <div className="grid grid-cols-2 gap-4">
          <div><strong>{t('insurer')}:</strong> {policy.nombreAseguradora}</div>
          <div><strong>{t('policyNumber')}:</strong> {policy.numeroPoliza}</div>
          <div><strong>{t('policyType')}:</strong> {policy.tipo}</div>
          <div><strong>{t('startDate')}:</strong> {policy.fechaInicio}</div>
          <div><strong>{t('endDate')}:</strong> {policy.fechaFin}</div>
        </div>
        <div className="border-t pt-4">
          <h3 className="font-semibold mb-2">{t('termsAndConditions')}</h3>
          <div className="bg-gray-50 dark:bg-gray-700 p-4 rounded-lg text-sm space-y-3">
            <div>
              <p className="font-semibold mb-1">Cobertura Principal:</p>
              <p>{policy.coberturas || "Responsabilidad Civil, Daños a terceros, Gastos médicos básicos"}</p>
            </div>
            <div className="grid grid-cols-2 gap-2">
              <div>
                <p className="font-semibold mb-1">Deducible:</p>
                <p>$500.00 USD</p>
              </div>
              <div>
                <p className="font-semibold mb-1">Límite de Cobertura:</p>
                <p>$50,000.00 USD</p>
              </div>
            </div>
            <div>
              <p className="font-semibold mb-1">Condiciones Generales:</p>
              <ul className="list-disc list-inside space-y-1 text-xs">
                <li>La póliza cubre daños materiales y lesiones corporales causadas por accidentes de tránsito</li>
                <li>Cobertura válida en territorio de El Salvador y países centroamericanos</li>
                <li>El asegurado debe tener licencia de conducir vigente al momento del siniestro</li>
                <li>El vehículo debe mantenerse en condiciones mecánicas adecuadas</li>
              </ul>
            </div>
            <div>
              <p className="font-semibold mb-1">Exclusiones:</p>
              <ul className="list-disc list-inside space-y-1 text-xs">
                <li>Daños preexistentes o desgaste normal del vehículo</li>
                <li>Uso del vehículo para fines comerciales no autorizados</li>
                <li>Conducción bajo influencia de alcohol o sustancias</li>
                <li>Daños causados por actos de guerra o terrorismo</li>
                <li>Robo de accesorios no fijados permanentemente al vehículo</li>
              </ul>
            </div>
            <div>
              <p className="font-semibold mb-1">Proceso de Reclamo:</p>
              <ul className="list-disc list-inside space-y-1 text-xs">
                <li>Notificar el siniestro dentro de las 48 horas siguientes al evento</li>
                <li>Presentar reporte policial en caso de accidente con terceros</li>
                <li>Proporcionar fotografías de los daños y documentación completa</li>
                <li>Cooperar con el ajustador designado por la aseguradora</li>
              </ul>
            </div>
            <div>
              <p className="font-semibold mb-1">Documentos Requeridos:</p>
              <ul className="list-disc list-inside space-y-1 text-xs">
                <li>Licencia de conducir vigente del conductor</li>
                <li>Póliza de seguro vigente</li>
                <li>Reporte policial (cuando aplique)</li>
                <li>Fotografías del siniestro y daños</li>
                <li>Presupuestos de reparación (cuando aplique)</li>
              </ul>
            </div>
            <div>
              <p className="font-semibold mb-1">Vigencia y Renovación:</p>
              <p className="text-xs">Esta póliza tiene vigencia de 12 meses. La renovación automática está sujeta a evaluación del historial de siniestros y cumplimiento de pagos.</p>
            </div>
          </div>
        </div>
        <Link to="/polizas" className="inline-block bg-gray-500 text-white px-4 py-2 rounded-lg hover:bg-gray-600 transition">{t('back')}</Link>
      </div>
    </motion.div>
  );
};

export default PolicyDetail;