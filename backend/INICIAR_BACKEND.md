# 🚀 Iniciar Backend de MiPlan

## ✅ Configuración Completada

La configuración del backend ya está lista:
- ✅ Base de datos: `miplan_db` en MySQL (XAMPP)
- ✅ Usuario: `root`
- ✅ Contraseña: (vacía)
- ✅ Puerto: 8080

## 🎯 Iniciar el Backend

### Opción 1: Desde PowerShell

```powershell
cd C:\Users\Jesus\CascadeProjects\MiPlan\backend
.\gradlew run
```

### Opción 2: Desde el IDE

Si usas IntelliJ IDEA:
1. Abre el proyecto `backend`
2. Busca `Application.kt`
3. Click derecho > Run

## 📊 Qué Esperar

### Primera vez (puede tardar 2-5 minutos):
```
Downloading https://services.gradle.org/distributions/gradle-8.2-bin.zip
...
Starting a Gradle Daemon
...
Compiling Kotlin sources
...
```

### Cuando esté listo verás:
```
[main] INFO  ktor.application - Autoreload is disabled because the development mode is off.
[main] INFO  ktor.application - Application started in X.XXX seconds.
[main] INFO  ktor.application - Responding at http://0.0.0.0:8080
```

## ✅ Verificar que Funciona

### 1. Health Check (Navegador)

Abre: http://localhost:8080/health

**Respuesta esperada:** `OK`

### 2. Health Check (PowerShell)

```powershell
curl http://localhost:8080/health
```

**Respuesta esperada:**
```
StatusCode        : 200
StatusDescription : OK
Content           : OK
```

### 3. Probar Login

```powershell
curl -X POST http://localhost:8080/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{\"email\":\"admin@miplan.com\",\"password\":\"admin123\"}'
```

**Respuesta esperada:** JSON con token JWT

## 🔗 Endpoints Disponibles

### Públicos (sin autenticación):
- `GET /health` - Estado del servidor
- `POST /api/auth/register` - Registro de usuario
- `POST /api/auth/login` - Login
- `GET /api/auth/verify-email?token=XXX` - Verificar email

### Protegidos (requieren JWT):
- `GET /api/auth/me` - Usuario actual
- `POST /api/auth/logout` - Cerrar sesión
- `GET /api/tasks` - Listar tareas
- `POST /api/tasks` - Crear tarea
- `GET /api/tasks/{id}` - Ver tarea
- `PUT /api/tasks/{id}` - Actualizar tarea
- `DELETE /api/tasks/{id}` - Eliminar tarea

Ver más en: `../docs/API.md`

## 🐛 Troubleshooting

### Error: "Address already in use"

**Causa:** El puerto 8080 ya está en uso

**Solución 1:** Detener el proceso que usa el puerto
```powershell
# Encontrar el proceso
netstat -ano | findstr :8080

# Matar el proceso (reemplaza PID)
taskkill /PID XXXX /F
```

**Solución 2:** Cambiar el puerto
Edita `application.conf`:
```hocon
ktor {
    deployment {
        port = 8081  # Cambiar a otro puerto
    }
}
```

### Error: "Can't connect to database"

**Causa:** MySQL no está corriendo

**Solución:**
1. Abre XAMPP Control Panel
2. Verifica que MySQL esté en verde
3. Si no, click en "Start"

### Error: "Access denied for user 'root'"

**Causa:** Contraseña incorrecta

**Solución:**
Edita `application.conf` y verifica:
```hocon
database {
    password = ""  # XAMPP no tiene contraseña
}
```

### Error: "Unknown database 'miplan_db'"

**Causa:** La base de datos no se importó

**Solución:**
```powershell
cd ..\database
.\importar_xampp.bat
```

### El backend se detiene solo

**Causa:** Error en el código o configuración

**Solución:**
Lee los logs en la consola para ver el error específico

## 📝 Logs Útiles

El backend muestra logs de:
- ✅ Conexión a base de datos
- ✅ Requests HTTP recibidos
- ✅ Errores y excepciones
- ✅ Autenticación JWT

Ejemplo:
```
[DefaultDispatcher-worker-1] INFO  ktor.application - 200 OK: POST /api/auth/login
[DefaultDispatcher-worker-2] INFO  ktor.application - 401 Unauthorized: GET /api/tasks
```

## 🔄 Detener el Backend

**En PowerShell:**
- Presiona `Ctrl + C`

**En IntelliJ:**
- Click en el botón rojo "Stop"

## 🎯 Siguiente Paso

Una vez que el backend esté corriendo:

1. ✅ Verifica: http://localhost:8080/health
2. ✅ Abre la app Android
3. ✅ Prueba el login con:
   - Email: `admin@miplan.com`
   - Password: `admin123`

## 📊 Stack Completo

Cuando todo esté corriendo:

```
✅ MySQL (XAMPP) → Puerto 3306
✅ Backend (Ktor) → Puerto 8080
✅ App Android → Emulador/Dispositivo
```

## 🔐 Credenciales de Prueba

**Usuario Admin:**
- Email: `admin@miplan.com`
- Password: `admin123`

**Para crear nuevos usuarios:**
- Usa la pantalla de registro en la app
- O usa el endpoint POST /api/auth/register

## 📚 Documentación

- `../docs/API.md` - Documentación completa de API
- `../docs/GUIA_DESARROLLO.md` - Guía de desarrollo
- `../README.md` - Descripción general del proyecto
