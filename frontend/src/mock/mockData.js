export const categoriasMock = [
    { id: 1, nombre: "Gastos Médicos", descripcion: "Reembolsos y gastos de salud" },
    { id: 2, nombre: "Seguro de Autos", descripcion: "Siniestros y daños vehiculares" },
    { id: 3, nombre: "Seguro de Daños", descripcion: "Daños a la propiedad" },
    { id: 4, nombre: "Seguro de Vida", descripcion: "Siniestros de vida" }
];

export const incidenciasMock = [
    { id: 1, descripcion: "Fractura de brazo por accidente doméstico", estado: "ABIERTA", categoriaId: 1 },
    { id: 2, descripcion: "Choque en autopista, daños al parachoques", estado: "EN_PROCESO", categoriaId: 2 },
    { id: 3, descripcion: "Incendio en almacén", estado: "CERRADA", categoriaId: 3 },
    { id: 4, descripcion: "Fallecimiento del asegurado", estado: "ABIERTA", categoriaId: 4 }
];