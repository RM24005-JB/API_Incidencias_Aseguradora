import React from 'react';

const Footer = () => {
  return (
    <footer className="bg-gray-100 dark:bg-gray-800 text-center py-3 text-xs text-gray-600 dark:text-gray-400 mt-auto">
      &copy; {new Date().getFullYear()} INCIDENCIAS DE ASEGURADORAS. Todos los derechos reservados.
    </footer>
  );
};

export default Footer;