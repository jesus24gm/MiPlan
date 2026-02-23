# 🧪 Guía de Pruebas - Sistema de Imágenes

## ✅ Cambios Implementados

### 1. Miniatura en Lista de Tareas ⭐ NUEVO
- Muestra imagen de 60x60dp en cada tarea
- Aparece entre el checkbox y el título
- Solo si la tarea tiene imagen

### 2. Imagen en Vista Detalle ✅ YA FUNCIONABA
- Muestra imagen de 250dp de altura
- Aparece después del título

### 3. Logs de Debug 🔍 NUEVO
- Agregados logs para verificar si imageUrl tiene valor
- Ayudan a diagnosticar problemas de guardado

---

## 🚀 Pasos para Probar

### 1. Sincronizar y Compilar
```
File > Sync Project with Gradle Files
Build > Clean Project
Build > Rebuild Project
Run > Run 'app'
```

### 2. Abrir Logcat
En Android Studio:
```
View > Tool Windows > Logcat
```

Filtrar por: `DEBUG`

---

## 📋 Escenarios de Prueba

### Escenario A: Crear Tarea con Imagen de Unsplash

1. **Abrir app** → Click en (+) Nueva Tarea
2. **Título:** "Tarea con imagen Unsplash"
3. **Click:** "Agregar Imagen"
4. **Seleccionar:** "Unsplash"
5. **Buscar:** "mountains"
6. **Seleccionar** una imagen
7. ✅ **Verificar:** Vista previa se muestra
8. **Click:** ✓ (Guardar)

**Verificar en Logcat:**
```
🔍 DEBUG - imageUrl antes de guardar: https://images.unsplash.com/...
🔍 DEBUG - Creando tarea con imageUrl: https://images.unsplash.com/...
```

9. **Ir a:** "Mis Tareas"
10. ✅ **Verificar:** Miniatura visible en la lista
11. **Click** en la tarea
12. ✅ **Verificar:** Imagen grande visible en detalle

---

### Escenario B: Crear Tarea con Imagen de Galería

1. **Nueva tarea** → Título: "Foto de galería"
2. **Click:** "Agregar Imagen" → "Galería"
3. **Seleccionar** foto del dispositivo
4. ✅ **Verificar:** "Subiendo..." aparece
5. ✅ **Verificar:** Vista previa después de subir
6. **Guardar**

**Verificar en Logcat:**
```
✅ Imagen subida: https://res.cloudinary.com/...
🔍 DEBUG - imageUrl antes de guardar: https://res.cloudinary.com/...
🔍 DEBUG - Creando tarea con imageUrl: https://res.cloudinary.com/...
```

7. ✅ **Verificar:** Miniatura en lista
8. ✅ **Verificar:** Imagen en detalle

---

### Escenario C: Crear Tarea con Foto de Cámara

1. **Nueva tarea** → Título: "Foto de cámara"
2. **Click:** "Agregar Imagen" → "Cámara"
3. **Permitir** permiso (si se solicita)
4. **Tomar** foto
5. ✅ **Verificar:** Subida automática
6. **Guardar**

**Verificar en Logcat:**
```
✅ Imagen subida: https://res.cloudinary.com/...
🔍 DEBUG - imageUrl antes de guardar: https://res.cloudinary.com/...
```

7. ✅ **Verificar:** Miniatura y detalle

---

### Escenario D: Editar Imagen de Tarea

1. **Abrir** tarea con imagen
2. **Click:** "Editar Tarea"
3. ✅ **Verificar:** Imagen actual se muestra
4. **Click:** "Cambiar"
5. **Seleccionar** nueva fuente y imagen
6. **Guardar**

**Verificar en Logcat:**
```
🔍 DEBUG - Actualizando tarea con imageUrl: [nueva URL]
```

7. ✅ **Verificar:** Miniatura actualizada
8. ✅ **Verificar:** Imagen actualizada en detalle

---

### Escenario E: Quitar Imagen

1. **Editar** tarea con imagen
2. **Click:** "Quitar"
3. ✅ **Verificar:** Vista previa desaparece
4. **Guardar**

**Verificar en Logcat:**
```
🔍 DEBUG - imageUrl antes de guardar: null
```

5. ✅ **Verificar:** Sin miniatura en lista
6. ✅ **Verificar:** Sin imagen en detalle

