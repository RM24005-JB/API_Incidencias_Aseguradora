import React from 'react';

const Dashboard = ({ incidencias, categorias }) => {
    const totalIncidencias = incidencias.length;
    const abiertas = incidencias.filter(i => i.estado === 'ABIERTA').length;
    const enProceso = incidencias.filter(i => i.estado === 'EN_PROCESO').length;
    const cerradas = incidencias.filter(i => i.estado === 'CERRADA').length;

    const incidenciasPorCategoria = categorias.map(cat => ({
        nombre: cat.nombre,
        cantidad: incidencias.filter(i => i.categoriaId === cat.id).length
    }));

    return (
        <div>
            <div className="banner">
                <h2>Panel de Control</h2>
                <p>Resumen general de incidencias y estadísticas operativas</p>
            </div>

            <div className="dashboard-stats">
                <div className="stat-card">
                    <h3>Total Incidencias</h3>
                    <div className="stat-value">{totalIncidencias}</div>
                </div>
                <div className="stat-card">
                    <h3>Abiertas</h3>
                    <div className="stat-value" style={{ color: '#EF4444' }}>{abiertas}</div>
                </div>
                <div className="stat-card">
                    <h3>En Proceso</h3>
                    <div className="stat-value" style={{ color: '#F59E0B' }}>{enProceso}</div>
                </div>
                <div className="stat-card">
                    <h3>Cerradas</h3>
                    <div className="stat-value" style={{ color: '#10B981' }}>{cerradas}</div>
                </div>
            </div>

            <div className="card">
                <h3>Incidencias por Categoría</h3>
                {incidenciasPorCategoria.map(ic => (
                    <div key={ic.nombre} style={{ display: 'flex', justifyContent: 'space-between', padding: '0.5rem 0', borderBottom: '1px solid #E2E8F0' }}>
                        <span>{ic.nombre}</span>
                        <strong>{ic.cantidad}</strong>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Dashboard;