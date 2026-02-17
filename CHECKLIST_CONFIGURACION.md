# ✅ Checklist de Configuración - MiPlan

## 📦 Proyecto Generado

- ✅ Documentación completa
- ✅ Estructura de carpetas
- ✅ Código base Android
- ✅ Código base Backend
- ✅ Scripts SQL

## 🗄️ Base de Datos

### Opción 1: XAMPP (Recomendado)
- [ ] Descargar XAMPP
- [ ] Instalar XAMPP
- [ ] Iniciar MySQL desde XAMPP Control Panel
- [ ] Importar `database/schema.sql` en phpMyAdmin
- [ ] Verificar que existe `miplan_db`
- [ ] Verificar usuario admin

### Opción 2: MySQL Directo
- [ ] Instalar MySQL
- [ ] Configurar contraseña root
- [ ] Ejecutar `database/schema.sql`
- [ ] Verificar base de datos

### Verificación
```powershell
# Con XAMPP
C:\xampp\mysql\bin\mysql.exe -u root miplan_db -e "SHOW TABLES;"

# Con MySQL
mysql -u root -p miplan_db -e "SHOW TABLES;"
```

**Resultado esperado:**
```
+---------------------+
| Tables_in_miplan_db |
+---------------------+
| boards              |
| notifications       |
| roles               |
| tasks               |
| user_tasks          |
| users               |
+---------------------+
```

## 🖥️ Backend (Ktor)

### Configuración
- [x] Archivos creados
- [ ] Editar `backend/src/main/resources/application.conf`
  - [ ] Configurar `database.user`
  - [ ] Configurar `database.password`
  - [ ] Configurar `email.username` (opcional)
  - [ ] Configurar `email.password` (opcional)

### Iniciar Backend
```powershell
cd backend
.\gradlew run
```

### Verificación
```powershell
# Verificar que el servidor está corriendo
curl http://localhost:8080/health
# Respuesta esperada: OK
```

**Endpoints disponibles:**
- `GET /health` - Estado del servidor
- `POST /api/auth/register` - Registro
- `POST /api/auth/login` - Login
- `GET /api/tasks` - Listar tareas (requiere auth)

## 📱 Android (Jetpack Compose)

### Configuración Automática
- [x] `gradle-wrapper.jar` descargado
- [x] `gradle-wrapper.properties` configurado
- [x] `local.properties` creado
- [x] SDK detectado

### Abrir en Android Studio
- [ ] File > Open
- [ ] Seleccionar carpeta `android`
- [ ] Esperar sincronización de Gradle (5-10 min)
- [ ] Verificar que no hay errores

### Configurar Dispositivo
- [ ] **Emulador:** Tools > Device Manager > Create Device
  - [ ] Dispositivo: Pixel 5
  - [ ] Sistema: Android 14 (API 34)
- [ ] **O Dispositivo Físico:**
  - [ ] Habilitar Depuración USB
  - [ ] Conectar por USB
  - [ ] Aceptar autorización

### Ejecutar App
- [ ] Seleccionar dispositivo
- [ ] Click en Run ▶️
- [ ] Verificar que la app se instala
- [ ] Verificar pantalla de Login

## 🔗 Integración Frontend-Backend

### Para Emulador
- [x] Ya configurado: `http://10.0.2.2:8080`

### Para Dispositivo Físico
- [ ] Encontrar IP de tu PC: `ipconfig`
- [ ] Editar `ApiConfig.kt`:
  ```kotlin
  const val BASE_URL = "http://TU_IP:8080"
  ```
- [ ] Verificar misma red WiFi

## 🧪 Pruebas Funcionales

### Backend
- [ ] Registrar usuario:
  ```bash
  curl -X POST http://localhost:8080/api/auth/register \
    -H "Content-Type: application/json" \
    -d '{"email":"test@test.com","password":"test123","name":"Test"}'
  ```
- [ ] Login:
  ```bash
  curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"email":"admin@miplan.com","password":"admin123"}'
  ```
- [ ] Guardar token recibido

### Android
- [ ] Abrir app
- [ ] Ver pantalla de Login
- [ ] Click en "Registrarse"
- [ ] Llenar formulario
- [ ] Verificar registro exitoso
- [ ] Iniciar sesión
- [ ] Ver pantalla Home

## 📊 Estado del Proyecto

### Completado (100%)
- ✅ Arquitectura documentada
- ✅ Backend base funcional
- ✅ Frontend base funcional
- ✅ Autenticación completa
- ✅ CRUD de tareas (backend)
- ✅ Base de datos completa
- ✅ Navegación Android
- ✅ ViewModels y estados

### Por Implementar (Semanas 2-4)
- ⏳ Pantallas de tareas (Android)
- ⏳ Pantallas de tableros (Android)
- ⏳ Vista de calendario
- ⏳ Notificaciones
- ⏳ Panel de administración
- ⏳ Tests unitarios
- ⏳ Tests de integración

## 🎯 Orden de Desarrollo Recomendado

### Semana 1 (Completada)
- ✅ Configuración inicial
- ✅ Backend base
- ✅ Frontend base

### Semana 2
1. Implementar pantallas de tareas
2. Implementar CRUD visual de tareas
3. Probar integración completa

### Semana 3
1. Implementar tableros
2. Implementar calendario
3. Implementar notificaciones

### Semana 4
1. Panel de administración
2. Testing completo
3. Refinamiento y optimización

## 📚 Recursos

### Documentación del Proyecto
- `README.md` - Descripción general
- `docs/ARQUITECTURA.md` - Arquitectura detallada
- `docs/GUIA_DESARROLLO.md` - Guía paso a paso
- `docs/API.md` - Documentación de API

### Configuración
- `database/README.md` - Guía de base de datos
- `android/CONFIGURACION_ANDROID_STUDIO.md` - Guía Android
- `android/INICIO_RAPIDO.md` - Inicio rápido

## 🆘 Soporte

### Problemas Comunes

**Base de Datos:**
- Ver: `database/README.md` > Troubleshooting

**Backend:**
- Ver: `docs/GUIA_DESARROLLO.md` > Troubleshooting

**Android:**
- Ver: `android/CONFIGURACION_ANDROID_STUDIO.md` > Troubleshooting

### Logs Útiles

**Backend:**
```powershell
# Ver logs en consola donde corre el backend
```

**Android:**
```
Android Studio > Logcat (parte inferior)
Filtrar por: com.miplan
```

---

## 🎉 ¡Listo para Desarrollar!

Una vez completado este checklist:
1. ✅ Base de datos funcionando
2. ✅ Backend corriendo
3. ✅ App Android ejecutándose
4. ✅ Integración completa

**Siguiente paso:** Seguir la guía de desarrollo en `docs/GUIA_DESARROLLO.md`
