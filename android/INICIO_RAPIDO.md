# 🚀 Inicio Rápido - Android Studio

## ✅ Archivos Configurados

Ya están listos:
- ✅ `gradle-wrapper.jar` - Descargado
- ✅ `gradle-wrapper.properties` - Configurado
- ✅ `local.properties` - SDK configurado
- ✅ `gradlew.bat` - Script de Gradle

## 📱 Pasos para Ejecutar

### 1. Abrir en Android Studio

```
File > Open > Seleccionar carpeta: C:\Users\Jesus\CascadeProjects\MiPlan\android
```

### 2. Esperar Sincronización

Android Studio descargará automáticamente:
- Gradle 8.2
- Dependencias del proyecto (Compose, Hilt, Ktor, etc.)
- Esto puede tardar 5-10 minutos la primera vez

### 3. Crear/Seleccionar Dispositivo

**Opción A - Emulador:**
- Tools > Device Manager > Create Device
- Selecciona: Pixel 5
- Sistema: Android 14 (API 34)

**Opción B - Dispositivo Físico:**
- Habilita "Depuración USB" en tu teléfono
- Conecta por USB
- Acepta la autorización

### 4. Ejecutar

- Click en el botón verde "Run" ▶️
- O presiona `Shift + F10`

## 🔧 Si hay Errores

### Error: "Gradle sync failed"

```
Build > Clean Project
Build > Rebuild Project
```

### Error: "SDK not found"

```
File > Project Structure > SDK Location
Verifica: C:\Users\Jesus\AppData\Local\Android\Sdk
```

### Error: "Unresolved reference"

```
File > Invalidate Caches / Restart
```

## 📊 Verificar Logs

- **Logcat** (parte inferior) - Ver logs de la app
- **Build** (parte inferior) - Ver errores de compilación

## 🌐 Conectar con Backend

### Para Emulador:
Ya está configurado: `http://10.0.2.2:8080`

### Para Dispositivo Físico:
1. Encuentra tu IP:
   ```powershell
   ipconfig
   # Busca IPv4 Address: 192.168.1.XXX
   ```

2. Edita `app/src/main/java/com/miplan/data/remote/ApiConfig.kt`:
   ```kotlin
   const val BASE_URL = "http://192.168.1.XXX:8080"
   ```

3. Asegúrate de que ambos estén en la misma red WiFi

## ✨ Primera Ejecución

La app mostrará:
1. **Pantalla de Login** (sin backend: mostrará error de conexión)
2. **Con backend corriendo**: Podrás registrarte e iniciar sesión

## 🔗 Iniciar Backend

Antes de probar la app, inicia el backend:

```powershell
cd ..\backend
.\gradlew run
```

El backend debe estar corriendo en `http://localhost:8080`

## 📝 Credenciales de Prueba

**Usuario Admin (después de crear la BD):**
- Email: `admin@miplan.com`
- Password: `admin123`

## 🎯 Siguiente Paso

Ver guía completa: `CONFIGURACION_ANDROID_STUDIO.md`
