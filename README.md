# SITO - Sistema de Información Tecnológica Organizacional

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Architecture](https://img.shields.io/badge/architecture-microservices-green.svg)
![Status](https://img.shields.io/badge/status-in%20development-orange.svg)

## 📋 Descripción del Proyecto

SITO es un **sistema escolar integral** desarrollado utilizando una **arquitectura de microservicios**. Este proyecto forma parte de la asignatura de Arquitectura de Software y tiene como objetivo simular un sistema real de gestión educativa que permita administrar todos los aspectos académicos y administrativos de una institución educativa.

## 🏗️ Arquitectura de Microservicios

El sistema SITO está diseñado siguiendo los principios de la arquitectura de microservicios, donde cada servicio es:

- **Independiente**: Cada microservicio puede desarrollarse, desplegarse y escalarse de forma independiente
- **Especializado**: Cada servicio tiene una responsabilidad específica bien definida
- **Comunicación**: Los servicios se comunican entre sí mediante APIs REST y mensajería asíncrona
- **Resiliente**: El fallo de un servicio no compromete el funcionamiento del sistema completo

### Ventajas de esta Arquitectura

- ✅ **Escalabilidad horizontal**: Cada servicio puede escalarse según sus necesidades específicas
- ✅ **Desarrollo independiente**: Equipos pueden trabajar en paralelo en diferentes servicios
- ✅ **Tecnologías heterogéneas**: Cada servicio puede usar la tecnología más adecuada
- ✅ **Despliegue independiente**: Actualizaciones sin afectar todo el sistema
- ✅ **Tolerancia a fallos**: Aislamiento de errores entre servicios

## 🎯 Servicios a Desarrollar

El sistema SITO estará compuesto por los siguientes microservicios:

### 1. 🔐 **Servicio de Autenticación y Autorización**
- Gestión de usuarios (estudiantes, docentes, administrativos)
- Autenticación JWT
- Control de permisos y roles
- Single Sign-On (SSO)

### 2. 👥 **Servicio de Gestión de Usuarios**
- Perfiles de estudiantes
- Perfiles de docentes
- Personal administrativo
- Gestión de datos personales

### 3. 📚 **Servicio de Gestión Académica**
- Catálogo de materias/asignaturas
- Planes de estudio
- Programas académicos
- Requisitos y prerrequisitos

### 4. 📝 **Servicio de Inscripciones**
- Proceso de inscripción de estudiantes
- Asignación de materias
- Gestión de cupos
- Validación de requisitos

### 5. 📊 **Servicio de Calificaciones**
- Registro de calificaciones
- Cálculo de promedios
- Transcripciones académicas
- Reportes de rendimiento

### 6. 📅 **Servicio de Horarios**
- Programación de clases
- Asignación de aulas
- Gestión de horarios docentes
- Calendario académico

### 7. 🔔 **Servicio de Notificaciones**
- Notificaciones push
- Correos electrónicos
- SMS
- Alertas del sistema

### 8. 💰 **Servicio de Pagos**
- Gestión de colegiaturas
- Procesamiento de pagos
- Estados de cuenta
- Reportes financieros

### 9. 📋 **Servicio de Expedientes Académicos**
- Historial académico completo
- Certificados digitales
- Documentos oficiales
- Archivo documental

### 10. 📖 **Servicio de Biblioteca**
- Catálogo de recursos
- Préstamos y devoluciones
- Reservas de material
- Inventario digital

### 11. 🌐 **API Gateway**
- Punto de entrada único
- Enrutamiento de peticiones
- Balanceador de carga
- Rate limiting y seguridad

## 🛠️ Stack Tecnológico

### Backend
- **Lenguajes**: Java (Spring Boot), Node.js (Express), Python (FastAPI)
- **Bases de Datos**: PostgreSQL, MongoDB, Redis
- **Mensajería**: Apache Kafka, RabbitMQ
- **API Gateway**: Kong, Zuul, o Spring Cloud Gateway

### DevOps & Infraestructura
- **Contenedores**: Docker, Docker Compose
- **Orquestación**: Kubernetes
- **CI/CD**: GitHub Actions, Jenkins
- **Monitoreo**: Prometheus, Grafana, ELK Stack

### Frontend
- **Web**: React.js, Angular, Vue.js
- **Mobile**: React Native, Flutter

## 📁 Estructura del Proyecto

```
SITO/
├── services/
│   ├── auth-service/
│   ├── user-management-service/
│   ├── academic-service/
│   ├── enrollment-service/
│   ├── grades-service/
│   ├── schedule-service/
│   ├── notification-service/
│   ├── payment-service/
│   ├── records-service/
│   ├── library-service/
│   └── api-gateway/
├── shared/
│   ├── common-models/
│   ├── shared-configs/
│   └── utilities/
├── infrastructure/
│   ├── docker/
│   ├── kubernetes/
│   └── monitoring/
├── frontend/
│   ├── web-app/
│   └── mobile-app/
├── docs/
│   ├── api/
│   ├── architecture/
│   └── deployment/
└── scripts/
    ├── setup/
    ├── build/
    └── deploy/
```

## 🚀 Instalación y Configuración

### Prerrequisitos
- Docker y Docker Compose
- Git
- Node.js (para frontend)
- Java 11+ (para servicios Spring Boot)
- Python 3.8+ (para servicios FastAPI)

### Configuración del Entorno de Desarrollo

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/MelchorRuiz/SITO-.git
   cd SITO-
   ```

2. **Configurar variables de entorno**
   ```bash
   cp .env.example .env
   # Editar .env con las configuraciones necesarias
   ```

3. **Levantar servicios de infraestructura**
   ```bash
   docker-compose -f docker-compose.infrastructure.yml up -d
   ```

4. **Ejecutar servicios individualmente**
   ```bash
   # Para cada servicio
   cd services/[nombre-servicio]
   ./scripts/dev-start.sh
   ```

## 📖 Documentación de APIs

Cada microservicio expondrá su documentación de API usando:
- **Swagger/OpenAPI 3.0** para documentación interactiva
- **Postman Collections** para testing
- **AsyncAPI** para eventos y mensajería

### Endpoints Base
- API Gateway: `http://localhost:8080`
- Auth Service: `http://localhost:8081`
- User Management: `http://localhost:8082`
- Academic Service: `http://localhost:8083`
- [... otros servicios]

## 🔧 Desarrollo y Contribución

### Flujo de Desarrollo
1. Crear feature branch desde `develop`
2. Implementar funcionalidad con tests
3. Ejecutar linting y tests locales
4. Crear Pull Request hacia `develop`
5. Code review y merge

### Estándares de Código
- **Linting**: ESLint, Prettier para JS/TS; Checkstyle para Java
- **Testing**: Jest, JUnit, pytest según el servicio
- **Documentación**: JSDoc, Javadoc, docstrings
- **Commits**: Conventional Commits format

### Testing
```bash
# Test individual por servicio
cd services/[nombre-servicio]
npm test  # o mvn test, pytest, etc.

# Tests de integración
./scripts/integration-tests.sh

# Tests end-to-end
./scripts/e2e-tests.sh
```

## 🚀 Despliegue

### Desarrollo
```bash
docker-compose up -d
```

### Staging/Producción
```bash
kubectl apply -f infrastructure/kubernetes/
```

### Monitoreo
- **Métricas**: Prometheus + Grafana
- **Logs**: ELK Stack (Elasticsearch + Logstash + Kibana)
- **Trazas**: Jaeger para distributed tracing

## 🤝 Contribuidores

Este proyecto es desarrollado como parte de la asignatura de **Arquitectura de Software**.

### Equipo de Desarrollo
- Líder del Proyecto: [Nombre]
- Arquitecto de Software: [Nombre]
- Desarrolladores Backend: [Nombres]
- Desarrolladores Frontend: [Nombres]
- DevOps Engineer: [Nombre]

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

## 📞 Contacto y Soporte

- **Repositorio**: [GitHub](https://github.com/MelchorRuiz/SITO-)
- **Documentación**: [Wiki del Proyecto](../../wiki)
- **Issues**: [GitHub Issues](../../issues)

---

**SITO** - *Construyendo el futuro de la gestión educativa con microservicios* 🎓
