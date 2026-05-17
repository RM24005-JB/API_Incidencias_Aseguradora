import React, { useState } from 'react';
import ConfirmacionModal from '../common/ConfirmacionModal';
import IncidenciaFormulario from './IncidenciaFormulario';

const IncidenciaLista = ({ incidencias, setIncidencias, categorias }) => {
    const [mostrarFormulario, setMostrarFormulario] = useState(false);
    const [incidenciaEditando, setIncidenciaEditando] = useState(null);
    const [modalEliminar, setModalEliminar] = useState({ open: false, id: null });

    const obtenerNombreCategoria = (categoriaId) => {
        const cat = categorias.find(c => c.id === categoriaId);
        return cat ? cat.nombre : "Sin categoría";
    };

    const crear = (nueva) => {
        const nuevaConId = { ...nueva, id: Date.now() };
        setIncidencias([...incidencias, nuevaConId]);
        setMostrarFormulario(false);
    };

    const actualizar = (id, datos) => {
        setIncidencias(incidencias.map(inc => inc.id === id ? { ...inc, ...datos } : inc));
        setIncidenciaEditando(null);
    };

    const eliminar = (id) => {
        setIncidencias(incidencias.filter(inc => inc.id !== id));
        setModalEliminar({ open: false, id: null });
    };

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                <h2 className="titulo-seccion">Incidencias Registradas</h2>
                <button className="btn-primary" onClick={() => setMostrarFormulario(true)}>Nueva Incidencia</button>
            </div>

            {mostrarFormulario && (
                <IncidenciaFormulario
                    categorias={categorias}
                    onGuardar={crear}
                    onCancelar={() => setMostrarFormulario(false)}
                />
            )}

            {incidenciaEditando && (
                <IncidenciaFormulario
                    incidenciaAEditar={incidenciaEditando}
                    categorias={categorias}
                    onGuardar={(datos) => actualizar(incidenciaEditando.id, datos)}
                    onCancelar={() => setIncidenciaEditando(null)}
                />
            )}

            {incidencias.length === 0 ? (
                <p className="text-center">No hay incidencias registradas</p>
            ) : (
                <div className="lista-incidencias">
                    {incidencias.map(inc => (
                        <div key={inc.id} className="incidencia-item">
                            <div style={{ flex: 1 }}>
                                <div className="incidencia-header">
                                    <span className="incidencia-descripcion">{inc.descripcion}</span>
                                    <span className="incidencia-categoria">{obtenerNombreCategoria(inc.categoriaId)}</span>
                                    <span className={`incidencia-estado estado-${inc.estado}`}>{inc.estado}</span>
                                </div>
                            </div>
                            <div className="incidencia-actions">
                                <button className="btn-secondary" onClick={() => setIncidenciaEditando(inc)}>Editar</button>
                                <button className="btn-danger" onClick={() => setModalEliminar({ open: true, id: inc.id })}>Eliminar</button>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            <ConfirmacionModal
                isOpen={modalEliminar.open}
                onConfirm={() => eliminar(modalEliminar.id)}
                onCancel={() => setModalEliminar({ open: false, id: null })}
                mensaje="¿Eliminar esta incidencia permanentemente?"
            />
        </div>
    );
};

export default IncidenciaLista;