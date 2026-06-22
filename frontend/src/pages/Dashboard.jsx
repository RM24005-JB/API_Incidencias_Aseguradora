import React from 'react';
import { motion } from 'framer-motion';
import { useAuth } from '../hooks/useAuth';
import { useDashboardStats } from '../hooks/useDashboardStats';
import { PieChart, Pie, Cell, Legend, ResponsiveContainer } from 'recharts';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { useTranslation } from 'react-i18next';
import StatusBadge from '../components/common/StatusBadge';

const Dashboard = () => {
  const { t } = useTranslation();
  const { user } = useAuth();
  const { data: stats, isLoading } = useDashboardStats();
  if (isLoading) return <LoadingSpinner />;

  const monthlyData = stats?.monthlyClaims || [];
  
  // Map Spanish enum keys to English translation keys
  const statusKeyMap = {
    'APROBADO': 'approved',
    'PAGADO': 'paid',
    'RECHAZADO': 'rejected',
    'REGISTRADO': 'registered',
    'EN_VALIDACION': 'inValidation'
  };
  
  // Map colors to specific states for consistency
  const statusColorMap = {
    'APROBADO': '#22C55E', // green
    'PAGADO': '#9333EA', // purple
    'RECHAZADO': '#EF4444', // red
    'REGISTRADO': '#6B7280', // gray
    'EN_VALIDACION': '#3B82F6' // blue
  };
  
  const statusData = Object.entries(stats?.reclamosPorEstado || {}).map(([key, value]) => ({ 
    name: t(statusKeyMap[key] || key.toLowerCase().replace('_', '')), 
    value,
    color: statusColorMap[key] || '#6B7280'
  }));

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900 dark:text-white">{t('dashboard')}</h1>
        <p className="text-gray-500 dark:text-gray-400">{t('welcome')}, {user?.nombre || 'Usuario'}</p>
      </div>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {[
          { label: t('totalPolicies'), value: stats?.totalPolicies || 0, color: 'text-gray-900', delay: 0.1 },
          { label: t('openClaims'), value: stats?.openClaims || 0, color: 'text-yellow-600', delay: 0.2 },
          { label: t('approvedClaims'), value: stats?.approvedClaims || 0, color: 'text-green-600', delay: 0.3 },
          { label: t('totalAmount'), value: `$${stats?.totalAmount?.toLocaleString() || 0}`, color: 'text-gray-900', delay: 0.4 }
        ].map((card, index) => (
          <motion.div key={index} initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}
            transition={{ delay: card.delay }} className="bg-white dark:bg-gray-800 p-4 rounded-xl shadow-sm border hover:shadow-md transition-shadow">
            <div className="text-sm text-gray-500">{card.label}</div>
            <div className={`text-2xl font-bold ${card.color}`}>{card.value}</div>
          </motion.div>
        ))}
      </div>
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white dark:bg-gray-800 p-6 rounded-xl shadow-sm border">
          <h3 className="text-lg font-semibold mb-4">{t('claimsByMonth')}</h3>
          <div className="grid grid-cols-3 gap-3">
            {monthlyData.map((item, index) => (
              <div key={index} className="bg-gradient-to-br from-indigo-50 to-indigo-100 dark:from-gray-700 dark:to-gray-600 p-4 rounded-lg text-center">
                <div className="text-xs font-medium text-indigo-700 dark:text-indigo-300 mb-1">{item.month}</div>
                <div className="text-2xl font-bold text-indigo-900 dark:text-white">{item.count}</div>
              </div>
            ))}
          </div>
        </div>
        <div className="bg-white dark:bg-gray-800 p-6 rounded-xl shadow-sm border">
          <h3 className="text-lg font-semibold mb-4">{t('claimStatus')}</h3>
          <div className="grid grid-cols-2 gap-3">
            {statusData.map((entry, idx) => (
              <div key={idx} className="flex items-center gap-3 p-3 bg-gray-50 dark:bg-gray-700 rounded-lg">
                <div className="w-4 h-4 rounded-full" style={{ backgroundColor: entry.color }} />
                <div className="flex-1">
                  <div className="text-sm font-medium text-gray-700 dark:text-gray-300">{entry.name}</div>
                  <div className="text-lg font-bold text-gray-900 dark:text-white">{entry.value}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
      {user?.role === 'ADMIN' && (
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border overflow-hidden">
          <div className="px-4 py-3 border-b"><h3 className="text-md font-semibold">{t('recentClaims')}</h3></div>
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y">
              <thead className="bg-gray-50 dark:bg-gray-700">
                <tr><th className="px-4 py-3 text-left text-xs font-medium uppercase">ID</th>
                  <th className="px-4 py-3 text-left text-xs font-medium uppercase">{t('policyNumber')}</th>
                  <th className="px-4 py-3 text-left text-xs font-medium uppercase">{t('description')}</th>
                  <th className="px-4 py-3 text-left text-xs font-medium uppercase w-32">{t('status')}</th>
                  <th className="px-4 py-3 text-left text-xs font-medium uppercase">{t('amount')}</th></tr>
              </thead>
              <tbody>
                {stats?.recentClaims?.map((claim, index) => (
                  <motion.tr key={claim.id} initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: index * 0.05 }}>
                    <td className="px-4 py-3 text-sm">#{claim.id}</td>
                    <td className="px-4 py-3 text-sm">{claim.policy}</td>
                    <td className="px-4 py-3 text-sm">{claim.description}</td>
                    <td className="px-4 py-3"><StatusBadge status={claim.status} /></td>
                    <td className="px-4 py-3 text-sm font-medium">${claim.amount.toLocaleString()}</td>
                  </motion.tr>
                )) || <tr><td colSpan="5" className="px-4 py-8 text-center text-gray-500">{t('noData')}</td></tr>}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;