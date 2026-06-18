import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useTheme } from '../../contexts/ThemeContext';
import { logout } from '../../services/auth';
import { Shield, Sun, Moon, LogOut, Menu, User, ChevronDown } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';

const Navbar = ({ onMenuClick }) => {
  const { t, i18n } = useTranslation();
  const { darkMode, toggleDarkMode } = useTheme();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [showUserMenu, setShowUserMenu] = useState(false);

  const handleLogout = async () => {
    await logout();
    navigate('/login');
    setShowUserMenu(false);
  };

  return (
    <nav className="bg-white dark:bg-gray-900 shadow-md sticky top-0 z-50">
      <div className="container mx-auto px-4">
        <div className="flex justify-between items-center h-14">
          <div className="flex items-center gap-3">
            <button onClick={onMenuClick} className="md:hidden p-2 rounded-md hover:bg-gray-100 dark:hover:bg-gray-800">
              <Menu size={20} />
            </button>
            <Link to="/dashboard" className="flex items-center gap-2">
              <Shield className="h-6 w-6 text-primary-600" />
              <span className="font-bold text-gray-800 dark:text-white">INCIDENCIAS DE ASEGURADORAS</span>
            </Link>
          </div>
          <div className="flex items-center gap-2">
            <button onClick={toggleDarkMode} className="p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-800">
              {darkMode ? <Sun size={18} /> : <Moon size={18} />}
            </button>
            <button onClick={() => i18n.changeLanguage('es')} className="px-2 py-1 text-xs">ES</button>
            <button onClick={() => i18n.changeLanguage('en')} className="px-2 py-1 text-xs">EN</button>
            <div className="relative">
              <button 
                onClick={() => setShowUserMenu(!showUserMenu)}
                className="flex items-center gap-2 px-2 py-1 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 transition"
              >
                <div className="w-8 h-8 rounded-full bg-primary-100 dark:bg-primary-900 flex items-center justify-center text-sm font-medium">
                  {user?.nombre?.charAt(0) || 'U'}
                </div>
                <ChevronDown size={14} className="text-gray-600 dark:text-gray-400" />
              </button>
              
              {showUserMenu && (
                <div className="absolute right-0 mt-2 w-64 bg-white dark:bg-gray-800 rounded-lg shadow-lg border dark:border-gray-700 z-50">
                  <div className="p-4 border-b dark:border-gray-700">
                    <div className="flex items-center gap-3">
                      <div className="w-12 h-12 rounded-full bg-primary-100 dark:bg-primary-900 flex items-center justify-center text-lg font-medium">
                        {user?.nombre?.charAt(0) || 'U'}
                      </div>
                      <div>
                        <p className="font-semibold text-gray-800 dark:text-white">{user?.nombre || 'Usuario'}</p>
                        <p className="text-sm text-gray-600 dark:text-gray-400">{user?.email || 'usuario@example.com'}</p>
                        <span className="inline-block mt-1 px-2 py-0.5 text-xs rounded-full bg-primary-100 dark:bg-primary-900 text-primary-600 dark:text-primary-400">
                          {user?.role === 'ADMIN' ? 'Administrador' : 'Cliente'}
                        </span>
                      </div>
                    </div>
                  </div>
                  <div className="p-2">
                    <button 
                      onClick={handleLogout}
                      className="w-full flex items-center gap-2 px-3 py-2 text-left rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition text-gray-700 dark:text-gray-300"
                    >
                      <LogOut size={16} />
                      <span>{t('logout')}</span>
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;