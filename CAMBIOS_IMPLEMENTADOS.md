# Cambios Implementados en el Backend

## Fecha: 2 de marzo de 2026

### Resumen
Se han implementado las correcciones necesarias para resolver dos problemas críticos:
1. Error 404 en el endpoint `/api/notifications`
2. Campo `avatarUrl` faltante en la respuesta de `/api/auth/me`

---

## 1. Corrección del campo `avatarUrl` en `/api/auth/me`

### Problema
El endpoint `/api/auth/me` devolvía el usuario sin el campo `avatarUrl`, causando que el avatar desapareciera cuando el cliente recargaba el usuario.

### Archivos modificados

#### `AuthService.kt`
**Ubicación**: `backend/src/main/kotlin/com/miplan/services/AuthService.kt`

**Cambio**: Línea 126
```kotlin
// ANTES
private fun User.toUserResponse(roleName: String): UserResponse {
    return UserResponse(
        id = this.id,
        email = this.email,
        name = this.name,
        role = roleName,
        isVerified = this.isVerified,
        createdAt = this.createdAt.format(dateFormatter)
    )
}

// DESPUÉS
private fun User.toUserResponse(roleName: String): UserResponse {
    return UserResponse(
        id = this.id,
        email = this.email,
        name = this.name,
        role = roleName,
        isVerified = this.isVerified,
        avatarUrl = this.avatarUrl,  // ✅ AGREGADO
        createdAt = this.createdAt.format(dateFormatter)
    )
}
```

**Resultado**: Ahora el endpoint `/api/auth/me` devuelve correctamente el `avatarUrl` del usuario.

---

## 2. Implementación de endpoints de notificaciones

### Problema
El endpoint `/api/notifications` devolvía 404 porque no estaba implementado.

### Archivos creados

#### `NotificationRoutes.kt` (NUEVO)
**Ubicación**: `backend/src/main/kotlin/com/miplan/routes/NotificationRoutes.kt`

**Endpoints implementados**:
- `GET /api/notifications` - Obtener todas las notificaciones del usuario
- `GET /api/notifications/unread` - Obtener notificaciones no leídas
- `PUT /api/notifications/{id}/read` - Marcar notificación como leída
- `PUT /api/notifications/read-all` - Marcar todas como leídas
- `DELETE /api/notifications/{id}` - Eliminar notificación

**Características**:
- Autenticación JWT requerida
- Validación de permisos (el usuario solo puede acceder a sus propias notificaciones)
- Manejo de errores apropiado

### Archivos modificados

#### `NotificationService.kt`
**Ubicación**: `backend/src/main/kotlin/com/miplan/services/NotificationService.kt`

**Métodos agregados**:
```kotlin
suspend fun getUserNotifications(userId: Int): List<NotificationResponse>
suspend fun getUnreadNotifications(userId: Int): List<NotificationResponse>
suspend fun markAsRead(notificationId: Int, userId: Int)
suspend fun markAllAsRead(userId: Int)
suspend fun deleteNotification(notificationId: Int, userId: Int)
```

**Características**:
- Conversión de entidades a DTOs de respuesta
- Validación de permisos
- Formateo de fechas

#### `Routing.kt`
**Ubicación**: `backend/src/main/kotlin/com/miplan/plugins/Routing.kt`

**Cambios**:
- Agregado parámetro `notificationService: NotificationService`
- Registrada la función `notificationRoutes(notificationService)`

#### `Application.kt`
**Ubicación**: `backend/src/main/kotlin/com/miplan/Application.kt`

**Cambios**:
- Agregado `notificationService` al llamado de `configureRouting()`

---

## Endpoints disponibles

### Autenticación
- `POST /api/auth/register` - Registro de usuario
- `POST /api/auth/login` - Inicio de sesión
- `GET /api/auth/me` - Obtener usuario actual ✅ **AHORA INCLUYE avatarUrl**
- `POST /api/auth/logout` - Cerrar sesión

### Notificaciones (NUEVOS)
- `GET /api/notifications` - Listar todas las notificaciones
- `GET /api/notifications/unread` - Listar notificaciones no leídas
- `PUT /api/notifications/{id}/read` - Marcar como leída
- `PUT /api/notifications/read-all` - Marcar todas como leídas
- `DELETE /api/notifications/{id}` - Eliminar notificación

---

## Formato de respuesta de notificaciones

```json
{
  "success": true,
  "message": "Notificaciones obtenidas",
  "data": [
    {
      "id": 1,
      "userId": 10,
      "taskId": 75,
      "message": "Tienes una tarea pendiente: arreglar lo q falta hoy",
      "type": "TASK_REMINDER",
      "isRead": false,
      "createdAt": "2026-03-02T14:16:20"
    }
  ]
}
```

---

## Tipos de notificación soportados

- `TASK_ASSIGNED` - Tarea asignada
- `TASK_COMPLETED` - Tarea completada
- `TASK_REMINDER` - Recordatorio de tarea
- `TASK_SHARED` - Tarea compartida
- `SYSTEM` - Notificación del sistema
- `INFO` - Información general

---

## Próximos pasos

### Para desplegar los cambios:

1. **Compilar el proyecto**:
   ```bash
   cd backend
   ./gradlew build
   ```

2. **Ejecutar localmente para probar**:
   ```bash
   ./gradlew run
   ```

3. **Desplegar a Railway**:
   ```bash
   git add .
   git commit -m "feat: agregar endpoints de notificaciones y corregir avatarUrl"
   git push origin main
   ```

Railway detectará automáticamente los cambios y desplegará la nueva versión.

---

## Verificación

### Probar endpoint de notificaciones:
```bash
curl -X GET https://miplan-production.up.railway.app/api/notifications \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Probar endpoint /api/auth/me:
```bash
curl -X GET https://miplan-production.up.railway.app/api/auth/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

Deberías ver el campo `avatarUrl` en la respuesta.

---

## Estado de la implementación

✅ **Completado**: Corrección de `avatarUrl` en `/api/auth/me`  
✅ **Completado**: Implementación de endpoints de notificaciones  
✅ **Completado**: Registro de rutas en el sistema  
✅ **Completado**: Validación de permisos  
✅ **Completado**: Manejo de errores  

**Todo listo para desplegar** 🚀
