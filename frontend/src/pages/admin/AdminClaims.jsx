import React, { useState } from 'react';
import { useAdminClaims } from '../../hooks/useAdminClaims';
import { useInsurers } from '../../hooks/useInsurers';
import StatusBadge from '../../components/common/StatusBadge';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import Pagination from '../../components/common/Pagination';
import { useTranslation } from 'react-i18next';
import { motion } from 'framer-motion';

const AdminClaims = () => {
  const { t } = useTranslation();
  const [page, setPage] = useState(0);
  const [filters, setFilters] = useState({ estado: '', aseguradoraId: '', fechaDesde: '', fechaHasta: '' });
  const { claims, totalPages, isLoading, updateClaimStatus } = useAdminClaims(page, 10, filters);
  const { insurers } = useInsurers();

  const handleFilterChange = (e) => { setFilters({ ...filters, [e.target.name]: e.target.value }); setPage(0); };
  const clearFilters = () => { setFilters({ estado: '', aseguradoraId: '', fechaDesde: '', fechaHasta: '' }); setPage(0); };
  const handleStatusChange = (id, newStatus) => updateClaimStatus.mutate({ id, estado: newStatus });

  if (isLoading) return <LoadingSpinner />;

  return (
    <div>
      <h1 className="text-2xl font-bold mb-4">{t('adminClaims')}</h1>
      <div className="bg-white dark:bg-gray-800 p-4 rounded-xl shadow-md mb-6 grid grid-cols-1 md:grid-cols-5 gap-4 items-end">
        <div><label className="block text-sm mb-1">{t('status')}</label>
          <select name="estado" value={filters.estado} onChange={handleFilterChange} className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700">
            <option value="">{t('all')}</option><option value="REGISTRADO">Registrado</option><option value="EN_VALIDACION">En validación</option>
            <option value="APROBADO">Aprobado</option><option value="RECHAZADO">Rechazado</option><option value="PAGADO">Pagado</option>
          </select></div>
        <div><label className="block text-sm mb-1">{t('insurer')}</label>
          <select name="aseguradoraId" value={filters.aseguradoraId} onChange={handleFilterChange} className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700">
            <option value="">{t('all')}</option>{insurers.map(i => <option key={i.id} value={i.id}>{i.nombre}</option>)}
          </select></div>
        <div><label className="block text-sm mb-1">{t('dateFrom')}</label><input type="date" name="fechaDesde" value={filters.fechaDesde} onChange={handleFilterChange} className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700" /></div>
        <div><label className="block text-sm mb-1">{t('dateTo')}</label><input type="date" name="fechaHasta" value={filters.fechaHasta} onChange={handleFilterChange} className="w-full border rounded-lg px-3 py-2 dark:bg-gray-700" /></div>
        <button onClick={clearFilters} className="bg-gray-500 text-white px-4 py-2 rounded-lg hover:bg-gray-600 transition">{t('clearFilters')}</button>
      </div>
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white dark:bg-gray-800 rounded-xl shadow">
          <thead className="bg-gray-100 dark:bg-gray-700"><tr><th className="p-3">{t('id')}</th><th className="p-3">{t('email')}</th><th className="p-3">{t('description')}</th><th className="p-3">{t('status')}</th><th className="p-3">{t('actions')}</th></tr></thead>
          <tbody>
            {claims.map((c, index) => (
              <motion.tr key={c.id} initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: index * 0.03 }}
                className="border-b hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
                <td className="p-3">{c.id}</td><td className="p-3">{c.usuarioEmail}</td><td className="p-3">{c.descripcion?.substring(0, 40)}</td>
                <td className="p-3"><StatusBadge status={c.estado} /></td>
                <td className="p-3"><select onChange={(e) => handleStatusChange(c.id, e.target.value)} defaultValue={c.estado} className="border rounded px-2 py-1 text-sm dark:bg-gray-700">
                  <option value="REGISTRADO">Registrado</option><option value="EN_VALIDACION">En validación</option>
                  <option value="APROBADO">Aprobado</option><option value="RECHAZADO">Rechazado</option><option value="PAGADO">Pagado</option>
                </select></td>
              </motion.tr>
            ))}
          </tbody>
        </table>
      </div>
      <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
    </div>
  );
};

export default AdminClaims;