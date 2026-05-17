import React from 'react';
import Navbar from './Navbar';

const Layout = ({ children, activeTab, setActiveTab }) => {
    return (
        <div className="app-wrapper">
            <header className="site-header">
                <div className="header-container">
                    <div className="logo">Gestión de Incidencias</div>
                    <Navbar activeTab={activeTab} setActiveTab={setActiveTab} />
                </div>
            </header>
            <main className="main-content">
                {children}
            </main>
            <footer className="site-footer">
                <p>&copy; {new Date().getFullYear()} Gestión de Incidencias - Aseguradora. Todos los derechos reservados.</p>
                <p style={{ marginTop: '0.5rem', fontSize: '0.7rem' }}>Plataforma profesional de administración de siniestros</p>
            </footer>
        </div>
    );
};

export default Layout;