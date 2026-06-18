import React, { useState } from 'react';
import { useClaims } from '../hooks/useClaims';
import { useInsurers } from '../hooks/useInsurers';
import { useAuth } from '../hooks/useAuth';
import StatusBadge from '../components/common/StatusBadge';
import LoadingSpinner from '../components/common/LoadingSpinner';
import Pagination from '../components/common/Pagination';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { motion } from 'framer-motion';

const Claims = () => {
  const { t } = useTranslation();
  const { user } = useAuth();
  const [page, setPage] = useState(0);
  const [filters, setFilters] = useState({ estado: '', aseguradoraId: '', fechaDesde: '', fechaHasta: '' });
  const { claims, totalPages, isLoading } = useClaims(page, 5, filters);
  const { insurers } = useInsurers();
  const isAdmin = user?.role === 'ADMIN';

  const handleFilterChange = (e) => { setFilters({ ...filters, [e.target.name]: e.target.value }); setPage(0); };
  const clearFilters = () => { setFilters({ estado: '', aseguradoraId: '', fechaDesde: '', fechaHasta: '' }); setPage(0); };

  if (isLoading) return <LoadingSpinner />;

  return (
    <div>
      <h1 className="text-2xl font-bold mb-4">{t('claims')}</h1>
      <div className="bg-white dark:bg-gray-800 p-4 rounded-xl shadow-md mb-6 grid grid-cols-1 md:grid-cols-5 gap-4 items-end">
        <div><label className="block text-sm font-medium mb-1">{t('status')}</label>
          <select name="estado" value={filters.estado} onChange={handleFilterChange} className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700">
            <option value="">{t('all')}</option><option value="REGISTRADO">Registrado</option>
            <option value="EN_VALIDACION">En validación</option><option value="APROBADO">Aprobado</option>
            <option value="RECHAZADO">Rechazado</option><option value="PAGADO">Pagado</option>
          </select></div>
        <div><label className="block text-sm font-medium mb-1">{t('insurer')}</label>
          <select name="aseguradoraId" value={filters.aseguradoraId} onChange={handleFilterChange} className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700">
            <option value="">{t('all')}</option>{insurers.map(i => <option key={i.id} value={i.id}>{i.nombre}</option>)}
          </select></div>
        <div><label className="block text-sm font-medium mb-1">{t('dateFrom')}</label>
          <input type="date" name="fechaDesde" value={filters.fechaDesde} onChange={handleFilterChange} className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700" /></div>
        <div><label className="block text-sm font-medium mb-1">{t('dateTo')}</label>
          <input type="date" name="fechaHasta" value={filters.fechaHasta} onChange={handleFilterChange} className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700" /></div>
        <button onClick={clearFilters} className="bg-gray-500 text-white px-4 py-2 rounded-lg hover:bg-gray-600 transition">{t('clearFilters')}</button>
      </div>

      {claims.length === 0 ? (
        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="bg-white dark:bg-gray-800 p-8 rounded-xl shadow text-center">
          <p className="text-gray-500 mb-4">{t('noClaims')}</p>
          {isAdmin && (
            <Link to="/reclamos/nuevo" className="bg-primary-600 text-white px-4 py-2 rounded-lg inline-block">{t('createFirst')}</Link>
          )}
        </motion.div>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full bg-white dark:bg-gray-800 rounded-xl shadow">
            <thead className="bg-gray-100 dark:bg-gray-700">
              <tr><th className="p-3 text-left">ID</th><th className="p-3 text-left">{t('insurer')}</th>
                <th className="p-3 text-left">{t('description')}</th><th className="p-3 text-left">{t('status')}</th>
                <th className="p-3 text-left">{t('amount')}</th><th className="p-3 text-left">{t('details')}</th></tr>
            </thead>
            <tbody>
              {claims.map((c, index) => (
                <motion.tr key={c.id} initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.03 }} className="border-b hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
                  <td className="p-3">{c.id}</td><td className="p-3">{c.aseguradoraNombre}</td>
                  <td className="p-3">{c.descripcion?.substring(0, 50)}</td>
                  <td className="p-3"><StatusBadge status={c.estado} /></td>
                  <td className="p-3">${c.montoEstimado}</td>
                  <td className="p-3"><Link to={`/reclamos/${c.id}`} className="text-primary-600 hover:underline">{t('details')}</Link></td>
                </motion.tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
    </div>
  );
};

export default Claims;