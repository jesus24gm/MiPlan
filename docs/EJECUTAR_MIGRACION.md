# 🔧 Ejecutar Migración Manualmente

## Problema
El backend no está ejecutando la migración automáticamente al iniciar, por lo que la columna `image_url` no existe en la base de datos.

## Solución: Ejecutar Migración Manual

He creado un endpoint temporal para ejecutar la migración manualmente.

---

## 📋 Pasos para Ejecutar

### Opción 1: Desde el Navegador (MÁS FÁCIL)

1. **Espera 2-3 minutos** a que el backend se despliegue en Railway

2. **Abre tu navegador** y ve a:
   ```
   https://miplan-production.up.railway.app/api/migrate
   ```

3. **Deberías ver:**
   ```json
   {
     "success": true,
     "message": "Migraciones ejecutadas correctamente"
   }
   ```

4. **¡Listo!** La columna `image_url` ha sido agregada

5. **Prueba la app** - Ahora debería funcionar

---

### Opción 2: Desde PowerShell

Si prefieres usar PowerShell:

```powershell
Invoke-WebRequest -Uri "https://miplan-production.up.railway.app/api/migrate" -Method GET
```

---

### Opción 3: Desde Postman/Thunder Client

1. Método: **GET**
2. URL: `https://miplan-production.up.railway.app/api/migrate`
3. Click en **Send**

---

## ✅ Verificar que Funcionó

### 1. Respuesta del Endpoint
Deberías ver:
```json
{
  "success": true,
  "message": "Migraciones ejecutadas correctamente"
}
```

### 2. Logs en Railway
Ve a Railway > tu proyecto > Logs

Busca:
```
🔧 Ejecutando migraciones manualmente...
🔄 Ejecutando migraciones de base de datos...
✅ Migración 1: Columna image_url agregada exitosamente
✅ Proceso de migraciones completado
```

### 3. Probar la App
1. Abre la app
2. Ve a "Mis Tareas"
3. ✅ No debería mostrar error SQL
4. Crea una tarea
5. ✅ Debería crearse correctamente

---

## 🐛 Si Sigue sin Funcionar

### Verificar Estado del Backend

1. Ve a: `https://miplan-production.up.railway.app/health`
2. Debería responder: `OK`

### Ver Logs de Railway

1. Ve a Railway Dashboard
2. Abre tu proyecto
3. Click en "Logs"
4. Busca errores

### Ejecutar SQL Manualmente

Si todo falla, puedes ejecutar el SQL directamente en Railway:

1. Ve a Railway Dashboard
2. Click en tu base de datos PostgreSQL
3. Click en "Query"
4. Ejecuta:
   ```sql
   ALTER TABLE tasks ADD COLUMN IF NOT EXISTS image_url VARCHAR(500);
   ```
5. Click en "Run"

---

## 📊 Timeline

1. **Ahora:** Backend desplegándose (~2 min)
2. **Después:** Ejecutar `/api/migrate` desde navegador
3. **Luego:** Probar app

---

## 🎯 Resumen Rápido

```
1. Espera 2-3 minutos
2. Abre: https://miplan-production.up.railway.app/api/migrate
3. Verifica que diga "success": true
4. Abre la app y prueba
```

---

**¡Avísame cuando hayas ejecutado el endpoint y te diré si funcionó!** 🚀
