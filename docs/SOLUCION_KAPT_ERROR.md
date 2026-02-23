# 🔧 Solución: Error de Kotlin KAPT

## 🎯 Problema

```
java.lang.IllegalAccessError: superclass access check failed
module jdk.compiler does not export com.sun.tools.javac.main
```

**Causa:** Incompatibilidad entre JDK 21+ y KAPT.

## ✅ Solución 1: Usar JDK 17 (Rápido)

### Paso 1: Configurar JDK en Android Studio

1. **File > Settings** (Ctrl+Alt+S)
2. **Build, Execution, Deployment > Build Tools > Gradle**
3. **Gradle JDK:** Selecciona una de estas opciones:
   - `Embedded JDK (JetBrains Runtime 17)`
   - `jbr-17`
   - Cualquier JDK 17

### Paso 2: Si no tienes JDK 17

1. En el mismo menú, click en el dropdown de **Gradle JDK**
2. Selecciona **"Download JDK..."**
3. Configura:
   - **Vendor:** JetBrains Runtime
   - **Version:** 17
4. Click **"Download"**
5. Espera a que descargue
6. Selecciónalo en **Gradle JDK**

### Paso 3: Aplicar cambios

1. Click **"Apply"** > **"OK"**
2. **File > Invalidate Caches / Restart**
3. Click **"Invalidate and Restart"**

### Paso 4: Limpiar y reconstruir

```
Build > Clean Project
Build > Rebuild Project
```

### Paso 5: Sync Gradle

```
File > Sync Project with Gradle Files
```

## ✅ Solución 2: Migrar a KSP (Recomendado a largo plazo)

KSP es el reemplazo moderno de KAPT, más rápido y compatible con JDK modernos.

### Actualizar build.gradle.kts (Proyecto raíz)

Reemplaza:
```kotlin
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20" apply false
}
```

Por:
```kotlin
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20" apply false
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
}
```

### Actualizar app/build.gradle.kts

**Cambiar plugins:**

De:
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    kotlin("kapt")
}
```

A:
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
}
```

**Cambiar dependencias:**

De:
```kotlin
// Hilt - Dependency Injection
implementation("com.google.dagger:hilt-android:2.48")
kapt("com.google.dagger:hilt-android-compiler:2.48")
implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
```

A:
```kotlin
// Hilt - Dependency Injection
implementation("com.google.dagger:hilt-android:2.48")
ksp("com.google.dagger:hilt-android-compiler:2.48")
implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
```

**Eliminar bloque kapt:**

Elimina:
```kotlin
kapt {
    correctErrorTypes = true
}
```

### Sync y Rebuild

```
File > Sync Project with Gradle Files
Build > Clean Project
Build > Rebuild Project
```

## 🐛 Verificar JDK Actual

Para ver qué JDK estás usando:

```powershell
# En PowerShell
java -version
```

O en Android Studio:
```
Help > About
```

Busca "JRE" o "Runtime version"

## ✅ Verificación

Después de aplicar la solución, deberías poder:

1. ✅ Sync Gradle sin errores
2. ✅ Build exitoso
3. ✅ Ejecutar la app

## 📊 Comparación KAPT vs KSP

| Característica | KAPT | KSP |
|---------------|------|-----|
| Velocidad | Lento | 2x más rápido |
| Compatibilidad JDK | Hasta JDK 17 | JDK 21+ |
| Futuro | Deprecado | Recomendado |
| Hilt | ✅ Soportado | ✅ Soportado |

## 🎯 Recomendación

**Para desarrollo inmediato:** Usa **Solución 1** (JDK 17)

**Para proyecto a largo plazo:** Migra a **Solución 2** (KSP)

## 🆘 Si Persiste el Error

1. **Elimina carpetas de caché:**
   ```powershell
   Remove-Item -Recurse -Force .gradle
   Remove-Item -Recurse -Force app\build
   Remove-Item -Recurse -Force build
   ```

2. **Reinicia Android Studio**

3. **Sync Gradle nuevamente**

4. **Verifica que no haya múltiples versiones de JDK en PATH:**
   ```powershell
   $env:JAVA_HOME
   ```

## 📚 Referencias

- [Hilt con KSP](https://dagger.dev/dev-guide/ksp)
- [Migración KAPT a KSP](https://developer.android.com/build/migrate-to-ksp)
