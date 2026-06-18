import React from 'react';

const statusMap = {
  REGISTRADO: { label: 'Registrado', color: 'bg-gray-100 text-gray-800' },
  EN_VALIDACION: { label: 'En validación', color: 'bg-blue-100 text-blue-800' },
  APROBADO: { label: 'Aprobado', color: 'bg-green-100 text-green-800' },
  RECHAZADO: { label: 'Rechazado', color: 'bg-red-100 text-red-800' },
  PAGADO: { label: 'Pagado', color: 'bg-purple-100 text-purple-800' },
};

const StatusBadge = ({ status }) => {
  const normalized = status?.toUpperCase() || '';
  const { label, color } = statusMap[normalized] || { label: normalized, color: 'bg-gray-100 text-gray-800' };
  return (
    <span className={`px-2 py-1 rounded-full text-xs font-medium ${color}`}>{label}</span>
  );
};

export default StatusBadge;