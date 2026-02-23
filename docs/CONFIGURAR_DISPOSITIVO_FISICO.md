# 📱 Configuración para Dispositivo Físico

## ✅ Cambio Realizado

He actualizado la URL del backend en `app/build.gradle.kts`:

**Antes (para emulador):**
```kotlin
buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080\"")
```

**Ahora (para tu dispositivo):**
```kotlin
buildConfigField("String", "BASE_URL", "\"http://192.168.1.146:8080\"")
```

## 🔧 Próximos Pasos en Android Studio

### 1. Sync Gradle

```
File > Sync Project with Gradle Files
```

### 2. Clean y Rebuild

```
Build > Clean Project
Build > Rebuild Project
```

### 3. Ejecutar la App

```
Run > Run 'app'
```

## ✅ Verificaciones Importantes

### 1. Ambos dispositivos en la misma red WiFi

- **Tu PC:** Conectada a WiFi (IP: 192.168.1.146)
- **Tu teléfono:** Conectado a la **misma red WiFi**

### 2. Firewall de Windows

Si sigue sin funcionar, permite el puerto 8080 en el firewall:

```powershell
# Ejecutar como Administrador
New-NetFirewallRule -DisplayName "MiPlan Backend" -Direction Inbound -LocalPort 8080 -Protocol TCP -Action Allow
```

### 3. Verificar que el backend esté corriendo

En tu navegador (en la PC):
```
http://localhost:8080/health
```

Debería mostrar: **OK**

### 4. Probar desde el teléfono

Abre el navegador del teléfono y ve a:
```
http://192.168.1.146:8080/health
```

Si muestra **OK**, la conexión funciona.

## 🔄 Si cambias de red WiFi

Si tu PC obtiene una IP diferente:

1. Verifica la nueva IP:
   ```powershell
   ipconfig
   ```

2. Actualiza `app/build.gradle.kts` con la nueva IP

3. Sync y Rebuild

## 📝 Notas

- **10.0.2.2** = Solo para emulador de Android
- **192.168.1.XXX** = Para dispositivo físico en la misma red
- **localhost** = Solo funciona en la misma máquina

## 🎯 Ahora Puedes

1. ✅ Registrar un nuevo usuario
2. ✅ Hacer login con: admin@miplan.com / admin123
3. ✅ Crear tareas
4. ✅ Ver el dashboard

¡Todo debería funcionar correctamente! 🚀
