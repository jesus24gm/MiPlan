# Documentación de API - MiPlan

## Base URL
```
http://localhost:8080
```

## Autenticación

La API utiliza JWT (JSON Web Tokens) para autenticación. Después del login, incluye el token en el header de cada petición:

```
Authorization: Bearer <tu_token_jwt>
```

---

## Endpoints

### 🔐 Autenticación

#### POST /api/auth/register
Registra un nuevo usuario.

**Request Body:**
```json
{
  "email": "usuario@example.com",
  "password": "password123",
  "name": "Nombre Usuario"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Usuario registrado. Por favor verifica tu email.",
  "data": null
}
```

---

#### POST /api/auth/login
Inicia sesión y obtiene un token JWT.

**Request Body:**
```json
{
  "email": "usuario@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "email": "usuario@example.com",
      "name": "Nombre Usuario",
      "role": "USER",
      "isVerified": true,
      "createdAt": "2026-02-16T10:00:00"
    }
  }
}
```

---

#### GET /api/auth/verify/{token}
Verifica el email del usuario.

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Email verificado exitosamente. Ya puedes iniciar sesión.",
  "data": null
}
```

---

#### GET /api/auth/me
Obtiene información del usuario actual. **Requiere autenticación.**

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Usuario obtenido",
  "data": {
    "id": 1,
    "email": "usuario@example.com",
    "name": "Nombre Usuario",
    "role": "USER",
    "isVerified": true,
    "createdAt": "2026-02-16T10:00:00"
  }
}
```

---

#### POST /api/auth/logout
Cierra la sesión del usuario. **Requiere autenticación.**

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Sesión cerrada exitosamente",
  "data": null
}
```

---

### ✅ Tareas

#### GET /api/tasks
Obtiene todas las tareas del usuario autenticado. **Requiere autenticación.**

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Tareas obtenidas",
  "data": [
    {
      "id": 1,
      "title": "Completar proyecto",
      "description": "Finalizar el desarrollo de MiPlan",
      "status": "IN_PROGRESS",
      "priority": "HIGH",
      "dueDate": "2026-02-20T10:00:00",
      "boardId": 1,
      "boardName": "Trabajo",
      "createdBy": 1,
      "createdAt": "2026-02-16T10:00:00",
      "updatedAt": "2026-02-16T10:00:00"
    }
  ]
}
```

---

#### GET /api/tasks/{id}
Obtiene una tarea específica por ID. **Requiere autenticación.**

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Tarea obtenida",
  "data": {
    "id": 1,
    "title": "Completar proyecto",
    "description": "Finalizar el desarrollo de MiPlan",
    "status": "IN_PROGRESS",
    "priority": "HIGH",
    "dueDate": "2026-02-20T10:00:00",
    "boardId": 1,
    "boardName": "Trabajo",
    "createdBy": 1,
    "createdAt": "2026-02-16T10:00:00",
    "updatedAt": "2026-02-16T10:00:00"
  }
}
```

---

#### GET /api/tasks/board/{boardId}
Obtiene todas las tareas de un tablero específico. **Requiere autenticación.**

---

#### GET /api/tasks/status/{status}
Obtiene tareas filtradas por estado. **Requiere autenticación.**

**Valores válidos:** `PENDING`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`

---

#### GET /api/tasks/date/{date}
Obtiene tareas por fecha específica. **Requiere autenticación.**

**Formato de fecha:** `2026-02-20T00:00:00`

---

#### POST /api/tasks
Crea una nueva tarea. **Requiere autenticación.**

**Request Body:**
```json
{
  "title": "Nueva tarea",
  "description": "Descripción de la tarea",
  "priority": "MEDIUM",
  "dueDate": "2026-02-25T15:00:00",
  "boardId": 1
}
```

**Campos:**
- `title` (requerido): Título de la tarea
- `description` (opcional): Descripción detallada
- `priority` (requerido): `LOW`, `MEDIUM`, `HIGH`
- `dueDate` (opcional): Fecha límite en formato ISO
- `boardId` (opcional): ID del tablero

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Tarea creada",
  "data": {
    "id": 2,
    "title": "Nueva tarea",
    "description": "Descripción de la tarea",
    "status": "PENDING",
    "priority": "MEDIUM",
    "dueDate": "2026-02-25T15:00:00",
    "boardId": 1,
    "boardName": "Trabajo",
    "createdBy": 1,
    "createdAt": "2026-02-16T11:00:00",
    "updatedAt": "2026-02-16T11:00:00"
  }
}
```

---

#### PUT /api/tasks/{id}
Actualiza una tarea existente. **Requiere autenticación.**

**Request Body:**
```json
{
  "title": "Tarea actualizada",
  "description": "Nueva descripción",
  "status": "COMPLETED",
  "priority": "HIGH",
  "dueDate": "2026-02-26T15:00:00",
  "boardId": 2
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Tarea actualizada",
  "data": { /* tarea actualizada */ }
}
```

---

#### PATCH /api/tasks/{id}/status
Actualiza solo el estado de una tarea. **Requiere autenticación.**

**Request Body:**
```json
{
  "status": "COMPLETED"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Estado actualizado",
  "data": { /* tarea actualizada */ }
}
```

---

#### DELETE /api/tasks/{id}
Elimina una tarea. **Requiere autenticación.**

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Tarea eliminada",
  "data": null
}
```

