import React from 'react';

const ConfirmacionModal = ({ isOpen, onConfirm, onCancel, mensaje }) => {
    if (!isOpen) return null;
    return (
        <div className="modal-overlay" onClick={onCancel}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <p>{mensaje || "¿Eliminar este registro?"}</p>
                <div className="modal-buttons">
                    <button className="btn-danger" onClick={onConfirm}>Eliminar</button>
                    <button className="btn-secondary" onClick={onCancel}>Cancelar</button>
                </div>
            </div>
        </div>
    );
};

export default ConfirmacionModal;