---

## 🐛 Diagnóstico de Problemas

### Problema: "La URL no se guarda"

#### Paso 1: Verificar Logs
Busca en Logcat:
```
🔍 DEBUG - imageUrl antes de guardar: [valor]
```

**Si es `null`:**
- La imagen no se seleccionó correctamente
- Problema en la subida a Cloudinary
- Problema en la selección de Unsplash

**Si tiene valor:**
- El problema está en el backend
- Verificar logs de Railway

#### Paso 2: Verificar Respuesta del Backend
Busca en Logcat:
```
Response: {"id":123,"title":"...","imageUrl":"..."}
```

**Si `imageUrl` es `null` en la respuesta:**
- El backend no está guardando la URL
- Verificar migración ejecutada
- Verificar logs de Railway

#### Paso 3: Verificar Migración
Abre en navegador:
```
https://miplan-production.up.railway.app/api/migrate
```

Debería decir:
```
✅ Migraciones ejecutadas correctamente...
```

---

## 📊 Checklist de Verificación

### Funcionalidades Básicas
- [ ] Seleccionar imagen de galería
- [ ] Tomar foto con cámara
- [ ] Buscar en Unsplash
- [ ] Vista previa en formulario
- [ ] Botones "Cambiar" y "Quitar" funcionan

### Guardado
- [ ] Log muestra imageUrl con valor
- [ ] Tarea se crea sin errores
- [ ] Tarea se actualiza sin errores

### Visualización
- [ ] Miniatura aparece en lista (60x60dp)
- [ ] Imagen aparece en detalle (250dp altura)
- [ ] Imágenes se cargan correctamente
- [ ] Sin errores de carga

### Edición
- [ ] Cambiar imagen funciona
- [ ] Quitar imagen funciona
- [ ] Cambios se reflejan en lista
- [ ] Cambios se reflejan en detalle

---

## 🔍 Logs Importantes

### Logs de Éxito
```
✅ Imagen subida: https://res.cloudinary.com/...
🔍 DEBUG - imageUrl antes de guardar: https://...
🔍 DEBUG - Creando tarea con imageUrl: https://...
```

### Logs de Error
```
❌ Error subiendo imagen: [mensaje]
Error: [descripción del problema]
```

---

## 📱 Capturas Esperadas

### Lista de Tareas (Con Imagen)
```
┌─────────────────────────────────┐
│ [✓] [🖼️] Comprar materiales    │
│          Para el proyecto...    │
│          🏳️ Media | 📅 18/02   │
├─────────────────────────────────┤
│ [✓] [🖼️] Reunión equipo        │
│          Discutir avances       │
│          🏳️ Alta | 📅 19/02    │
└─────────────────────────────────┘
```

### Vista Detalle (Con Imagen)
```
┌─────────────────────────────────┐
│ Título                          │
│ Comprar materiales              │
├─────────────────────────────────┤
│ Imagen                          │
│ ┌─────────────────────────┐    │
│ │                         │    │
│ │   [Imagen 250dp]        │    │
│ │                         │    │
│ └─────────────────────────┘    │
├─────────────────────────────────┤
│ Descripción                     │
│ Para el proyecto de...          │
└─────────────────────────────────┘
```

---

## ✅ Resultado Esperado

Después de todas las pruebas:

1. ✅ Todas las tareas con imagen muestran miniatura
2. ✅ Al abrir tarea, imagen se ve en detalle
3. ✅ Logs muestran imageUrl con valor
4. ✅ Sin errores en Logcat
5. ✅ Imágenes persisten después de cerrar app

---

## 🚨 Si Algo Falla

### 1. Verificar Migración
```
https://miplan-production.up.railway.app/api/migrate
```

### 2. Ver Logs de Railway
- Railway Dashboard → Proyecto → Logs
- Buscar errores relacionados con `image_url`

### 3. Limpiar y Reconstruir
```
Build > Clean Project
Build > Rebuild Project
File > Invalidate Caches / Restart
```

### 4. Reportar Problema
Incluye:
- Logs de Logcat (filtro: DEBUG y ERROR)
- Captura de pantalla del error
- Pasos para reproducir

---

**¡Prueba todas las funcionalidades y avísame cómo va!** 🚀📸
