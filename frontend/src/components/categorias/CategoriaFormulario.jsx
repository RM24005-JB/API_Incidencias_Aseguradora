import React, { useState, useEffect } from 'react';

const CategoriaFormulario = ({ categoriaAEditar, onGuardar, onCancelar }) => {
    const [nombre, setNombre] = useState('');
    const [descripcion, setDescripcion] = useState('');

    useEffect(() => {
        if (categoriaAEditar) {
            setNombre(categoriaAEditar.nombre);
            setDescripcion(categoriaAEditar.descripcion || '');
        } else {
            setNombre('');
            setDescripcion('');
        }
    }, [categoriaAEditar]);

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!nombre.trim()) {
            alert("El nombre de la categoría es obligatorio");
            return;
        }
        onGuardar({ nombre: nombre.trim(), descripcion: descripcion.trim() });
    };

    return (
        <div className="formulario">
            <h3>{categoriaAEditar ? "Editar Categoría" : "Nueva Categoría"}</h3>
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Nombre</label>
                    <input type="text" value={nombre} onChange={(e) => setNombre(e.target.value)} placeholder="Ej: Seguro de Autos" />
                </div>
                <div className="form-group">
                    <label>Descripción (opcional)</label>
                    <textarea rows="2" value={descripcion} onChange={(e) => setDescripcion(e.target.value)} placeholder="Descripción de la categoría" />
                </div>
                <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
                    {categoriaAEditar && <button type="button" className="btn-secondary" onClick={onCancelar}>Cancelar</button>}
                    <button type="submit" className="btn-primary">{categoriaAEditar ? "Actualizar" : "Crear"}</button>
                </div>
            </form>
        </div>
    );
};

export default CategoriaFormulario;