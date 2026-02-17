# 🎯 Próximos Pasos - MiPlan en Railway

## ✅ Completado Hasta Ahora

- [x] Backend desplegado en Railway
- [x] Dominio generado: `https://miplan-production.up.railway.app`
- [x] Variables de entorno configuradas
- [x] Backend respondiendo correctamente
- [x] URL actualizada en app Android

---

## 📋 Pasos Restantes

### 1️⃣ Importar Base de Datos a Railway (5 minutos)

#### Obtener Credenciales de MySQL:

1. Ve a: https://railway.app/dashboard
2. Abre tu proyecto **MiPlan**
3. Click en el servicio **MySQL** (no backend)
4. Ve a la pestaña **"Variables"**
5. Copia estos valores:
   - `MYSQLHOST`
   - `MYSQLPORT`
   - `MYSQLDATABASE`
   - `MYSQLUSER`
   - `MYSQLPASSWORD`

#### Importar Schema:

**Opción A: Usar el script (Recomendado)**

```powershell
cd C:\Users\Jesus\CascadeProjects\MiPlan\database
.\importar_railway.bat
```

Sigue las instrucciones y pega las credenciales cuando te las pida.

**Opción B: Manual**

```powershell
cd C:\Users\Jesus\CascadeProjects\MiPlan\database

C:\xampp\mysql\bin\mysql.exe -h MYSQLHOST -P MYSQLPORT -u MYSQLUSER -pMYSQLPASSWORD MYSQLDATABASE < schema.sql
```

Reemplaza `MYSQLHOST`, `MYSQLPORT`, etc. con los valores reales.

---

### 2️⃣ Sincronizar y Reconstruir App Android (5 minutos)

#### En Android Studio:

1. **File > Sync Project with Gradle Files**
   - Espera a que termine (barra de progreso abajo)

2. **Build > Clean Project**
   - Espera a que termine

3. **Build > Rebuild Project**
   - Espera a que termine (puede tardar 1-2 minutos)

#### Verificar que la URL cambió:

Abre: `app/src/main/java/com/miplan/data/remote/ApiConfig.kt`

Deberías ver:
```kotlin
BASE_URL = "https://miplan-production.up.railway.app"
```

---

### 3️⃣ Probar la App (10 minutos)

#### A. Desde el Móvil/Emulador:

1. **Run > Run 'app'**
2. Selecciona tu dispositivo
3. Espera a que instale

#### B. Registrar Nuevo Usuario:

1. Click en **"Registrarse"**
2. Completa el formulario:
   - **Nombre:** Tu nombre
   - **Email:** Un email real
   - **Contraseña:** La que quieras
3. Click en **"Registrar"**

#### C. Verificar Email:

1. Revisa tu bandeja de entrada
2. Busca email de **"MiPlan"**
3. Click en el enlace de verificación
4. Deberías ver una página bonita: **"✅ Email Verificado!"**

#### D. Hacer Login:

1. Vuelve a la app
2. Haz login con tu email y contraseña
3. Deberías ver la pantalla principal (Home)

#### E. Crear Tareas:

1. Click en el botón **+** (flotante)
2. Crea una tarea de prueba
3. Verifica que se guarda correctamente

---

### 4️⃣ Probar desde Otro Dispositivo (Opcional)

#### Ventaja de Railway:

Ahora tu app funciona desde **cualquier lugar con internet**:

- ✅ Desde tu casa
- ✅ Desde el trabajo
- ✅ Desde el móvil con datos móviles
- ✅ Desde otro WiFi
- ✅ Desde otro país

#### Prueba:

1. Instala la app en otro dispositivo
2. Haz login con el mismo usuario
3. Las tareas deberían sincronizarse

---

## 🎉 ¡Felicidades! Fase 1 Completada

Si todo funciona, has completado exitosamente:

✅ **Backend en la nube** (Railway)
✅ **Base de datos en la nube** (MySQL en Railway)
✅ **App Android** conectada al backend
✅ **Emails funcionando** (verificación)
✅ **Acceso desde cualquier lugar**

---

## 📊 Resumen de URLs

- **Backend API:** https://miplan-production.up.railway.app
- **Health Check:** https://miplan-production.up.railway.app/health
- **Railway Dashboard:** https://railway.app/dashboard
- **GitHub Repo:** https://github.com/jesus24gm/MiPlan

---

## 🔄 Próximas Mejoras (Fase 2 - Opcional)

### Implementar Room Database (Modo Híbrido)

Para que la app funcione **offline** y sincronice cuando tenga internet:

1. Implementar Room Database local
2. Crear repositorios locales
3. Agregar lógica de sincronización
4. Manejo de conflictos

**Tiempo estimado:** 4-5 horas
**Beneficio:** App funciona sin internet

---

## 🆘 Troubleshooting

### Error: "Can't connect to database"

**Causa:** Schema no importado o credenciales incorrectas

**Solución:**
1. Verifica que importaste el schema
2. Verifica las credenciales de MySQL en Railway
3. Reinicia el backend en Railway

### Error: "Network error" en la app

**Causa:** URL incorrecta o backend caído

**Solución:**
1. Verifica que el backend esté corriendo en Railway
2. Verifica la URL en `build.gradle.kts`
3. Haz Sync y Rebuild en Android Studio

### Error: "Email not verified"

**Causa:** No hiciste click en el enlace del email

**Solución:**
1. Revisa tu bandeja de entrada (y spam)
2. Click en el enlace de verificación
3. Intenta hacer login de nuevo

### Backend se detiene después de un tiempo

**Causa:** Plan gratuito de Railway tiene límites

**Solución:**
1. Verifica tu uso en Railway Dashboard
2. El plan gratuito da $5/mes (suficiente para desarrollo)
3. Si necesitas más, considera plan Hobby ($5/mes)

---

## 💰 Costos

### Railway Plan Gratuito:

- ✅ $5 de crédito/mes
- ✅ ~500 horas de ejecución
- ✅ 1GB RAM
- ✅ 1GB almacenamiento

### Suficiente Para:

- ✅ Desarrollo personal
- ✅ Testing
- ✅ Demos
- ✅ Portfolio

### Si Necesitas Más:

- **Hobby Plan:** $5/mes
- **Pro Plan:** $20/mes

---

## 📚 Recursos

- **Railway Docs:** https://docs.railway.app/
- **Ktor Docs:** https://ktor.io/docs/
- **Jetpack Compose:** https://developer.android.com/jetpack/compose
- **Room Database:** https://developer.android.com/training/data-storage/room

---

## ✅ Checklist Final

- [ ] Base de datos importada a Railway
- [ ] App Android sincronizada y reconstruida
- [ ] Registro de usuario funciona
- [ ] Email de verificación llega
- [ ] Login funciona
- [ ] Crear tareas funciona
- [ ] App funciona desde cualquier lugar

---

¡Felicidades por llegar hasta aquí! 🎉🚀