---

### 📋 Tableros

#### GET /api/boards
Obtiene todos los tableros del usuario. **Requiere autenticación.**

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Tableros obtenidos",
  "data": [
    {
      "id": 1,
      "name": "Trabajo",
      "description": "Tareas laborales",
      "color": "#E3F2FD",
      "userId": 1,
      "createdAt": "2026-02-16T10:00:00",
      "updatedAt": "2026-02-16T10:00:00",
      "taskCount": 5
    }
  ]
}
```

---

#### GET /api/boards/{id}
Obtiene un tablero específico. **Requiere autenticación.**

---

#### POST /api/boards
Crea un nuevo tablero. **Requiere autenticación.**

**Request Body:**
```json
{
  "name": "Personal",
  "description": "Tareas personales",
  "color": "#FCE4EC"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Tablero creado",
  "data": {
    "id": 2,
    "name": "Personal",
    "description": "Tareas personales",
    "color": "#FCE4EC",
    "userId": 1,
    "createdAt": "2026-02-16T11:00:00",
    "updatedAt": "2026-02-16T11:00:00",
    "taskCount": 0
  }
}
```

---

#### PUT /api/boards/{id}
Actualiza un tablero. **Requiere autenticación.**

---

#### DELETE /api/boards/{id}
Elimina un tablero. **Requiere autenticación.**

---

### 🔔 Notificaciones

#### GET /api/notifications
Obtiene todas las notificaciones del usuario. **Requiere autenticación.**

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Notificaciones obtenidas",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "taskId": 1,
      "message": "La tarea 'Completar proyecto' vence pronto",
      "type": "TASK_REMINDER",
      "isRead": false,
      "createdAt": "2026-02-16T10:00:00"
    }
  ]
}
```

---

#### GET /api/notifications/unread
Obtiene solo las notificaciones no leídas. **Requiere autenticación.**

---

#### PUT /api/notifications/{id}/read
Marca una notificación como leída. **Requiere autenticación.**

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Notificación marcada como leída",
  "data": null
}
```

---

#### PUT /api/notifications/read-all
Marca todas las notificaciones como leídas. **Requiere autenticación.**

---

#### DELETE /api/notifications/{id}
Elimina una notificación. **Requiere autenticación.**

---

### 👤 Usuarios

#### GET /api/users/{id}
Obtiene información de un usuario. **Requiere autenticación.**

---

#### PUT /api/users/profile
Actualiza el perfil del usuario actual. **Requiere autenticación.**

**Request Body:**
```json
{
  "name": "Nuevo Nombre",
  "email": "nuevo@example.com"
}
```

---

### 👨‍💼 Administración

#### GET /api/admin/users
Obtiene todos los usuarios del sistema. **Requiere autenticación y rol ADMIN.**

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Usuarios obtenidos",
  "data": [
    {
      "id": 1,
      "email": "usuario@example.com",
      "name": "Usuario",
      "role": "USER",
      "isVerified": true,
      "createdAt": "2026-02-16T10:00:00"
    }
  ]
}
```

---

#### PUT /api/admin/users/{id}/role
Cambia el rol de un usuario. **Requiere autenticación y rol ADMIN.**

**Request Body:**
```json
{
  "role": "ADMIN"
}
```

---

#### DELETE /api/admin/users/{id}
Elimina un usuario. **Requiere autenticación y rol ADMIN.**

---

## Códigos de Estado HTTP

- **200 OK**: Petición exitosa
- **201 Created**: Recurso creado exitosamente
- **400 Bad Request**: Datos inválidos en la petición
- **401 Unauthorized**: No autenticado o token inválido
- **403 Forbidden**: No tiene permisos para esta acción
- **404 Not Found**: Recurso no encontrado
- **500 Internal Server Error**: Error del servidor

---

## Errores

Todas las respuestas de error siguen este formato:

```json
{
  "success": false,
  "message": "Descripción del error",
  "data": null
}
```

**Ejemplos de errores comunes:**

```json
{
  "success": false,
  "message": "Credenciales inválidas",
  "data": null
}
```

```json
{
  "success": false,
  "message": "El título es obligatorio",
  "data": null
}
```

```json
{
  "success": false,
  "message": "No tienes permiso para editar esta tarea",
  "data": null
}
```

---

## Colección de Postman

Para facilitar el testing, puedes importar esta colección en Postman:

```json
{
  "info": {
    "name": "MiPlan API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Auth",
      "item": [
        {
          "name": "Register",
          "request": {
            "method": "POST",
            "header": [],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"email\": \"test@example.com\",\n  \"password\": \"password123\",\n  \"name\": \"Test User\"\n}",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            },
            "url": {
              "raw": "http://localhost:8080/api/auth/register",
              "protocol": "http",
              "host": ["localhost"],
              "port": "8080",
              "path": ["api", "auth", "register"]
            }
          }
        },
        {
          "name": "Login",
          "request": {
            "method": "POST",
            "header": [],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"email\": \"admin@miplan.com\",\n  \"password\": \"admin123\"\n}",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            },
            "url": {
              "raw": "http://localhost:8080/api/auth/login",
              "protocol": "http",
              "host": ["localhost"],
              "port": "8080",
              "path": ["api", "auth", "login"]
            }
          }
        }
      ]
    }
  ]
}
```

---

**Última actualización:** Febrero 2026
