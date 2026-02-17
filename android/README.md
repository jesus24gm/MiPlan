# MiPlan - Android App

Aplicación móvil de gestión de tareas desarrollada con Jetpack Compose y Clean Architecture.

## 🚀 Inicio Rápido

### 1. Abrir en Android Studio

```
File > Open > Seleccionar esta carpeta (android)
```

### 2. Esperar Sincronización

Gradle descargará automáticamente todas las dependencias (primera vez: 5-10 minutos)

### 3. Ejecutar

- Selecciona un dispositivo/emulador
- Click en Run ▶️ o `Shift + F10`

## 📖 Guías Detalladas

- **[INICIO_RAPIDO.md](INICIO_RAPIDO.md)** - Pasos básicos para ejecutar
- **[CONFIGURACION_ANDROID_STUDIO.md](CONFIGURACION_ANDROID_STUDIO.md)** - Guía completa de configuración

## 🏗️ Arquitectura

### Clean Architecture + MVVM

```
app/
├── data/           # Capa de datos
│   ├── local/      # DataStore, caché
│   ├── remote/     # API, DTOs
│   └── repository/ # Implementaciones
├── domain/         # Capa de dominio
│   ├── model/      # Modelos de negocio
│   └── repository/ # Interfaces
├── ui/             # Capa de presentación
│   ├── screens/    # Pantallas Compose
│   ├── theme/      # Tema Material 3
│   └── navigation/ # Navegación
├── viewmodel/      # ViewModels
└── di/             # Inyección de dependencias (Hilt)
```

## 🔧 Tecnologías

- **UI:** Jetpack Compose + Material 3
- **Arquitectura:** Clean Architecture + MVVM
- **DI:** Hilt
- **Networking:** Ktor Client
- **Serialization:** Kotlinx Serialization
- **Storage:** DataStore Preferences
- **Navigation:** Navigation Compose
- **State:** StateFlow

## 🌐 Configuración de Backend

### Para Emulador (por defecto)
```kotlin
// ApiConfig.kt
const val BASE_URL = "http://10.0.2.2:8080"
```

### Para Dispositivo Físico
1. Encuentra tu IP: `ipconfig`
2. Edita `BASE_URL`:
   ```kotlin
   const val BASE_URL = "http://192.168.1.XXX:8080"
   ```
3. Misma red WiFi

## 📱 Pantallas Implementadas

- ✅ **LoginScreen** - Inicio de sesión
- ✅ **RegisterScreen** - Registro de usuario
- ✅ **HomeScreen** - Dashboard principal
- ⏳ **TaskListScreen** - Lista de tareas
- ⏳ **CreateTaskScreen** - Crear tarea
- ⏳ **BoardListScreen** - Lista de tableros
- ⏳ **CalendarScreen** - Vista de calendario
- ⏳ **NotificationScreen** - Notificaciones
- ⏳ **ProfileScreen** - Perfil de usuario
- ⏳ **AdminScreen** - Panel de administración

## 🧪 Testing

```bash
# Tests unitarios
.\gradlew test

# Tests instrumentados (requiere dispositivo)
.\gradlew connectedAndroidTest

# Generar reporte de cobertura
.\gradlew jacocoTestReport
```

## 🔍 Debugging

### Ver Logs
```
Android Studio > Logcat
Filtrar por: com.miplan
```

### Niveles de Log
- `DEBUG` - Información de desarrollo
- `INFO` - Flujo general
- `ERROR` - Errores y excepciones

### Logs de Red
Los requests HTTP se logean automáticamente en Logcat con tag `HttpClient`

## 📦 Build

### Debug APK
```bash
.\gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Release APK
```bash
.\gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

## 🐛 Troubleshooting

### Gradle Sync Failed
```
Build > Clean Project
Build > Rebuild Project
```

### Unresolved References
```
File > Invalidate Caches / Restart
```

### SDK Not Found
```
File > Project Structure > SDK Location
Verificar: C:\Users\Jesus\AppData\Local\Android\Sdk
```

Ver más soluciones: [CONFIGURACION_ANDROID_STUDIO.md](CONFIGURACION_ANDROID_STUDIO.md#troubleshooting)

## 📊 Dependencias Principales

```kotlin
// Compose
androidx.compose.ui:ui
androidx.compose.material3:material3
androidx.navigation:navigation-compose

// Hilt
com.google.dagger:hilt-android
androidx.hilt:hilt-navigation-compose

// Ktor Client
io.ktor:ktor-client-android
io.ktor:ktor-client-content-negotiation
io.ktor:ktor-client-auth

// Kotlinx
org.jetbrains.kotlinx:kotlinx-serialization-json
org.jetbrains.kotlinx:kotlinx-coroutines-android

// DataStore
androidx.datastore:datastore-preferences
```

## 🔐 Seguridad

- ✅ Tokens JWT almacenados en DataStore (encriptado)
- ✅ HTTPS en producción
- ✅ Validación de inputs
- ✅ Manejo seguro de credenciales

## 🎨 Personalización

### Cambiar Tema
Edita `ui/theme/Color.kt` y `Theme.kt`

### Cambiar Tipografía
Edita `ui/theme/Type.kt`

### Agregar Pantalla
1. Crear Composable en `ui/screens/`
2. Agregar ruta en `ui/navigation/Screen.kt`
3. Agregar en `NavGraph.kt`

## 📚 Recursos

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material 3](https://m3.material.io/)
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- [Ktor Client](https://ktor.io/docs/client.html)

## 🤝 Contribuir

Ver guía de desarrollo: `../docs/GUIA_DESARROLLO.md`

## 📄 Licencia

Ver archivo LICENSE en la raíz del proyecto.
