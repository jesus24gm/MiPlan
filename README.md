# MiPlan - Aplicación de Gestión de Tareas

## 📋 Descripción

MiPlan es una aplicación móvil profesional de gestión de tareas que permite a los usuarios organizar su trabajo mediante:
- Listas de tareas con prioridades
- Tableros tipo Kanban
- Calendario mensual con visualización de tareas
- Sistema de notificaciones y recordatorios
- Gestión de usuarios con roles (Usuario y Administrador)

## 🏗️ Arquitectura

### Frontend (Android)
- **Lenguaje**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Arquitectura**: Clean Architecture + MVVM
- **Navegación**: Navigation Compose
- **DI**: Hilt
- **Networking**: Ktor Client
- **Estado**: StateFlow

### Backend
- **Framework**: Ktor
- **Lenguaje**: Kotlin
- **Autenticación**: JWT
- **Base de datos**: MySQL
- **ORM**: Exposed

### Base de Datos
- **Motor**: MySQL 8.0+
- **Entidades**: Usuario, Rol, Tablero, Tarea, Notificación

## 📁 Estructura del Proyecto

```
MiPlan/
├── android/                    # Proyecto Android
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/miplan/
│   │   │   │   ├── ui/              # Capa de presentación
│   │   │   │   ├── viewmodel/       # ViewModels
│   │   │   │   ├── data/            # Repositorios y fuentes de datos
│   │   │   │   ├── domain/          # Modelos y casos de uso
│   │   │   │   ├── di/              # Inyección de dependencias
│   │   │   │   └── MiPlanApp.kt
│   │   │   └── res/
│   │   ├── build.gradle.kts
│   │   └── proguard-rules.pro
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── gradle.properties
│
├── backend/                    # Servidor Ktor
│   ├── src/main/kotlin/com/miplan/
│   │   ├── routes/              # Endpoints REST
│   │   ├── services/            # Lógica de negocio
│   │   ├── repositories/        # Acceso a datos
│   │   ├── models/              # DTOs y entidades
│   │   ├── security/            # JWT y autenticación
│   │   ├── database/            # Configuración DB
│   │   ├── plugins/             # Plugins Ktor
│   │   └── Application.kt
│   ├── build.gradle.kts
│   └── src/main/resources/
│       └── application.conf
│
├── database/                   # Scripts SQL
│   ├── schema.sql
│   ├── seed.sql
│   └── migrations/
│
├── docs/                       # Documentación
│   ├── ARQUITECTURA.md
│   ├── API.md
│   └── GUIA_DESARROLLO.md
│
└── README.md
```

## 🚀 Inicio Rápido

### Requisitos Previos

- **Android Studio**: Hedgehog (2023.1.1) o superior
- **JDK**: 17 o superior
- **MySQL**: 8.0 o superior
- **Gradle**: 8.0+ (incluido en wrapper)

### 1. Configurar Base de Datos

```bash
# Crear base de datos
mysql -u root -p
CREATE DATABASE miplan_db;

# Ejecutar script de schema
mysql -u root -p miplan_db < database/schema.sql

# (Opcional) Cargar datos de prueba
mysql -u root -p miplan_db < database/seed.sql
```

### 2. Configurar Backend

```bash
cd backend

# Configurar variables de entorno
cp src/main/resources/application.conf.example src/main/resources/application.conf

# Editar application.conf con tus credenciales de MySQL y SMTP

# Ejecutar servidor
./gradlew run
```

El servidor estará disponible en `http://localhost:8080`

### 3. Configurar Android

```bash
# Abrir proyecto en Android Studio
# File > Open > seleccionar carpeta 'android'

# Editar android/app/src/main/java/com/miplan/data/remote/ApiConfig.kt
# Cambiar BASE_URL si es necesario (usar 10.0.2.2:8080 para emulador)

# Sincronizar Gradle
# Build > Make Project

# Ejecutar en emulador o dispositivo
# Run > Run 'app'
```

## 📱 Funcionalidades Principales

### Usuario
- ✅ Registro con confirmación por email
- ✅ Login/Logout con JWT
- ✅ CRUD de tareas (crear, leer, actualizar, eliminar)
- ✅ CRUD de tableros
- ✅ Asignar tareas a tableros
- ✅ Vista de calendario mensual
- ✅ Notificaciones y recordatorios
- ✅ Perfil de usuario

### Administrador
- ✅ Panel de administración
- ✅ Gestión de usuarios
- ✅ Asignación de roles
- ✅ Estadísticas del sistema

## 🔐 Seguridad

- **Autenticación**: JWT con expiración de 7 días
- **Contraseñas**: Hash con BCrypt
- **Validación**: Email de confirmación obligatorio
- **HTTPS**: Recomendado en producción
- **Roles**: Usuario y Administrador

## 📚 API Endpoints

### Autenticación
- `POST /api/auth/register` - Registro de usuario
- `POST /api/auth/login` - Inicio de sesión
- `GET /api/auth/verify/{token}` - Verificar email
- `POST /api/auth/logout` - Cerrar sesión

### Tareas
- `GET /api/tasks` - Listar tareas del usuario
- `POST /api/tasks` - Crear tarea
- `GET /api/tasks/{id}` - Obtener tarea
- `PUT /api/tasks/{id}` - Actualizar tarea
- `DELETE /api/tasks/{id}` - Eliminar tarea

### Tableros
- `GET /api/boards` - Listar tableros
- `POST /api/boards` - Crear tablero
- `GET /api/boards/{id}` - Obtener tablero
- `PUT /api/boards/{id}` - Actualizar tablero
- `DELETE /api/boards/{id}` - Eliminar tablero

### Notificaciones
- `GET /api/notifications` - Listar notificaciones
- `PUT /api/notifications/{id}/read` - Marcar como leída

### Admin
- `GET /api/admin/users` - Listar usuarios
- `PUT /api/admin/users/{id}/role` - Cambiar rol
- `DELETE /api/admin/users/{id}` - Eliminar usuario

Documentación completa en `docs/API.md`

## 🧪 Testing

### Backend
```bash
cd backend
./gradlew test
```

### Android
```bash
cd android
./gradlew test
./gradlew connectedAndroidTest
```

## 📦 Dependencias Principales

### Android
- Jetpack Compose 1.5.4
- Hilt 2.48
- Ktor Client 2.3.5
- Navigation Compose 2.7.5
- Coil 2.5.0

### Backend
- Ktor 2.3.5
- Exposed 0.44.1
- HikariCP 5.1.0
- BCrypt 0.10.2
- JWT 4.4.0

## 🛠️ Desarrollo

### Orden de Implementación Recomendado

1. **Semana 1**: Backend base + Autenticación
   - Configurar base de datos
   - Implementar registro y login
   - Sistema de verificación por email

2. **Semana 2**: CRUD Tareas y Tableros
   - Endpoints de tareas
   - Endpoints de tableros
   - Relaciones entre entidades

3. **Semana 3**: Frontend principal
   - Pantallas de autenticación
   - Lista de tareas
   - Formularios CRUD

4. **Semana 4**: Funcionalidades avanzadas
   - Vista calendario
   - Notificaciones
   - Panel admin
   - Testing y refinamiento

Ver guía detallada en `docs/GUIA_DESARROLLO.md`

## 📄 Licencia

Este proyecto es de código abierto para fines educativos.

## 👥 Contribución

Para contribuir al proyecto:
1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crea un Pull Request

## 📞 Soporte

Para preguntas o problemas, abre un issue en el repositorio.

---

**Versión**: 1.0.0  
**Última actualización**: Febrero 2026
