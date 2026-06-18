# INCIDENCIAS DE ASEGURADORAS

## Descripción del Proyecto

Sistema de gestión de incidencias de aseguradoras que permite a usuarios y administradores gestionar pólizas de seguros, reclamos de siniestros, y aseguradoras de manera centralizada. El sistema proporciona una plataforma web completa con autenticación segura, gestión de documentos, y estadísticas en tiempo real.

### Funciones Principales

- **Gestión de Usuarios**: Registro y autenticación con JWT + Refresh Token, roles (ADMIN/USER)
- **Gestión de Pólizas**: Creación, consulta y filtrado de pólizas por usuario y aseguradora
- **Gestión de Reclamos**: Creación de reclamos con documentos adjuntos, seguimiento de estados
- **Panel de Administración**: Gestión de usuarios, aseguradoras y estados de reclamos
- **Dashboard**: Estadísticas en tiempo real, gráficos de reclamos por mes y estado
- **Multi-idioma**: Soporte para español e inglés
- **Documentación**: API REST documentada con Swagger/OpenAPI

### Tecnologías Utilizadas

**Backend:**
- Spring Boot 3.x (Java 17)
- Arquitectura N-Capas (Controladores, Servicios, Repositorios, Entidades)
- Patrón DTO para transferencia de datos
- Spring Security con JWT
- JPA/Hibernate para persistencia
- PostgreSQL como base de datos
- Swagger/OpenAPI para documentación

**Frontend:**
- React 18 con JavaScript
- SPA (Single Page Application)
- Axios para consumo de API REST
- React Router para navegación
- React Query para gestión de estado
- TailwindCSS para estilos responsive
- i18next para internacionalización
- Recharts para gráficos

**Despliegue:**
- Docker y Docker Compose
- Nginx para servidor web
- PostgreSQL en contenedor

## Diagrama Entidad-Relación


┌─────────────┐       ┌──────────────┐       ┌─────────────┐
│   USUARIO   │       │    POLIZA    │       │ ASEGURADORA │
├─────────────┤       ├──────────────┤       ├─────────────┤
│ id (PK)     │◄──────│ id (PK)      │◄──────│ id (PK)     │
│ email       │  1:N  │ usuario_id   │  N:1  │ nombre      │
│ password    │       │ aseguradora_ │       │ nit         │
│ nombre      │       │     _id      │       │ contacto_   │
│ telefono    │       │ numero_poliza│       │    email    │
│ direccion   │       │ tipo         │       │ logo_url    │
│ role        │       │ fecha_inicio │       └─────────────┘
│ enabled     │       │ fecha_fin    │
│ created_at  │       │ coberturas   │
│ updated_at  │       │ created_at   │
└─────────────┘       │ updated_at   │
                      └──────────────┘
                              │
                              │ 1:N
                              ▼
                      ┌──────────────┐
                      │    RECLAMO   │
                      ├──────────────┤
                      │ id (PK)      │
                      │ poliza_id    │
                      │ fecha_       │
                      │   siniestro  │
                      │ descripcion  │
                      │ monto_       │
                      │   estimado   │
                      │ estado       │
                      │ fecha_       │
                      │   creacion   │
                      │ fecha_       │
                      │   actualiz.  │
                      └──────────────┘
                              │
                              │ 1:N
                              ▼
                      ┌──────────────┐       ┌──────────────┐
                      │  DOCUMENTO   │       │  HISTORIAL   │
                      ├──────────────┤       ├──────────────┤
                      │ id (PK)      │       │ id (PK)      │
                      │ reclamo_id   │       │ reclamo_id   │
                      │ nombre_      │       │ estado_      │
                      │   archivo    │       │   anterior   │
                      │ tipo_        │       │ estado_      │
                      │   archivo    │       │   nuevo      │
                      │ ruta_        │       │ fecha_       │
                      │   archivo    │       │   cambio     │
                      │ created_at   │       │ usuario_id   │
                      └──────────────┘       └──────────────┘

┌─────────────┐
│ REFRESH_    │
│   TOKEN     │
├─────────────┤
│ id (PK)     │
│ token       │
│ usuario_id  │◄──┐
│ expiry_date │   │
└─────────────┘   │
                  │
                  │ 1:N
                  │
            ┌─────┴────── ┐
            │   USUARIO   │
            └─────────────┘


