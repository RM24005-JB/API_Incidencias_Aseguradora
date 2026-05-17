import React, { useState } from 'react';
import Layout from './components/common/Layout';
import IncidenciaLista from './components/incidencias/IncidenciaLista';
import CategoriaLista from './components/categorias/CategoriaLista';
import Dashboard from './components/dashboard/Dashboard';
import { incidenciasMock, categoriasMock } from './mock/mockData';

function App() {
    const [activeTab, setActiveTab] = useState('incidencias');
    const [incidencias, setIncidencias] = useState(incidenciasMock);
    const [categorias, setCategorias] = useState(categoriasMock);

    return (
        <Layout activeTab={activeTab} setActiveTab={setActiveTab}>
            {activeTab === 'incidencias' && (
                <IncidenciaLista
                    incidencias={incidencias}
                    setIncidencias={setIncidencias}
                    categorias={categorias}
                />
            )}
            {activeTab === 'categorias' && (
                <CategoriaLista
                    categorias={categorias}
                    setCategorias={setCategorias}
                />
            )}
            {activeTab === 'dashboard' && (
                <Dashboard incidencias={incidencias} categorias={categorias} />
            )}
        </Layout>
    );
}

export default App;