# 📸 Configuración de Imágenes para Tareas

## 🎯 Objetivo

Permitir agregar imágenes a las tareas desde:
1. **Galería del dispositivo**
2. **Cámara**
3. **Unsplash** (banco de imágenes)

---

## 📋 Paso 1: Obtener Credenciales

### Cloudinary (Ya tienes cuenta)

1. Ve a: https://cloudinary.com/console
2. En el Dashboard, copia:
   - **Cloud Name:** `tu_cloud_name`
   - **API Key:** `tu_api_key`
   - **API Secret:** `tu_api_secret`

### Unsplash API

1. Ve a: https://unsplash.com/oauth/applications
2. Click en **"New Application"**
3. Acepta los términos
4. Rellena:
   - **Application name:** MiPlan
   - **Description:** Task management app
5. Click en **"Create application"**
6. Copia el **Access Key**

---

## 🔧 Paso 2: Configurar Android

### 1. Agregar Dependencias

Abre `app/build.gradle.kts` y agrega:

```kotlin
dependencies {
    // ... dependencias existentes ...
    
    // Coil para cargar imágenes
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Cloudinary
    implementation("com.cloudinary:cloudinary-android:2.5.0")
    
    // Retrofit para Unsplash
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // Activity Result API
    implementation("androidx.activity:activity-compose:1.8.2")
}
```

### 2. Agregar Permisos

Abre `app/src/main/AndroidManifest.xml` y agrega:

```xml
<manifest ...>
    <!-- Permisos -->
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" 
                     android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
    
    <application ...>
        <!-- Provider para la cámara -->
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>
        
        <!-- ... resto del manifest ... -->
    </application>
</manifest>
```

### 3. Crear file_paths.xml

Crea `app/src/main/res/xml/file_paths.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-files-path 
        name="my_images" 
        path="Pictures" />
    <cache-path 
        name="my_cache" 
        path="." />
</paths>
```

### 4. Crear local.properties

Abre `local.properties` (en la raíz del proyecto) y agrega:

```properties
# Cloudinary
CLOUDINARY_CLOUD_NAME=tu_cloud_name
CLOUDINARY_API_KEY=tu_api_key
CLOUDINARY_API_SECRET=tu_api_secret

# Unsplash
UNSPLASH_ACCESS_KEY=tu_access_key
```

**⚠️ IMPORTANTE:** Este archivo NO se sube a Git (ya está en .gitignore)

### 5. Configurar BuildConfig

Abre `app/build.gradle.kts` y agrega en `android { defaultConfig { ... } }`:

```kotlin
android {
    // ... configuración existente ...
    
    defaultConfig {
        // ... configuración existente ...
        
        // Leer desde local.properties
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        
        buildConfigField("String", "CLOUDINARY_CLOUD_NAME", 
            "\"${properties.getProperty("CLOUDINARY_CLOUD_NAME")}\"")
        buildConfigField("String", "CLOUDINARY_API_KEY", 
            "\"${properties.getProperty("CLOUDINARY_API_KEY")}\"")
        buildConfigField("String", "CLOUDINARY_API_SECRET", 
            "\"${properties.getProperty("CLOUDINARY_API_SECRET")}\"")
        buildConfigField("String", "UNSPLASH_ACCESS_KEY", 
            "\"${properties.getProperty("UNSPLASH_ACCESS_KEY")}\"")
    }
    
    buildFeatures {
        buildConfig = true
        compose = true
    }
}
```

---

## 📝 Resumen de Archivos a Modificar

1. ✅ `app/build.gradle.kts` - Dependencias y BuildConfig
2. ✅ `app/src/main/AndroidManifest.xml` - Permisos y FileProvider
3. ✅ `app/src/main/res/xml/file_paths.xml` - Rutas para FileProvider (crear)
4. ✅ `local.properties` - Credenciales (NO subir a Git)

---

## 🧪 Verificar Configuración

Después de configurar, ejecuta:

```bash
File > Sync Project with Gradle Files
Build > Clean Project
Build > Rebuild Project
```

Si todo está bien, deberías poder acceder a:
```kotlin
BuildConfig.CLOUDINARY_CLOUD_NAME
BuildConfig.CLOUDINARY_API_KEY
BuildConfig.CLOUDINARY_API_SECRET
BuildConfig.UNSPLASH_ACCESS_KEY
```

---

## 🚀 Siguiente Paso

Una vez configurado, continuaremos con:
1. Crear `CloudinaryManager` para subir imágenes
2. Crear `UnsplashService` para buscar imágenes
3. Crear `ImagePickerManager` para galería y cámara
4. Actualizar `TaskFormScreen` con selector de imágenes
5. Mostrar imágenes en `TaskDetailScreen` y `TaskListScreen`

---

**¿Listo para continuar?** Avísame cuando hayas:
1. ✅ Copiado tus credenciales de Cloudinary
2. ✅ Obtenido tu Access Key de Unsplash
3. ✅ Agregado las credenciales a `local.properties`
4. ✅ Sincronizado el proyecto

Entonces continuaremos con la implementación completa. 🎉
