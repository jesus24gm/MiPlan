# MiPlan Backend

Backend API para la aplicación MiPlan - Sistema de gestión de tareas.

## 🚀 Tecnologías

- **Kotlin** 1.9.20
- **Ktor** 2.3.5 - Framework web
- **Exposed** - ORM para base de datos
- **MySQL** - Base de datos
- **JWT** - Autenticación
- **BCrypt** - Encriptación de contraseñas

## 📦 Deployment en Railway

### Variables de Entorno Requeridas

```env
DATABASE_URL=jdbc:mysql://host:port/database
DATABASE_USER=root
DATABASE_PASSWORD=your-password
JWT_SECRET=your-secret-key
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
EMAIL_FROM=MiPlan <your-email@gmail.com>
BASE_URL=https://your-app.up.railway.app
PORT=8080
```

### Build

```bash
./gradlew clean build
```

### Run Locally

```bash
./gradlew run
```

## 📚 API Endpoints

### Autenticación
- `POST /api/auth/register` - Registro de usuario
- `POST /api/auth/login` - Login
- `GET /api/auth/verify/{token}` - Verificar email
- `GET /api/auth/me` - Usuario actual (requiere JWT)
- `POST /api/auth/logout` - Cerrar sesión

### Tareas
- `GET /api/tasks` - Listar tareas
- `POST /api/tasks` - Crear tarea
- `GET /api/tasks/{id}` - Ver tarea
- `PUT /api/tasks/{id}` - Actualizar tarea
- `DELETE /api/tasks/{id}` - Eliminar tarea

## 🔧 Configuración Local

Ver `application.conf` para configuración local.

## 📄 Licencia

MIT
