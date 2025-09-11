# SITO+

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

## 📁 Estructura del Proyecto

```
frontend/
services/
├── authentication/
├── students/
├── teachers/
├── school-services/
├── human-resources/
docker-compose.yml
README.md
```

## 🤝 Contribuidores

Este proyecto es desarrollado como parte de la asignatura de **Arquitectura de Software**.

### Equipo de Desarrollo
- Melchor Ruiz
- Miriam Conchas
- Paulina
- Sandro Ramirez

