# Diagnóstico de Notificaciones - Checklist

## ✅ Pasos para Verificar

### 1. Verificar Permisos en el Dispositivo

**Android 13+ (API 33+):**
- [ ] Ir a Configuración del dispositivo → Aplicaciones → MiPlan → Notificaciones
- [ ] Verificar que "Permitir notificaciones" esté ACTIVADO
- [ ] Verificar que los 3 canales estén activos:
  - Tarea creada
  - Recordatorios de tareas
  - Notificaciones anticipadas

**Android 12+ (Alarmas exactas):**
- [ ] Ir a Configuración → Aplicaciones → MiPlan → Alarmas y recordatorios
- [ ] Verificar que "Permitir configurar alarmas y recordatorios" esté ACTIVADO

### 2. Verificar Configuración en la App

- [ ] Abrir la app MiPlan
- [ ] Ir a la pantalla de Configuración de Notificaciones
- [ ] Verificar que "Habilitar notificaciones" esté ACTIVADO (switch verde)
- [ ] Verificar que "Notificación al crear tarea" esté ACTIVADO
- [ ] Si no ves la pantalla de configuración, falta agregarla a la navegación

### 3. Probar Notificación Inmediata

**Código de prueba manual:**

Agrega este botón temporal en cualquier pantalla para probar:

```kotlin
Button(onClick = {
    NotificationHelper.showTaskCreatedNotification(
        context,
        taskId = 999,
        taskTitle = "Tarea de Prueba",
        dueDate = "2026-02-26",
        dueTime = "15:00"
    )
}) {
    Text("Probar Notificación")
}
```

- [ ] Si este botón muestra la notificación → El sistema funciona, el problema está en TaskViewModel
- [ ] Si NO muestra notificación → Problema de permisos o configuración del sistema

### 4. Verificar que TaskViewModel se está Usando

**Buscar dónde se crean las tareas:**
- [ ] Verificar que la pantalla de creación de tareas use `TaskViewModel.createTask()`
- [ ] NO debe llamar directamente a `taskRepository.createTask()`
- [ ] Verificar que el ViewModel se inyecte correctamente con Hilt

### 5. Logs de Depuración

Agregar logs temporales en `TaskViewModel.kt`:

```kotlin
private fun showTaskCreatedNotification(task: Task) {
    Log.d("NOTIF", "=== INICIANDO NOTIFICACIÓN ===")
    Log.d("NOTIF", "Task ID: ${task.id}")
    Log.d("NOTIF", "Task Title: ${task.title}")
    Log.d("NOTIF", "Due Date: ${task.dueDate}")
    
    val preferences = NotificationPreferences(application)
    Log.d("NOTIF", "Notifications enabled: ${preferences.notificationsEnabled}")
    Log.d("NOTIF", "Task created notif enabled: ${preferences.taskCreatedNotificationEnabled}")
    
    // ... resto del código
}
```

### 6. Verificar Optimización de Batería

- [ ] Ir a Configuración → Batería → Optimización de batería
- [ ] Buscar MiPlan
- [ ] Cambiar a "No optimizar"

### 7. Verificar Modo No Molestar

- [ ] Desactivar temporalmente el modo "No molestar" del dispositivo
- [ ] Subir el volumen del dispositivo

## 🔧 Soluciones Rápidas

### Si no ves la pantalla de configuración:
La pantalla existe pero falta agregarla a la navegación. Necesitas:
1. Agregar ruta en `NavGraph.kt`
2. Conectar el botón de notificaciones a esa pantalla

### Si los permisos no se solicitan:
La solicitud está en `NotificationSettingsScreen`, pero solo se ejecuta cuando abres esa pantalla.

### Si nada funciona:
1. Desinstalar completamente la app del dispositivo
2. Volver a instalar
3. Conceder todos los permisos cuando los solicite
4. Probar de nuevo

## 📱 Prueba Definitiva

**Crear esta función de prueba en MainActivity:**

```kotlin
// En MainActivity.kt, dentro de onCreate después de setContent
lifecycleScope.launch {
    delay(3000) // Esperar 3 segundos
    NotificationHelper.showTaskCreatedNotification(
        this@MainActivity,
        taskId = 1,
        taskTitle = "Prueba de Notificación",
        dueDate = "2026-02-26",
        dueTime = "15:00"
    )
}
```

Si después de 3 segundos de abrir la app NO ves una notificación, el problema es de permisos del sistema.
