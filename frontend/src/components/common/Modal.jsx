import React from 'react';
import ReactModal from 'react-modal';

ReactModal.setAppElement('#root');

const Modal = ({ isOpen, onClose, onConfirm, title, message, confirmText = 'Confirmar', cancelText = 'Cancelar' }) => {
  return (
    <ReactModal
      isOpen={isOpen}
      onRequestClose={onClose}
      className="bg-white dark:bg-gray-800 rounded-xl shadow-xl p-6 max-w-md w-full mx-4"
      overlayClassName="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    >
      <h2 className="text-xl font-bold mb-4">{title}</h2>
      <p className="mb-6">{message}</p>
      <div className="flex justify-end space-x-3">
        <button onClick={onClose} className="px-4 py-2 bg-gray-300 dark:bg-gray-600 rounded-lg">{cancelText}</button>
        <button onClick={onConfirm} className="px-4 py-2 bg-red-600 text-white rounded-lg">{confirmText}</button>
      </div>
    </ReactModal>
  );
};

export default Modal;