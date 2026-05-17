import React, { useState } from 'react';
import ConfirmacionModal from '../common/ConfirmacionModal';
import CategoriaFormulario from './CategoriaFormulario';

const CategoriaLista = ({ categorias, setCategorias }) => {
    const [mostrarFormulario, setMostrarFormulario] = useState(false);
    const [categoriaEditando, setCategoriaEditando] = useState(null);
    const [modalEliminar, setModalEliminar] = useState({ open: false, id: null });

    const crear = (nueva) => {
        const nuevaConId = { ...nueva, id: Date.now() };
        setCategorias([...categorias, nuevaConId]);
        setMostrarFormulario(false);
    };

    const actualizar = (id, datos) => {
        setCategorias(categorias.map(cat => cat.id === id ? { ...cat, ...datos } : cat));
        setCategoriaEditando(null);
    };

    const eliminar = (id) => {
        setCategorias(categorias.filter(cat => cat.id !== id));
        setModalEliminar({ open: false, id: null });
    };

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                <h2 className="titulo-seccion">Categorías de Incidencias</h2>
                <button className="btn-primary" onClick={() => setMostrarFormulario(true)}>Nueva Categoría</button>
            </div>

            {mostrarFormulario && (
                <CategoriaFormulario onGuardar={crear} onCancelar={() => setMostrarFormulario(false)} />
            )}

            {categoriaEditando && (
                <CategoriaFormulario
                    categoriaAEditar={categoriaEditando}
                    onGuardar={(datos) => actualizar(categoriaEditando.id, datos)}
                    onCancelar={() => setCategoriaEditando(null)}
                />
            )}

            {categorias.length === 0 ? (
                <p className="text-center">No hay categorías registradas</p>
            ) : (
                <div className="lista-incidencias">
                    {categorias.map(cat => (
                        <div key={cat.id} className="incidencia-item">
                            <div>
                                <strong>{cat.nombre}</strong>
                                {cat.descripcion && <p style={{ fontSize: '0.85rem', marginTop: '0.25rem' }}>{cat.descripcion}</p>}
                            </div>
                            <div className="incidencia-actions">
                                <button className="btn-secondary" onClick={() => setCategoriaEditando(cat)}>Editar</button>
                                <button className="btn-danger" onClick={() => setModalEliminar({ open: true, id: cat.id })}>Eliminar</button>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            <ConfirmacionModal
                isOpen={modalEliminar.open}
                onConfirm={() => eliminar(modalEliminar.id)}
                onCancel={() => setModalEliminar({ open: false, id: null })}
                mensaje="¿Eliminar esta categoría? Las incidencias asociadas quedarán sin categoría"
            />
        </div>
    );
};

export default CategoriaLista;