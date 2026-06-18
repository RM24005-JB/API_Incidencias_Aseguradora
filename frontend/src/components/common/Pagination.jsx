import React from 'react';
import { useTranslation } from 'react-i18next';

const Pagination = ({ currentPage, totalPages, onPageChange }) => {
  const { t } = useTranslation();
  const maxButtons = 5;
  let startPage = Math.max(0, currentPage - Math.floor(maxButtons / 2));
  let endPage = Math.min(totalPages - 1, startPage + maxButtons - 1);
  if (endPage - startPage + 1 < maxButtons) {
    startPage = Math.max(0, endPage - maxButtons + 1);
  }
  const pages = Array.from({ length: endPage - startPage + 1 }, (_, i) => startPage + i);

  return (
    <div className="flex justify-center items-center space-x-2 mt-6">
      <button onClick={() => onPageChange(currentPage - 1)} disabled={currentPage === 0}
        className="px-3 py-1 rounded bg-gray-200 dark:bg-gray-700 disabled:opacity-50">{t('previous')}</button>
      {startPage > 0 && (
        <>
          <button onClick={() => onPageChange(0)} className="px-3 py-1 rounded bg-gray-200 dark:bg-gray-700">1</button>
          {startPage > 1 && <span className="px-2">...</span>}
        </>
      )}
      {pages.map(page => (
        <button key={page} onClick={() => onPageChange(page)}
          className={`px-3 py-1 rounded ${page === currentPage ? 'bg-primary-600 text-white' : 'bg-gray-200 dark:bg-gray-700'}`}>
          {page + 1}
        </button>
      ))}
      {endPage < totalPages - 1 && (
        <>
          {endPage < totalPages - 2 && <span className="px-2">...</span>}
          <button onClick={() => onPageChange(totalPages - 1)} className="px-3 py-1 rounded bg-gray-200 dark:bg-gray-700">{totalPages}</button>
        </>
      )}
      <button onClick={() => onPageChange(currentPage + 1)} disabled={currentPage === totalPages - 1}
        className="px-3 py-1 rounded bg-gray-200 dark:bg-gray-700 disabled:opacity-50">{t('next')}</button>
    </div>
  );
};

export default Pagination;