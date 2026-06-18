-- ============================================
-- Plataforma Multi-Aseguradora - Esquema de Base de Datos
-- ============================================

-- Habilitar extensión UUID si es necesario
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================
-- TABLA: usuarios
-- ============================================
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(255),
    telefono VARCHAR(50),
    direccion VARCHAR(500),
    role VARCHAR(20) DEFAULT 'USER',
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear índice en email para búsquedas más rápidas
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);

-- ============================================
-- TABLA: aseguradoras
-- ============================================
CREATE TABLE IF NOT EXISTS aseguradoras (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    nit VARCHAR(50),
    contacto_email VARCHAR(255),
    logo_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- TABLA: coberturas
-- ============================================
CREATE TABLE IF NOT EXISTS coberturas (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    descripcion TEXT
);

-- ============================================
-- TABLA: polizas
-- ============================================
CREATE TABLE IF NOT EXISTS polizas (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT REFERENCES usuarios(id) ON DELETE CASCADE,
    aseguradora_id BIGINT REFERENCES aseguradoras(id) ON DELETE SET NULL,
    numero_poliza VARCHAR(255) UNIQUE,
    tipo VARCHAR(100),
    fecha_inicio DATE,
    fecha_fin DATE,
    coberturas TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices para pólizas
CREATE INDEX IF NOT EXISTS idx_usuario_id ON polizas(usuario_id);
CREATE INDEX IF NOT EXISTS idx_numero_poliza ON polizas(numero_poliza);

-- ============================================
-- TABLA: poliza_cobertura (Tabla de unión)
-- ============================================
CREATE TABLE IF NOT EXISTS poliza_cobertura (
    poliza_id BIGINT REFERENCES polizas(id) ON DELETE CASCADE,
    cobertura_id BIGINT REFERENCES coberturas(id) ON DELETE CASCADE,
    PRIMARY KEY (poliza_id, cobertura_id)
);

-- ============================================
-- TABLA: reclamos
-- ============================================
CREATE TABLE IF NOT EXISTS reclamos (
    id BIGSERIAL PRIMARY KEY,
    poliza_id BIGINT REFERENCES polizas(id) ON DELETE CASCADE,
    fecha_siniestro TIMESTAMP,
    descripcion TEXT,
    monto_estimado DECIMAL(15, 2),
    estado VARCHAR(50) DEFAULT 'REGISTRADO',
    numero_referencia_externo VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices para reclamos
CREATE INDEX IF NOT EXISTS idx_reclamo_poliza ON reclamos(poliza_id);
CREATE INDEX IF NOT EXISTS idx_reclamo_estado ON reclamos(estado);
CREATE INDEX IF NOT EXISTS idx_reclamo_fecha ON reclamos(fecha_creacion);

-- ============================================
-- TABLA: documentos
-- ============================================
CREATE TABLE IF NOT EXISTS documentos (
    id BIGSERIAL PRIMARY KEY,
    nombre_original VARCHAR(255),
    tipo_contenido VARCHAR(100),
    ruta_archivo VARCHAR(500),
    tamano BIGINT,
    reclamo_id BIGINT REFERENCES reclamos(id) ON DELETE CASCADE,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- TABLA: reclamo_estado_historial
-- ============================================
CREATE TABLE IF NOT EXISTS reclamo_estado_historial (
    id BIGSERIAL PRIMARY KEY,
    reclamo_id BIGINT REFERENCES reclamos(id) ON DELETE CASCADE,
    estado_anterior VARCHAR(50),
    estado_nuevo VARCHAR(50),
    cambiado_por VARCHAR(255),
    fecha_cambio TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- TABLA: refresh_tokens
-- ============================================
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT UNIQUE REFERENCES usuarios(id) ON DELETE CASCADE,
    token VARCHAR(500) UNIQUE NOT NULL,
    expiry_date TIMESTAMP NOT NULL
);

-- Crear índice en token para búsquedas más rápidas
CREATE INDEX IF NOT EXISTS idx_refresh_token ON refresh_tokens(token);

-- ============================================
-- DATOS INICIALES
-- ============================================

-- Insertar usuario administrador por defecto (contraseña: admin123 - debe cambiarse en producción)
-- La contraseña está codificada con BCrypt para "admin123"
INSERT INTO usuarios (email, password, nombre, role, enabled) 
VALUES ('admin@insurances.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Admin User', 'ADMIN', TRUE)
ON CONFLICT (email) DO NOTHING;

-- Insertar usuario cliente por defecto (contraseña: cliente123)
INSERT INTO usuarios (email, password, nombre, role, enabled) 
VALUES ('cliente@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Cliente Demo', 'USER', TRUE)
ON CONFLICT (email) DO NOTHING;

-- Insertar aseguradoras de ejemplo
INSERT INTO aseguradoras (nombre, nit, contacto_email) VALUES
('Seguros Bolívar', '900123456-1', 'contacto@bolivar.com'),
('Aseguradora Solidaria', '900234567-2', 'info@solidaria.com'),
('Mapfre Colombia', '900345678-3', 'servicios@mapfre.com.co')
ON CONFLICT DO NOTHING;

-- Insertar coberturas de ejemplo
INSERT INTO coberturas (nombre, descripcion) VALUES
('Daños a terceros', 'Cubre daños causados a terceros en accidentes'),
('Robo total', 'Cubre el robo total del vehículo asegurado'),
('Asistencia vial', 'Servicio de grúa y asistencia en carretera'),
('Protección legal', 'Asesoría legal en caso de accidentes')
ON CONFLICT DO NOTHING;

-- ============================================
-- TRIGGERS PARA UPDATED_AT
-- ============================================

-- Función para actualizar el timestamp updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Crear triggers para tablas con updated_at
CREATE TRIGGER update_usuarios_updated_at BEFORE UPDATE ON usuarios
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_aseguradoras_updated_at BEFORE UPDATE ON aseguradoras
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_polizas_updated_at BEFORE UPDATE ON polizas
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_reclamos_updated_at BEFORE UPDATE ON reclamos
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
