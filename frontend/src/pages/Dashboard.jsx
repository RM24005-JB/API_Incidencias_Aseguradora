import React from 'react';
import { motion } from 'framer-motion';
import { useAuth } from '../hooks/useAuth';
import { useDashboardStats } from '../hooks/useDashboardStats';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, PieChart, Pie, Cell, Legend } from 'recharts';
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
  
  const statusData = Object.entries(stats?.reclamosPorEstado || {}).map(([key, value]) => ({ 
    name: t(statusKeyMap[key] || key.toLowerCase().replace('_', '')), 
    value 
  }));
  const colors = ['#4338CA', '#639922', '#EF9F27', '#E24B4A', '#9CA3AF'];

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
          <ResponsiveContainer width="100%" height={350}>
            <BarChart data={monthlyData} margin={{ top: 20, right: 30, left: 20, bottom: 40 }}>
              <XAxis 
                dataKey="month" 
                tick={{ fontSize: 12, fill: '#6B7280' }}
                axisLine={{ stroke: '#E5E7EB' }}
                tickLine={{ stroke: '#E5E7EB' }}
              />
              <YAxis 
                tick={{ fontSize: 12, fill: '#6B7280' }}
                axisLine={{ stroke: '#E5E7EB' }}
                tickLine={{ stroke: '#E5E7EB' }}
              />
              <Tooltip 
                contentStyle={{ 
                  backgroundColor: '#1F2937', 
                  border: 'none', 
                  borderRadius: '8px',
                  color: '#fff'
                }}
                formatter={(value) => [value, t('count')]}
              />
              <Bar 
                dataKey="count" 
                fill="#4338CA" 
                radius={[6, 6, 0, 0]}
                barSize={30}
              />
            </BarChart>
          </ResponsiveContainer>
        </div>
        <div className="bg-white dark:bg-gray-800 p-4 rounded-xl shadow-sm border">
          <h3 className="text-md font-semibold mb-4">{t('claimStatus')}</h3>
          <ResponsiveContainer width="100%" height={320}>
            <PieChart margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
              <Pie 
                data={statusData} 
                cx="50%" 
                cy="45%" 
                innerRadius={70} 
                outerRadius={100} 
                dataKey="value" 
                labelLine={false}
                label={false}
              >
                {statusData.map((entry, idx) => <Cell key={`cell-${idx}`} fill={colors[idx % colors.length]} />)}
              </Pie>
              <Legend 
                verticalAlign="bottom" 
                height={50} 
                iconType="circle"
                formatter={(value, entry) => {
                  const translated = t(value.toLowerCase().replace('_', ''));
                  return `${translated} (${entry.payload.value})`;
                }}
              />
            </PieChart>
          </ResponsiveContainer>
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
                  <th className="px-4 py-3 text-left text-xs font-medium uppercase">{t('status')}</th>
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