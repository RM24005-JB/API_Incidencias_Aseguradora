import React, { useState, useEffect } from 'react';

const IncidenciaFormulario = ({ incidenciaAEditar, categorias, onGuardar, onCancelar }) => {
    const [descripcion, setDescripcion] = useState('');
    const [estado, setEstado] = useState('ABIERTA');
    const [categoriaId, setCategoriaId] = useState('');

    useEffect(() => {
        if (incidenciaAEditar) {
            setDescripcion(incidenciaAEditar.descripcion);
            setEstado(incidenciaAEditar.estado);
            setCategoriaId(incidenciaAEditar.categoriaId.toString());
        } else {
            setDescripcion('');
            setEstado('ABIERTA');
            setCategoriaId(categorias.length > 0 ? categorias[0].id.toString() : '');
        }
    }, [incidenciaAEditar, categorias]);

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!descripcion.trim()) {
            alert("La descripción es obligatoria");
            return;
        }
        if (!categoriaId) {
            alert("Debe seleccionar una categoría");
            return;
        }
        const datos = {
            descripcion: descripcion.trim(),
            estado,
            categoriaId: parseInt(categoriaId)
        };
        onGuardar(datos);
    };

    return (
        <div className="formulario">
            <h3>{incidenciaAEditar ? "Editar Incidencia" : "Nueva Incidencia"}</h3>
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Descripción</label>
                    <textarea rows="3" value={descripcion} onChange={(e) => setDescripcion(e.target.value)} placeholder="Describa la incidencia" />
                </div>
                <div className="form-group">
                    <label>Categoría</label>
                    <select value={categoriaId} onChange={(e) => setCategoriaId(e.target.value)}>
                        {categorias.map(cat => (
                            <option key={cat.id} value={cat.id}>{cat.nombre}</option>
                        ))}
                    </select>
                </div>
                <div className="form-group">
                    <label>Estado</label>
                    <select value={estado} onChange={(e) => setEstado(e.target.value)}>
                        <option value="ABIERTA">Abierta</option>
                        <option value="EN_PROCESO">En Proceso</option>
                        <option value="CERRADA">Cerrada</option>
                    </select>
                </div>
                <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
                    {incidenciaAEditar && <button type="button" className="btn-secondary" onClick={onCancelar}>Cancelar</button>}
                    <button type="submit" className="btn-primary">{incidenciaAEditar ? "Actualizar" : "Crear"}</button>
                </div>
            </form>
        </div>
    );
};

export default IncidenciaFormulario;