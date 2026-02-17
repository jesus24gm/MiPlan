# Configuración de Android Studio para MiPlan

## 📋 Requisitos Previos

- **Android Studio** Hedgehog (2023.1.1) o superior
- **JDK 17** (Android Studio lo incluye)
- **Conexión a Internet** (para descargar dependencias)

## 🚀 Paso 1: Preparar el Proyecto

### Opción A: Ejecutar script automático (Recomendado)

Abre PowerShell en la carpeta `android` y ejecuta:

```powershell
.\setup_gradle.bat
```

### Opción B: Descarga manual

Si el script falla, descarga manualmente:

1. Ve a: https://services.gradle.org/distributions/gradle-8.2-bin.zip
2. Extrae el ZIP
3. Copia el contenido a `C:\Users\Jesus\.gradle\wrapper\dists\gradle-8.2-bin\`

## 🎯 Paso 2: Abrir en Android Studio

1. **Abre Android Studio**

2. **File > Open**

3. **Navega a:** `C:\Users\Jesus\CascadeProjects\MiPlan\android`

4. **Selecciona la carpeta `android`** (no el proyecto completo)

5. **Click en "OK"**

6. **Espera la sincronización de Gradle** (puede tardar varios minutos)
   - Verás una barra de progreso en la parte inferior
   - "Gradle sync in progress..."

## ⚙️ Paso 3: Configurar SDK de Android

Si Android Studio pide configurar el SDK:

1. **File > Project Structure > SDK Location**

2. **Android SDK Location:** Debería detectarse automáticamente
   - Típicamente: `C:\Users\Jesus\AppData\Local\Android\Sdk`

3. **Si no está configurado:**
   - Click en "Edit"
   - Selecciona "Android SDK"
   - Instala SDK Platform 34 (Android 14)
   - Click "Apply" y "OK"

## 📱 Paso 4: Configurar Emulador o Dispositivo

### Opción A: Crear un Emulador (AVD)

1. **Tools > Device Manager**

2. **Click en "Create Device"**

3. **Selecciona un dispositivo:**
   - Recomendado: Pixel 5 o Pixel 6

4. **Selecciona una imagen del sistema:**
   - Recomendado: Android 14 (API 34) con Google APIs
   - Click "Download" si no está instalada
   - Click "Next"

5. **Configura el AVD:**
   - Nombre: Pixel_5_API_34
   - Click "Finish"

### Opción B: Usar Dispositivo Físico

1. **Habilita Opciones de Desarrollador en tu teléfono:**
   - Configuración > Acerca del teléfono
   - Toca 7 veces en "Número de compilación"

2. **Habilita Depuración USB:**
   - Configuración > Opciones de desarrollador
   - Activa "Depuración USB"

3. **Conecta el dispositivo con USB**

4. **Acepta la autorización** en el teléfono

## 🔧 Paso 5: Configurar Run Configuration

Si Android Studio no detecta automáticamente la configuración:

1. **Run > Edit Configurations**

2. **Click en "+" > Android App**

3. **Configura:**
   - Name: `app`
   - Module: `MiPlan.app.main`
   - Installation option: Default APK

4. **Click "Apply" y "OK"**

## ▶️ Paso 6: Ejecutar la Aplicación

1. **Selecciona el dispositivo** en la barra superior
   - Emulador o dispositivo físico

2. **Click en el botón verde "Run" (▶️)**
   - O presiona `Shift + F10`

3. **Espera a que compile y se instale**
   - Primera vez puede tardar varios minutos

## ✅ Verificar que Funciona

La app debería:
1. ✅ Compilar sin errores
2. ✅ Instalarse en el dispositivo/emulador
3. ✅ Mostrar la pantalla de Login

## 🐛 Troubleshooting

### Error: "Gradle sync failed"

**Solución 1:** Limpiar y reconstruir
```
Build > Clean Project
Build > Rebuild Project
```

**Solución 2:** Invalidar cachés
```
File > Invalidate Caches / Restart
```

**Solución 3:** Verificar conexión a Internet
- Gradle necesita descargar dependencias

### Error: "SDK location not found"

**Solución:**
1. File > Project Structure
2. SDK Location > Android SDK Location
3. Selecciona: `C:\Users\Jesus\AppData\Local\Android\Sdk`
4. Si no existe, click "Edit" e instala el SDK

### Error: "Unsupported Java version"

**Solución:**
1. File > Settings > Build, Execution, Deployment > Build Tools > Gradle
2. Gradle JDK: Selecciona "Embedded JDK (JetBrains Runtime 17)"

### Error: "Could not resolve dependencies"

**Solución:**
1. Verifica conexión a Internet
2. File > Settings > Build, Execution, Deployment > Gradle
3. Marca "Offline work" y desmárcala
4. Click "Apply"
5. File > Sync Project with Gradle Files

### Error: "Manifest merger failed"

**Solución:**
Verifica que `AndroidManifest.xml` esté en:
`android/app/src/main/AndroidManifest.xml`

### Error: "No devices found"

**Solución para Emulador:**
1. Tools > Device Manager
2. Crea un nuevo AVD (ver Paso 4)

**Solución para Dispositivo Físico:**
1. Verifica que la Depuración USB esté habilitada
2. Reconecta el cable USB
3. Acepta la autorización en el teléfono

### Error: "Unresolved reference" en el código

**Solución:**
1. File > Sync Project with Gradle Files
2. Build > Clean Project
3. Build > Rebuild Project
4. File > Invalidate Caches / Restart

### La app se cierra inmediatamente

**Causa:** El backend no está corriendo

**Solución:**
1. Inicia el backend primero (ver siguiente sección)
2. O la app mostrará error de conexión (esperado si backend no corre)

## 🔗 Paso 7: Conectar con Backend

### Si usas Emulador:

El `BASE_URL` ya está configurado correctamente:
```kotlin
// En ApiConfig.kt
const val BASE_URL = "http://10.0.2.2:8080"
```

`10.0.2.2` es la IP especial del emulador para acceder a `localhost` de tu PC.

### Si usas Dispositivo Físico:

1. **Encuentra la IP de tu PC:**
   ```powershell
   ipconfig
   # Busca "IPv4 Address" de tu red WiFi
   # Ejemplo: 192.168.1.100
   ```

2. **Edita `ApiConfig.kt`:**
   ```kotlin
   const val BASE_URL = "http://192.168.1.100:8080"
   ```

3. **Asegúrate de que el dispositivo y PC estén en la misma red WiFi**

## 📊 Estructura del Proyecto en Android Studio

```
MiPlan
├── app
│   ├── manifests
│   │   └── AndroidManifest.xml
│   ├── java
│   │   └── com.miplan
│   │       ├── MiPlanApp.kt
│   │       ├── MainActivity.kt
│   │       ├── data
│   │       ├── domain
│   │       ├── ui
│   │       ├── viewmodel
│   │       └── di
│   └── res
│       ├── values
│       └── xml
└── Gradle Scripts
    ├── build.gradle.kts (Project)
    ├── build.gradle.kts (Module: app)
    └── settings.gradle.kts
