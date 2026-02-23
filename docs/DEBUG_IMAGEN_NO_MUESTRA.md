# 🔍 Debug: Imagen no se muestra

## Problema
La imagen se guarda correctamente en el backend pero no se muestra en:
- Lista de tareas (miniatura)
- Vista detalle
- Al editar

## Posible Causa
El backend puede estar devolviendo el campo con nombre diferente:
- `imageUrl` (camelCase) ✅
- `image_url` (snake_case) ❓

## ✅ Solución Implementada

He modificado `TaskResponse.kt` para soportar **ambos formatos**:

```kotlin
@Serializable
data class TaskResponse(
    // ... otros campos
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("image_url")
    val imageUrlSnake: String? = null,
    // ... otros campos
) {
    fun toDomain(): Task {
        // Usar el que tenga valor
        val finalImageUrl = imageUrl ?: imageUrlSnake
        
        return Task(
            // ...
            imageUrl = finalImageUrl,
            // ...
        )
    }
}
```

## 🧪 Pasos para Probar

### 1. Sincronizar y Compilar
```
File > Sync Project with Gradle Files
Build > Clean Project
Build > Rebuild Project
```

### 2. Ejecutar App
```
Run > Run 'app'
```

### 3. Abrir Logcat
```
View > Tool Windows > Logcat
```
Filtrar por: `TaskResponse`

### 4. Crear Tarea con Imagen

1. Nueva tarea → Título: "Test debug"
2. Agregar Imagen → Unsplash → "mountains"
3. Seleccionar imagen
4. Guardar

### 5. Verificar Logs en Logcat

Busca estos logs:
```
🔍 TaskResponse.toDomain() - imageUrl: [valor o null]
🔍 TaskResponse.toDomain() - imageUrlSnake: [valor o null]
🔍 TaskResponse.toDomain() - finalImageUrl: [valor final]
```

**Escenarios posibles:**

#### Escenario A: Backend usa camelCase
```
🔍 TaskResponse.toDomain() - imageUrl: https://images.unsplash.com/...
🔍 TaskResponse.toDomain() - imageUrlSnake: null
🔍 TaskResponse.toDomain() - finalImageUrl: https://images.unsplash.com/...
```
✅ **Debería funcionar ahora**

#### Escenario B: Backend usa snake_case
```
🔍 TaskResponse.toDomain() - imageUrl: null
🔍 TaskResponse.toDomain() - imageUrlSnake: https://images.unsplash.com/...
🔍 TaskResponse.toDomain() - finalImageUrl: https://images.unsplash.com/...
```
✅ **Debería funcionar ahora**

#### Escenario C: Ambos son null
```
🔍 TaskResponse.toDomain() - imageUrl: null
🔍 TaskResponse.toDomain() - imageUrlSnake: null
🔍 TaskResponse.toDomain() - finalImageUrl: null
```
❌ **El backend no está devolviendo la imagen**

---

## 🔧 Si Ambos son Null

### Paso 1: Verificar Respuesta Raw del Backend

Agrega este log en `TaskRepositoryImpl.kt`:

```kotlin
override suspend fun createTask(...): Result<Task> {
    return try {
        val request = CreateTaskRequest(...)
        val response = apiService.createTask(request)
        
        // DEBUG: Ver respuesta completa
        println("🔍 RAW Response: $response")
        
        Result.success(response.toDomain())
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

### Paso 2: Ver Respuesta en Logcat

Busca:
```
🔍 RAW Response: TaskResponse(id=123, title=..., imageUrl=..., ...)
```

**Si `imageUrl` y `image_url` son null:**
- El backend no está guardando la imagen
- Verificar logs de Railway
- Verificar migración ejecutada

**Si aparece con otro nombre:**
- Agregar ese nombre a `@SerialName`

---

## 🎯 Verificación Final

Después de compilar y ejecutar:

### 1. Crear Tarea con Imagen
- Agregar imagen de Unsplash
- Guardar

### 2. Verificar en Lista
✅ **Debería aparecer miniatura de 60x60dp**

### 3. Abrir Tarea
✅ **Debería aparecer imagen de 250dp**

### 4. Editar Tarea
✅ **Debería mostrar imagen actual**

---

## 📊 Checklist de Verificación

- [ ] Sincronizado y compilado
- [ ] App ejecutándose
- [ ] Logcat abierto y filtrado
- [ ] Tarea creada con imagen
- [ ] Logs de `TaskResponse.toDomain()` visibles
- [ ] `finalImageUrl` tiene valor
- [ ] Miniatura visible en lista
- [ ] Imagen visible en detalle

---

## 🚨 Si Sigue sin Funcionar

### Opción A: Verificar Backend

Abre en navegador:
```
https://miplan-production.up.railway.app/api/tasks
```

Busca una tarea con imagen y verifica el JSON:
```json
{
  "id": 123,
  "title": "...",
  "imageUrl": "https://...",  // ← Debe estar aquí
  // o
  "image_url": "https://...",  // ← O aquí
  ...
}
```

### Opción B: Verificar Migración

```
https://miplan-production.up.railway.app/api/migrate
```

### Opción C: Ver Logs de Railway

Railway Dashboard → Proyecto → Logs

Buscar:
```
SELECT * FROM tasks WHERE id = [id]
```

Verificar que la columna `image_url` tenga valor.

---

## 📝 Resumen de Cambios

### TaskResponse.kt
- ✅ Agregado soporte para `imageUrl` (camelCase)
- ✅ Agregado soporte para `image_url` (snake_case)
- ✅ Lógica para usar el que tenga valor
- ✅ Logs de debug para diagnosticar

### TaskListScreen.kt
- ✅ Miniatura de 60x60dp agregada
- ✅ Se muestra si `task.imageUrl` no es null

### TaskDetailScreen.kt
- ✅ Imagen de 250dp ya estaba implementada
- ✅ Se muestra si `task.imageUrl` no es null

---

**Ejecuta la app y avísame qué ves en los logs de `TaskResponse.toDomain()`** 🔍
