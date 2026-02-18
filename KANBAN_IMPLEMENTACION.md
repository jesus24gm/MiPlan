# 📋 Sistema Kanban - Implementación Completa

## 🎯 Objetivo
Implementar un sistema Kanban estilo Trello con tableros, columnas, tarjetas, checklists y attachments.

---

## 📊 Estructura de Datos

### Jerarquía:
```
Board (Tablero)
  └── Column (Columna)
        └── Card (Tarjeta)
              ├── CardChecklist (Checklist)
              │     └── ChecklistItem (Item de checklist)
              └── CardAttachment (Archivo adjunto)
```

### Tablas Creadas:

1. **boards** (actualizada)
   - `background_image_url` VARCHAR(500) - Imagen de fondo del tablero

2. **columns** (nueva)
   - `id`, `board_id`, `title`, `position`, `created_at`, `updated_at`

3. **cards** (nueva)
   - `id`, `column_id`, `title`, `description`, `cover_image_url`, `position`, `task_id`, `created_at`, `updated_at`

4. **card_checklists** (nueva)
   - `id`, `card_id`, `title`, `created_at`

5. **checklist_items** (nueva)
   - `id`, `checklist_id`, `title`, `is_completed`, `position`, `created_at`

6. **card_attachments** (nueva)
   - `id`, `card_id`, `file_url`, `file_name`, `file_type`, `created_at`

---

## ✅ Progreso - Fase 1: Backend

### 1. Base de Datos ✅
- [x] Actualizar `Tables.kt` con nuevas tablas
- [x] Crear migraciones en `Migrations.kt`
- [x] Agregar `background_image_url` a boards
- [x] Crear tabla `columns`
- [x] Crear tabla `cards`
- [x] Crear tabla `card_checklists`
- [x] Crear tabla `checklist_items`
- [x] Crear tabla `card_attachments`

### 2. Modelos ✅
- [x] Crear `KanbanModels.kt` con modelos de dominio
- [x] Crear `KanbanRequests.kt` con DTOs de request
- [x] Crear `KanbanResponses.kt` con DTOs de response

### 3. Repositorios ⏳
- [ ] `BoardRepository.kt` - CRUD de tableros
- [ ] `ColumnRepository.kt` - CRUD de columnas
- [ ] `CardRepository.kt` - CRUD de tarjetas
- [ ] `ChecklistRepository.kt` - CRUD de checklists
- [ ] `AttachmentRepository.kt` - CRUD de attachments

### 4. Servicios ⏳
- [ ] `BoardService.kt` - Lógica de negocio de tableros
- [ ] `ColumnService.kt` - Lógica de negocio de columnas
- [ ] `CardService.kt` - Lógica de negocio de tarjetas
- [ ] `ChecklistService.kt` - Lógica de negocio de checklists
- [ ] `AttachmentService.kt` - Lógica de negocio de attachments

### 5. Rutas API ⏳
- [ ] `BoardRoutes.kt` - Endpoints de tableros
- [ ] `ColumnRoutes.kt` - Endpoints de columnas
- [ ] `CardRoutes.kt` - Endpoints de tarjetas
- [ ] `ChecklistRoutes.kt` - Endpoints de checklists
- [ ] `AttachmentRoutes.kt` - Endpoints de attachments

---

## 📡 API Endpoints (Planificados)

### Boards
```
GET    /api/boards              - Listar tableros del usuario
GET    /api/boards/{id}         - Obtener tablero con columnas y tarjetas
POST   /api/boards              - Crear tablero
PUT    /api/boards/{id}         - Actualizar tablero
DELETE /api/boards/{id}         - Eliminar tablero
```

### Columns
```
GET    /api/columns/{id}        - Obtener columna
POST   /api/columns             - Crear columna
PUT    /api/columns/{id}        - Actualizar columna
DELETE /api/columns/{id}        - Eliminar columna
PUT    /api/columns/{id}/move   - Mover columna (cambiar posición)
```