```

## 🎨 Personalizar Configuración

### Cambiar puerto del backend:

Edita `app/build.gradle.kts`:
```kotlin
buildTypes {
    debug {
        buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080\"")
    }
}
```

### Habilitar logs de red:

En `ApiConfig.kt`, el logging ya está habilitado:
```kotlin
install(Logging) {
    logger = Logger.DEFAULT
    level = LogLevel.ALL
}
```

Ver logs en: **Logcat** (parte inferior de Android Studio)

## 📝 Comandos Útiles de Gradle

```powershell
# Compilar el proyecto
.\gradlew build

# Limpiar el proyecto
.\gradlew clean

# Instalar en dispositivo
.\gradlew installDebug

# Ver dependencias
.\gradlew app:dependencies

# Ejecutar tests
.\gradlew test
```

## ✨ Próximos Pasos

Una vez que la app compile y ejecute:

1. ✅ Verifica que aparezca la pantalla de Login
2. ✅ Inicia el backend (ver `backend/README.md`)
3. ✅ Prueba el registro de usuario
4. ✅ Prueba el login

## 📚 Recursos Adicionales

- [Android Studio User Guide](https://developer.android.com/studio/intro)
- [Gradle Build Tool](https://gradle.org/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)

---

**¿Necesitas ayuda?** Revisa la sección de Troubleshooting o consulta los logs en Logcat.