**Relaciones:**
- Usuario ↔ Poliza: 1:N (Un usuario tiene múltiples pólizas)
- Aseguradora ↔ Poliza: 1:N (Una aseguradora tiene múltiples pólizas)
- Poliza ↔ Reclamo: 1:N (Una póliza tiene múltiples reclamos)
- Reclamo ↔ Documento: 1:N (Un reclamo tiene múltiples documentos)
- Reclamo ↔ Historial: 1:N (Un reclamo tiene múltiples cambios de estado)
- Usuario ↔ RefreshToken: 1:N (Un usuario tiene múltiples refresh tokens)

## Manual de Despliegue

### Requisitos Previos

- Docker 20.10+
- Docker Compose 2.0+
- 4GB RAM mínimo
- 10GB espacio en disco

### Pasos de Despliegue

1. **Clonar el repositorio:**
   ```bash
   git clone <repositorio-url>
   cd API_Incidencias_Aseguradora
   ```

2. **Levantar el sistema con Docker Compose:**
   ```bash
   docker-compose up --build
   ```

   Este comando:
   - Construye las imágenes Docker del frontend y backend
   - Inicia el contenedor de PostgreSQL
   - Configura las redes y volúmenes
   - Aplica las migraciones de base de datos automáticamente
   - Inicializa datos de prueba

3. **Acceder a la aplicación:**
   - **Frontend**: http://localhost
   - **Backend API**: http://localhost:8080
   - **Documentación Swagger**: http://localhost:8080/swagger-ui.html
   - **Health Check**: http://localhost:8080/actuator/health

4. **Detener el sistema:**
   ```bash
   docker-compose down
   ```

5. **Limpiar todo (incluyendo volúmenes):**
   ```bash
   docker-compose down -v
   ```

### Credenciales de Prueba

**Administrador:**
- Email: admin@insurances.com
- Password: admin123

**Clientes de prueba:**
- Cliente A: clienteA@example.com / clienteA123
- Cliente B: clienteB@example.com / clienteB123
- Cliente C: clienteC@example.com / clienteC123

### Estructura de Contenedores

```
┌─────────────────────────────────────────────────┐
│              Docker Network                     │
│  ┌──────────────┐  ┌──────────────┐             │
│  │   Frontend   │  │   Backend    │             │
│  │   (Nginx)    │  │  (Spring)    │             │
│  │   Port: 80   │  │  Port: 8080  │             │
│  └──────┬───────┘  └──────┬───────┘             │
│         │                  │                    │
│         └────────┬─────────┘                    │
│                  │                              │
│         ┌────────▼─────────┐                    │
│         │  PostgreSQL DB   │                    │
│         │   Port: 5432     │                    │
│         └──────────────────┘                    │
└─────────────────────────────────────────────────┘
```

## Evidencias de Funcionamiento

### Documentación Swagger

La API REST está completamente documentada con Swagger/OpenAPI. Acceda a:
http://localhost:8080/swagger-ui.html

**Características de la API:**
- Verbos HTTP correctos (GET, POST, PUT, DELETE)
- Códigos de estado HTTP apropiados (200, 201, 400, 401, 403, 404, 500)
- Autenticación JWT con Bearer tokens
- Validación de datos con Jakarta Validation
- Manejo de errores con mensajes descriptivos

### Tabla de Endpoints del Backend

