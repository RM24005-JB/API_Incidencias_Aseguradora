import React from 'react';

const Navbar = ({ activeTab, setActiveTab }) => {
    const tabs = [
        { id: 'incidencias', nombre: 'Incidencias' },
        { id: 'categorias', nombre: 'Categorías' },
        { id: 'dashboard', nombre: 'Dashboard' }
    ];
    return (
        <ul className="nav-menu">
            {tabs.map(tab => (
                <li key={tab.id}>
                    <button
                        className={`nav-link ${activeTab === tab.id ? 'active' : ''}`}
                        onClick={() => setActiveTab(tab.id)}
                    >
                        {tab.nombre}
                    </button>
                </li>
            ))}
        </ul>
    );
};

export default Navbar;