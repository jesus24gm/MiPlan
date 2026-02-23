# 📦 Migración del Código MiPlan

## 🎯 Objetivo

Copiar todo el código del proyecto original a este nuevo proyecto de Android Studio.

## 📋 Pasos a Seguir

### Paso 1: Actualizar build.gradle.kts (Proyecto raíz)

Reemplaza el contenido de `build.gradle.kts` con:

```kotlin
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20" apply false
}
```

### Paso 2: Actualizar app/build.gradle.kts

Reemplaza el contenido de `app/build.gradle.kts` con:

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    kotlin("kapt")
}

android {
    namespace = "com.miplan"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.miplan"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"https://api.miplan.com\"")
        }
        debug {
            isMinifyEnabled = false
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Ktor Client
    val ktorVersion = "2.3.5"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Coil
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.8")
    
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

kapt {
    correctErrorTypes = true
}
```

### Paso 3: Copiar Código Fuente

Ejecuta el script:

```powershell
.\COPIAR_CODIGO.bat
```

O manualmente copia:
```
C:\Users\Jesus\CascadeProjects\MiPlan\android\app\src\main\java\com\miplan\
```
A:
```
C:\Users\Jesus\AndroidStudioProjects\MiPlan\app\src\main\java\com\miplan\
```

### Paso 4: Copiar AndroidManifest.xml

Copia el contenido de:
```
C:\Users\Jesus\CascadeProjects\MiPlan\android\app\src\main\AndroidManifest.xml
```

A:
```
C:\Users\Jesus\AndroidStudioProjects\MiPlan\app\src\main\AndroidManifest.xml
```

### Paso 5: Copiar Recursos (res/)

Copia todas las carpetas de:
```
C:\Users\Jesus\CascadeProjects\MiPlan\android\app\src\main\res\
```

A:
```
C:\Users\Jesus\AndroidStudioProjects\MiPlan\app\src\main\res\
```

Esto incluye:
- `values/strings.xml`
- `values/themes.xml`
- `values/ic_launcher_background.xml`
- `xml/backup_rules.xml`
- `xml/data_extraction_rules.xml`
- `mipmap-anydpi-v26/` (iconos)
- `drawable/` (iconos foreground)

### Paso 6: Copiar ProGuard Rules

Copia el contenido de:
```
C:\Users\Jesus\CascadeProjects\MiPlan\android\app\proguard-rules.pro
```

A:
```
C:\Users\Jesus\AndroidStudioProjects\MiPlan\app\proguard-rules.pro
```

### Paso 7: Eliminar libs.versions.toml (si existe)

Si existe el archivo `gradle/libs.versions.toml`, elimínalo ya que usaremos dependencias directas.

### Paso 8: Sync Gradle

En Android Studio:
```
File > Sync Project with Gradle Files
```

Espera a que descargue todas las dependencias (puede tardar varios minutos).

### Paso 9: Clean y Rebuild

```
Build > Clean Project
Build > Rebuild Project
```

### Paso 10: Ejecutar

```
Run > Run 'app'
```

## ✅ Verificación

Después de completar todos los pasos, deberías tener:

- ✅ Todas las clases de dominio (User, Task, Board, etc.)
- ✅ Todos los repositorios implementados
- ✅ Todos los ViewModels
- ✅ Todas las pantallas (Login, Register, Home)
- ✅ Navegación configurada
- ✅ Tema Material 3
- ✅ Hilt configurado
- ✅ Ktor Client configurado

## 🐛 Problemas Comunes

### Error: "Unresolved reference"

**Solución:**
```
File > Invalidate Caches / Restart
```

### Error: "Duplicate class"

**Solución:**
Elimina el archivo `MainActivity.kt` generado por defecto si existe en `com.example.miplan`

### Error: "Cannot find symbol: class BuildConfig"

**Solución:**
Verifica que en `build.gradle.kts` tengas:
```kotlin
buildFeatures {
    buildConfig = true
}
```

### Error de namespace

**Solución:**
Asegúrate de que en `AndroidManifest.xml` el package sea `com.miplan` y no `com.example.miplan`

## 📊 Estructura Final

```
app/src/main/
├── AndroidManifest.xml
├── java/com/miplan/
│   ├── MiPlanApp.kt
│   ├── MainActivity.kt
│   ├── data/
│   │   ├── local/
│   │   │   └── TokenManager.kt
│   │   ├── remote/
│   │   │   ├── ApiConfig.kt
│   │   │   ├── ApiService.kt
│   │   │   └── dto/
│   │   └── repository/
│   ├── domain/
│   │   ├── model/
│   │   └── repository/
│   ├── ui/
│   │   ├── navigation/
│   │   ├── screens/
│   │   └── theme/
│   ├── viewmodel/
│   └── di/
└── res/
    ├── values/
    ├── xml/
    ├── mipmap-anydpi-v26/
    └── drawable/
```

## 🚀 Siguiente Paso

Una vez migrado todo el código, consulta:
- `../CascadeProjects/MiPlan/docs/GUIA_DESARROLLO.md` - Guía de desarrollo
- `../CascadeProjects/MiPlan/docs/API.md` - Documentación de API
- `../CascadeProjects/MiPlan/CHECKLIST_CONFIGURACION.md` - Checklist completo
