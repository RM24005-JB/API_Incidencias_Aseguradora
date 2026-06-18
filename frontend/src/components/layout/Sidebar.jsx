import React from 'react';
import { NavLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { X, LayoutDashboard, FileText, AlertCircle, PlusCircle, Users, ShieldCheck } from 'lucide-react';

const Sidebar = ({ isOpen, onClose }) => {
  const { t } = useTranslation();
  const userRole = localStorage.getItem('userRole');

  const navItems = [
    { to: '/dashboard', icon: LayoutDashboard, label: t('dashboard'), roles: ['USER', 'ADMIN'] },
    { to: '/polizas', icon: FileText, label: t('policies'), roles: ['USER', 'ADMIN'] },
    { to: '/reclamos', icon: AlertCircle, label: t('claims'), roles: ['USER', 'ADMIN'] },
    { to: '/reclamos/nuevo', icon: PlusCircle, label: t('newClaim'), roles: ['USER', 'ADMIN'] },
    { to: '/aseguradoras', icon: ShieldCheck, label: t('insurers'), roles: ['USER', 'ADMIN'] },
    { to: '/admin/reclamos', icon: AlertCircle, label: t('adminClaims'), roles: ['ADMIN'] },
    { to: '/admin/usuarios', icon: Users, label: t('adminUsers'), roles: ['ADMIN'] },
    { to: '/admin/aseguradoras', icon: ShieldCheck, label: t('adminInsurers'), roles: ['ADMIN'] },
  ];

  const filtered = navItems.filter(item => item.roles.includes(userRole || 'USER'));

  return (
    <>
      <aside className={`fixed inset-y-0 left-0 z-40 w-64 bg-white dark:bg-gray-900 shadow-xl transform transition-transform duration-300 ease-in-out md:relative md:translate-x-0 ${isOpen ? 'translate-x-0' : '-translate-x-full'}`}>
        <div className="flex justify-end p-2 md:hidden">
          <button onClick={onClose}><X /></button>
        </div>
        <div className="flex flex-col gap-1 p-4">
          {filtered.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              onClick={onClose}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2 rounded-lg transition ${
                  isActive ? 'bg-primary-50 dark:bg-primary-900 text-primary-700' : 'hover:bg-gray-100 dark:hover:bg-gray-800'
                }`
              }
            >
              <item.icon size={18} />
              <span className="text-sm">{item.label}</span>
            </NavLink>
          ))}
        </div>
      </aside>
      {isOpen && <div className="fixed inset-0 bg-black bg-opacity-50 z-30 md:hidden" onClick={onClose}></div>}
    </>
  );
};

export default Sidebar;