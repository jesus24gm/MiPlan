# 🚀 Ejecutar Migraciones del Sistema Kanban

## ✅ Cambios Desplegados

Se ha desplegado **Backend v2.0.0** con el sistema Kanban completo.

### Nuevas Tablas:
1. ✅ `columns` - Columnas de tableros
2. ✅ `cards` - Tarjetas dentro de columnas
3. ✅ `card_checklists` - Checklists de tarjetas
4. ✅ `checklist_items` - Items de checklists
5. ✅ `card_attachments` - Archivos adjuntos de tarjetas

### Columnas Agregadas:
- ✅ `boards.background_image_url` - Imagen de fondo del tablero

---

## 📡 Ejecutar Migraciones

### Opción 1: Automático (Recomendado)

Las migraciones se ejecutan **automáticamente** al iniciar el backend. Railway reiniciará el servicio automáticamente después del push.

**Espera 2-3 minutos** y las migraciones se ejecutarán solas.

---

### Opción 2: Manual (Endpoint)

Si quieres ejecutar las migraciones manualmente:

#### Usando cURL:
```bash
curl -X POST https://miplan-production.up.railway.app/api/migrate
```

#### Usando PowerShell:
```powershell
Invoke-WebRequest -Uri "https://miplan-production.up.railway.app/api/migrate" -Method POST
```

#### Usando Postman/Insomnia:
```
POST https://miplan-production.up.railway.app/api/migrate
```

---

## 🔍 Verificar Migraciones

### 1. Revisar Logs de Railway

1. Ve a https://railway.app
2. Abre el proyecto MiPlan
3. Click en el servicio backend
4. Ve a la pestaña "Deployments"
5. Click en el último deployment
6. Ve a "View Logs"

**Busca estos mensajes:**
```
🔄 Ejecutando migraciones de base de datos...
📝 Migración 1: Agregando columna image_url a tasks...
✅ Migración 1: Completada
📝 Migración 2: Agregando columna background_image_url a boards...
✅ Migración 2: Completada
📝 Migración 3: Creando tabla columns...
✅ Migración 3: Completada
📝 Migración 4: Creando tabla cards...
✅ Migración 4: Completada
📝 Migración 5: Creando tabla card_checklists...
✅ Migración 5: Completada
📝 Migración 6: Creando tabla checklist_items...
✅ Migración 6: Completada
📝 Migración 7: Creando tabla card_attachments...
✅ Migración 7: Completada
✅ Proceso de migraciones completado
✅ Backend v2.0.0 - Sistema Kanban: Boards, Columns, Cards, Checklists, Attachments
```

---

### 2. Verificar Tablas en la Base de Datos

Si tienes acceso a la base de datos PostgreSQL:

```sql
-- Verificar que las tablas existen
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_name IN ('columns', 'cards', 'card_checklists', 'checklist_items', 'card_attachments');

-- Verificar columna background_image_url en boards
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'boards' 
AND column_name = 'background_image_url';

-- Ver estructura de la tabla columns
\d columns

-- Ver estructura de la tabla cards
\d cards

-- Ver estructura de la tabla card_checklists
\d card_checklists

-- Ver estructura de la tabla checklist_items
\d checklist_items

-- Ver estructura de la tabla card_attachments
\d card_attachments
```

---

## 📊 Estructura de Datos Creada

### Jerarquía:
```
Board (Tablero)
  ├── background_image_url (nuevo campo)
  └── Column (Columna)
        └── Card (Tarjeta)
              ├── cover_image_url
              ├── task_id (opcional)
              ├── CardChecklist (Checklist)
              │     └── ChecklistItem (Item)
              └── CardAttachment (Archivo)
```

### Relaciones:
```
boards (1) ──→ (N) columns
columns (1) ──→ (N) cards
cards (1) ──→ (N) card_checklists
card_checklists (1) ──→ (N) checklist_items
cards (1) ──→ (N) card_attachments
cards (N) ──→ (1) tasks [opcional]
```

---

## ⚠️ Posibles Errores

### Error: "Tabla ya existe"
**Solución:** Las migraciones verifican si las tablas existen antes de crearlas. Este mensaje es normal si ya ejecutaste las migraciones antes.

### Error: "Columna ya existe"
**Solución:** Similar al anterior, las migraciones son idempotentes (se pueden ejecutar múltiples veces sin problemas).

### Error: "Foreign key constraint"
**Solución:** Las migraciones se ejecutan en orden para respetar las dependencias. Si hay un error, verifica que todas las migraciones anteriores se completaron.

---

## 🧪 Probar el Sistema

### 1. Verificar Backend
```bash
curl https://miplan-production.up.railway.app/health
```

Debería responder:
```json
{
  "status": "ok",
  "version": "2.0.0"
}
```

### 2. Verificar Tableros Existentes
```bash
curl -H "Authorization: Bearer TU_TOKEN" \
     https://miplan-production.up.railway.app/api/boards
```

---

## 📝 Siguiente Paso

Una vez que las migraciones se hayan ejecutado correctamente:

1. ✅ Verificar logs de Railway
2. ✅ Confirmar que las tablas existen
3. ⏳ Continuar con la implementación de:
   - Repositorios
   - Servicios
   - Rutas API

---

## 🔄 Rollback (Si es necesario)

Si algo sale mal, puedes revertir los cambios:

```bash
# En el directorio del backend
git revert HEAD
git push origin main
```

Esto revertirá el commit y Railway desplegará la versión anterior.

---

## ✅ Checklist de Verificación

- [ ] Push exitoso a GitHub
- [ ] Railway detectó el cambio
- [ ] Railway inició nuevo deployment
- [ ] Deployment completado exitosamente
- [ ] Logs muestran migraciones ejecutadas
- [ ] Todas las 7 migraciones completadas
- [ ] Backend v2.0.0 iniciado correctamente
- [ ] Sin errores en los logs

---

**Una vez verificado, avísame para continuar con los repositorios, servicios y rutas API!** 🚀
