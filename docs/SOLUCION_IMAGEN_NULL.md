# 🔧 Solución: imageUrl es NULL en el Backend

## 🔍 Diagnóstico

Los logs muestran que el backend está devolviendo `imageUrl: null` para todas las tareas:

```
🔍 TaskResponse.toDomain() - imageUrl: null
🔍 TaskResponse.toDomain() - imageUrlSnake: null
🔍 TaskResponse.toDomain() - finalImageUrl: null
```

**Esto significa:**
- ✅ El frontend está funcionando correctamente
- ✅ La deserialización JSON funciona
- ❌ El backend NO está devolviendo el campo `imageUrl`
- ❌ La columna `image_url` en la base de datos está vacía o no existe

---

## 🎯 Soluciones

### Solución 1: Verificar Migración (MÁS PROBABLE)

La columna `image_url` puede no existir en la base de datos.

#### Paso 1: Ejecutar Migración
Abre en el navegador:
```
https://miplan-production.up.railway.app/api/migrate
```

Deberías ver:
```
✅ Migraciones ejecutadas correctamente. La columna image_url ha sido agregada a la tabla tasks.
```

#### Paso 2: Verificar en Railway

1. Ve a Railway Dashboard
2. Abre tu proyecto
3. Click en "Logs"
4. Busca:
```
🔄 Ejecutando migraciones de base de datos...
✅ Migración 1: Columna image_url agregada exitosamente
```

---

### Solución 2: Crear Nueva Tarea con Imagen

Las tareas existentes no tienen imagen porque se crearon antes de la migración.

#### Paso 1: Crear Nueva Tarea
1. Abre la app
2. Click en (+) Nueva Tarea
3. Título: "Tarea con imagen nueva"
4. Click "Agregar Imagen"
5. Seleccionar Unsplash → "mountains"
6. Seleccionar imagen
7. Guardar

#### Paso 2: Verificar Logs
Busca en Logcat:
```
🔍 DEBUG - imageUrl antes de guardar: https://images.unsplash.com/...
🔍 DEBUG - Creando tarea con imageUrl: https://images.unsplash.com/...
```

#### Paso 3: Recargar Lista
1. Salir de la app (cerrar completamente)
2. Abrir de nuevo
3. Ir a "Mis Tareas"

#### Paso 4: Verificar en Logcat
Busca:
```
🔍 TaskResponse.toDomain() - imageUrl: https://images.unsplash.com/...
🔍 TaskResponse.toDomain() - finalImageUrl: https://images.unsplash.com/...
```

✅ **Si ahora tiene valor, el problema estaba en las tareas viejas**

---

### Solución 3: Verificar Base de Datos Directamente

Si las soluciones anteriores no funcionan, verifica la base de datos:

#### Opción A: Desde Railway Query

1. Ve a Railway Dashboard
2. Click en tu base de datos PostgreSQL
3. Click en "Query"
4. Ejecuta:
```sql
-- Ver estructura de la tabla
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'tasks';
```

5. Verifica que aparezca:
```
column_name  | data_type
-------------+-----------
image_url    | varchar
```

6. Si NO aparece, ejecuta:
```sql
ALTER TABLE tasks ADD COLUMN image_url VARCHAR(500);
```

#### Opción B: Ver Tareas con Imagen

```sql
-- Ver tareas que tienen imagen
SELECT id, title, image_url 
FROM tasks 
WHERE image_url IS NOT NULL;
```

Si no hay resultados, ninguna tarea tiene imagen guardada.

---

## 🚀 Plan de Acción Paso a Paso

### 1. Ejecutar Migración
```
https://miplan-production.up.railway.app/api/migrate
```

### 2. Esperar 1 minuto
Para que el backend se reinicie con la columna agregada.

### 3. Crear Nueva Tarea con Imagen
- Nueva tarea → "Test imagen final"
- Agregar imagen de Unsplash
- Guardar

### 4. Cerrar y Reabrir App
- Cerrar completamente la app
- Abrir de nuevo
- Ir a "Mis Tareas"

### 5. Verificar Logs
Buscar en Logcat:
```
🔍 TaskResponse.toDomain() - imageUrl: https://...
```

---

## 📊 Escenarios Posibles

### Escenario A: Migración No Ejecutada
**Síntoma:** Todas las tareas tienen `imageUrl: null`

**Solución:**
1. Ejecutar `/api/migrate`
2. Crear nueva tarea con imagen
3. Verificar que la nueva tarea SÍ muestra imagen

---

### Escenario B: Tareas Viejas Sin Imagen
**Síntoma:** Tareas viejas tienen `null`, tareas nuevas tienen valor

**Solución:**
- Las tareas viejas se crearon sin imagen
- Editar tareas viejas y agregar imagen
- O crear nuevas tareas

---

### Escenario C: Backend No Guarda Imagen
**Síntoma:** Incluso tareas nuevas tienen `imageUrl: null`

**Solución:**
1. Verificar logs de Railway
2. Buscar errores al guardar
3. Verificar que la columna existe en la BD

---

## 🧪 Test Rápido

### Crear Tarea de Prueba

1. **Abrir navegador** y ve a:
```
https://miplan-production.up.railway.app/api/migrate
```

2. **Espera 1 minuto**

3. **En la app:**
   - Nueva tarea → "Test final"
   - Agregar imagen Unsplash → "workspace"
   - Seleccionar primera imagen
   - Guardar

4. **Verificar en Logcat:**
```
🔍 DEBUG - imageUrl antes de guardar: https://images.unsplash.com/...
```

5. **Cerrar y reabrir app**

6. **Ir a "Mis Tareas"**

7. **Verificar en Logcat:**
```
🔍 TaskResponse.toDomain() - imageUrl: https://images.unsplash.com/...
```

8. **Verificar visualmente:**
   - ✅ Miniatura visible en lista
   - ✅ Imagen visible en detalle

---

## ✅ Checklist

- [ ] Ejecutar `/api/migrate`
- [ ] Esperar 1 minuto
- [ ] Crear nueva tarea con imagen
- [ ] Verificar log "imageUrl antes de guardar"
- [ ] Cerrar y reabrir app
- [ ] Verificar log "TaskResponse.toDomain()"
- [ ] Verificar miniatura en lista
- [ ] Verificar imagen en detalle

---

## 🎯 Resultado Esperado

Después de ejecutar la migración y crear una nueva tarea:

```
🔍 DEBUG - imageUrl antes de guardar: https://images.unsplash.com/photo-xyz
🔍 DEBUG - Creando tarea con imageUrl: https://images.unsplash.com/photo-xyz

[Después de recargar]

🔍 TaskResponse.toDomain() - imageUrl: https://images.unsplash.com/photo-xyz
🔍 TaskResponse.toDomain() - imageUrlSnake: null
🔍 TaskResponse.toDomain() - finalImageUrl: https://images.unsplash.com/photo-xyz
```

Y visualmente:
- ✅ Miniatura de 60x60dp en la lista
- ✅ Imagen de 250dp en el detalle

---

**Ejecuta el paso 1 (migración) y luego crea una nueva tarea. Avísame qué ves en los logs!** 🚀
