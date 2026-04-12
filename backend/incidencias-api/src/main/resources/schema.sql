CREATE TABLE incidencias (
    id SERIAL PRIMARY KEY,
    descripcion VARCHAR(255) NOT NULL,
    estado VARCHAR(50) NOT NULL
);