### Cards
```
GET    /api/cards/{id}          - Obtener tarjeta con detalles
POST   /api/cards               - Crear tarjeta
PUT    /api/cards/{id}          - Actualizar tarjeta
DELETE /api/cards/{id}          - Eliminar tarjeta
PUT    /api/cards/{id}/move     - Mover tarjeta a otra columna
POST   /api/cards/{id}/copy     - Copiar tarjeta
```

### Checklists
```
GET    /api/checklists/{id}     - Obtener checklist con items
POST   /api/checklists          - Crear checklist
PUT    /api/checklists/{id}     - Actualizar checklist
DELETE /api/checklists/{id}     - Eliminar checklist
```

### Checklist Items
```
POST   /api/checklist-items     - Crear item
PUT    /api/checklist-items/{id} - Actualizar item (toggle completed)
DELETE /api/checklist-items/{id} - Eliminar item
```

### Attachments
```
POST   /api/attachments         - Agregar archivo
DELETE /api/attachments/{id}    - Eliminar archivo
```

---

## 🎨 Funcionalidades

### Básicas (Fase 2-3)
- ✅ Crear/editar/eliminar tableros
- ✅ Crear/editar/eliminar columnas
- ✅ Crear/editar/eliminar tarjetas
- ✅ Ver tablero con todas sus columnas y tarjetas
- ✅ Imagen de fondo del tablero
- ✅ Imagen de cabecera de tarjeta

### Avanzadas (Fase 4)
- ✅ Checklists con items
- ✅ Barra de progreso de checklist
- ✅ Múltiples attachments por tarjeta
- ✅ Asociar tarea existente a tarjeta
- ✅ Crear tarea desde tarjeta
- ✅ Mover tarjeta entre columnas
- ✅ Copiar tarjeta a otro tablero
- ⏳ Drag & drop (nativo Android)

---

## 🔄 Migraciones

### Ejecutar Migraciones:
```bash
# Opción 1: Automático al iniciar el backend
# Las migraciones se ejecutan automáticamente en Application.kt

# Opción 2: Endpoint manual
POST https://miplan-production.up.railway.app/api/migrate
```

### Migraciones Incluidas:
1. ✅ Agregar `image_url` a `tasks`
2. ✅ Agregar `background_image_url` a `boards`
3. ✅ Crear tabla `columns`
4. ✅ Crear tabla `cards`
5. ✅ Crear tabla `card_checklists`
6. ✅ Crear tabla `checklist_items`
7. ✅ Crear tabla `card_attachments`

---

## 📝 Próximos Pasos

### Inmediato:
1. Crear repositorios para todas las entidades
2. Crear servicios con lógica de negocio
3. Crear rutas API
4. Probar endpoints con Postman/Insomnia
5. Desplegar backend actualizado

### Después:
6. Implementar frontend Android (Fase 2)
7. Pantallas de lista y creación de tableros
8. Vista de tablero con columnas y tarjetas
9. Funcionalidades avanzadas

---

## 🚀 Estado Actual

**Fase 1: Backend - 40% Completado**

✅ Completado:
- Esquema de base de datos
- Migraciones
- Modelos de dominio
- DTOs (Request/Response)

⏳ En Progreso:
- Repositorios
- Servicios
- Rutas API

---

## 📦 Archivos Creados/Modificados

### Backend:
```
backend/src/main/kotlin/com/miplan/
├── database/
│   ├── Tables.kt (modificado)
│   └── Migrations.kt (modificado)
├── models/
│   ├── KanbanModels.kt (nuevo)
│   ├── requests/
│   │   └── KanbanRequests.kt (nuevo)
│   └── responses/
│       └── KanbanResponses.kt (nuevo)
├── repositories/ (pendiente)
├── services/ (pendiente)
└── routes/ (pendiente)
```

---

**Continuando con la implementación...**