| Método | Endpoint                        | Descripción            | Autenticación | Rol        |
|--------|---------------------------------|------------------------|---------------|------------|
| POST   | /api/auth/register              | Registro de usuario    | Pública       | -          |
| POST   | /api/auth/login                 | Inicio de sesión       | Pública       | -          |
| POST   | /api/auth/refresh               | Renovar token          | Pública       | -          |
| GET    | /api/auth/me                    | Obtener perfil usuario | JWT           | USER/ADMIN |
| POST   | /api/auth/logout                | Cerrar sesión          | JWT           | USER/ADMIN |
| GET    | /api/aseguradoras               | Listar aseguradoras    | Pública       | -          |
| POST   | /api/aseguradoras               | Crear aseguradora      | JWT           | ADMIN      |
| GET    | /api/polizas                    | Listar pólizas usuario | JWT           | USER/ADMIN |
| POST   | /api/polizas                    | Crear póliza           | JWT           | USER       |
| GET    | /api/polizas/{id}               | Detalle de póliza      | JWT           | USER/ADMIN |
| PUT    | /api/polizas/{id}               | Actualizar póliza      | JWT           | ADMIN      |
| DELETE | /api/polizas/{id}               | Eliminar póliza        | JWT           | ADMIN      |
| GET    | /api/reclamos                   | Listar reclamos usuario| JWT           | USER/ADMIN |
| POST   | /api/reclamos                   | Crear reclamo          | JWT           | USER       |
| GET    | /api/reclamos/{id}              | Detalle de reclamo     | JWT           | USER/ADMIN |
| PUT    | /api/reclamos/{id}              | Actualizar reclamo     | JWT           | USER       |
| POST   | /api/upload/reclamo/{id}        | Subir documento        | JWT           | USER/ADMIN |
| GET    | /api/upload/download/{id}       | Descargar documento    | JWT           | USER/ADMIN |
| GET    | /api/admin/reclamos             | Listar todos reclamos  | JWT           | ADMIN      |
| PUT    | /api/admin/reclamos/{id}/estado | Cambiar estado reclamo | JWT           | ADMIN      |
| GET    | /api/admin/polizas              | Listar todas pólizas   | JWT           | ADMIN      |
| GET    | /api/admin/usuarios             | Listar usuarios        | JWT           | ADMIN      |
| PUT    | /api/admin/usuarios/{id}/role   | Cambiar rol usuario    | JWT           | ADMIN      |
| GET    | /api/dashboard/stats            | Estadísticas dashboard | JWT           | USER/ADMIN |

### Vistas de la Aplicación

**1. Login/Registro:**
- Formulario de inicio de sesión con validación
- Registro de nuevos usuarios
- Manejo de errores con mensajes específicos

**2. Dashboard:**
- Estadísticas en tiempo real
- Gráfico de reclamos por mes
- Gráfico circular de estados de reclamos
- Tabla de reclamos recientes (admin)

**3. Pólizas:**
- Lista de pólizas del usuario
- Filtro por aseguradora
- Creación de nuevas pólizas
- Detalle de póliza con términos y condiciones

**4. Reclamos:**
- Lista de reclamos con filtros
- Creación de nuevos reclamos
- Subida de documentos
- Seguimiento de estados
- Historial de cambios

**5. Aseguradoras:**
- Lista de aseguradoras disponibles
- Información de contacto
- Gestión (admin)

**6. Panel de Administración:**
- Gestión de todos los reclamos
- Cambio de estados de reclamos
- Gestión de usuarios y roles
- Gestión de aseguradoras

### Características Técnicas Implementadas

**Backend (Spring Boot 3.x):**
- Arquitectura N-Capas (Controladores, Servicios, Repositorios, Entidades)
- Patrón DTO para transferencia de datos
- JPA/Hibernate con relaciones (1:N, N:M)
- Spring Security con JWT
- Refresh Token para renovación de sesiones
- Validación con Jakarta Validation
- Manejo de excepciones personalizado
- Documentación Swagger/OpenAPI
- Health checks con Spring Actuator

**Base de Datos (PostgreSQL):**
- Relaciones 1:N y N:M mapeadas correctamente
- Índices para optimización de consultas
- Auditoría con created_at/updated_at
- Datos de prueba inicializados automáticamente

**Frontend (React):**
- SPA con React Router
- Consumo de API con Axios
- Gestión de estado con React Query
- Responsive design con TailwindCSS
- Internacionalización (i18next)
- Gráficos con Recharts
- Manejo de errores específicos
- Rutas protegidas por roles

**Despliegue (Docker):**
- Dockerfiles para frontend y backend
- Docker Compose para orquestación
- Volúmenes para persistencia de datos
- Redes Docker para comunicación
- Health checks para monitoreo

### Datos de Prueba

El sistema se inicializa con:
- 1 usuario administrador
- 3 usuarios clientes con pólizas
- 3 aseguradoras
- 15+ reclamos distribuidos en diferentes meses
- Estados variados de reclamos (REGISTRADO, EN_VALIDACION, APROBADO, PAGADO, RECHAZADO)

## Integrantes del equipo

Juan Antonio Artiga Jovel - AJ23001
Jacqueline Beatriz Renderos Martínez - RM24005
Rolando Duran Colocho - DC23003
Armando Enrique Garcia Mendez - GM21015
Oscar Aníbal Gomez Luna - GL10025


## Licencia

Proyecto desarrollado para fines educativos y de evaluación académica.