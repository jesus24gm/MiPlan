# 🚀 Guía: Desplegar MiPlan en Railway

## 📋 Requisitos Previos

- [ ] Cuenta de GitHub (gratis)
- [ ] Cuenta de Railway (gratis)
- [ ] Git instalado en tu PC

---

## ✅ Paso 1: Crear Repositorio en GitHub

### 1.1 Crear Cuenta en GitHub (si no tienes)

1. Ve a: https://github.com/signup
2. Completa el registro
3. Verifica tu email

### 1.2 Crear Nuevo Repositorio

1. Ve a: https://github.com/new
2. Configura:
   - **Repository name:** `MiPlan`
   - **Description:** `Sistema de gestión de tareas con Android + Ktor`
   - **Visibility:** Private (o Public si quieres)
   - ❌ NO marques "Initialize with README"
3. Click en **"Create repository"**

### 1.3 Copiar la URL del Repositorio

Verás algo como:
```
https://github.com/TU_USUARIO/MiPlan.git
```

**Cópiala**, la necesitarás después.

---

## ✅ Paso 2: Subir Código a GitHub

### 2.1 Inicializar Git en el Proyecto

Abre PowerShell en la carpeta del proyecto:

```powershell
cd C:\Users\Jesus\CascadeProjects\MiPlan
```

### 2.2 Configurar Git (primera vez)

```powershell
git config --global user.name "Tu Nombre"
git config --global user.email "tu-email@gmail.com"
```

### 2.3 Inicializar Repositorio

```powershell
git init
```

### 2.4 Crear .gitignore Principal

Ya tenemos .gitignore en backend, pero necesitamos uno principal.

Ejecuta en PowerShell:

```powershell
@"
# Android
android/.gradle/
android/build/
android/local.properties
android/.idea/
android/*.iml
android/app/build/

# Backend
backend/.gradle/
backend/build/
backend/.idea/
backend/*.iml

# Database
database/backup/

# Environment
.env
*.env
!.env.example

# OS
.DS_Store
Thumbs.db
"@ | Out-File -FilePath .gitignore -Encoding UTF8
```

### 2.5 Añadir Archivos

```powershell
git add .
```

### 2.6 Hacer Commit

```powershell
git commit -m "Initial commit: MiPlan app with Ktor backend"
```

### 2.7 Conectar con GitHub

Reemplaza `TU_USUARIO` con tu usuario de GitHub:

```powershell
git remote add origin https://github.com/TU_USUARIO/MiPlan.git
git branch -M main
git push -u origin main
```

**Si pide credenciales:**
- Usuario: Tu usuario de GitHub
- Contraseña: Usa un **Personal Access Token** (no tu contraseña)

#### Crear Personal Access Token:

1. Ve a: https://github.com/settings/tokens
2. Click en "Generate new token (classic)"
3. Marca: `repo` (Full control of private repositories)
4. Click en "Generate token"
5. **Copia el token** (solo se muestra una vez)
6. Úsalo como contraseña en Git

---

## ✅ Paso 3: Crear Cuenta en Railway

### 3.1 Registrarse

1. Ve a: https://railway.app/
2. Click en **"Start a New Project"** o **"Login"**
3. Selecciona **"Login with GitHub"**
4. Autoriza Railway

### 3.2 Verificar Cuenta

Railway te da **$5 de crédito gratis** al mes (suficiente para este proyecto).

---

## ✅ Paso 4: Crear Proyecto en Railway

### 4.1 Nuevo Proyecto

1. En Railway, click en **"New Project"**
2. Selecciona **"Deploy from GitHub repo"**
3. Click en **"Configure GitHub App"**
4. Selecciona tu repositorio **"MiPlan"**
5. Click en **"Deploy Now"**

### 4.2 Seleccionar Servicio

Railway detectará automáticamente que es un proyecto Kotlin/Gradle.

1. Selecciona la carpeta **"backend"** como root
2. Railway comenzará a construir

---

## ✅ Paso 5: Agregar Base de Datos MySQL

### 5.1 Agregar MySQL al Proyecto

1. En tu proyecto de Railway, click en **"+ New"**
2. Selecciona **"Database"**
3. Selecciona **"Add MySQL"**
4. Railway creará una base de datos MySQL automáticamente

### 5.2 Obtener Credenciales

1. Click en el servicio **"MySQL"**
2. Ve a la pestaña **"Variables"**
3. Verás variables como:
   - `MYSQLHOST`
   - `MYSQLPORT`
   - `MYSQLDATABASE`
   - `MYSQLUSER`
   - `MYSQLPASSWORD`

**No necesitas copiarlas manualmente**, Railway las conecta automáticamente.

---

## ✅ Paso 6: Configurar Variables de Entorno

### 6.1 Ir al Servicio Backend

1. Click en el servicio de tu backend (no MySQL)
2. Ve a la pestaña **"Variables"**

### 6.2 Agregar Variables

Click en **"+ New Variable"** y agrega cada una:

#### Base de Datos:

```
DATABASE_URL = jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}
DATABASE_USER = ${{MySQL.MYSQLUSER}}
DATABASE_PASSWORD = ${{MySQL.MYSQLPASSWORD}}
```

#### JWT:

```
JWT_SECRET = miplan-production-secret-key-change-this-to-something-random
```

#### Email (usa tus credenciales):

```
EMAIL_HOST = smtp.gmail.com
EMAIL_PORT = 587
EMAIL_USERNAME = jeez24897@gmail.com
EMAIL_PASSWORD = eircvkpvdhhiunoa
EMAIL_FROM = MiPlan <jeez24897@gmail.com>
```

#### URL Base (la configuraremos después):

```
BASE_URL = https://tu-app.up.railway.app
```

#### Puerto:

```
PORT = 8080
```

### 6.3 Guardar

Railway redesplegará automáticamente con las nuevas variables.

---

## ✅ Paso 7: Importar Base de Datos

### 7.1 Conectarse a MySQL de Railway

Desde tu PC, ejecuta:

```powershell
# Instalar MySQL client si no lo tienes
# O usa el que viene con XAMPP

cd C:\Users\Jesus\CascadeProjects\MiPlan\database

# Conectar (reemplaza con tus credenciales de Railway)
C:\xampp\mysql\bin\mysql.exe -h MYSQLHOST -P MYSQLPORT -u MYSQLUSER -p
```

Cuando pida password, ingresa `MYSQLPASSWORD` de Railway.

### 7.2 Importar Schema

```sql
USE nombre_de_tu_base_datos;
source C:/Users/Jesus/CascadeProjects/MiPlan/database/schema.sql;
exit;
```

**O desde PowerShell:**

```powershell
Get-Content schema.sql | C:\xampp\mysql\bin\mysql.exe -h MYSQLHOST -P MYSQLPORT -u MYSQLUSER -p nombre_base_datos
```

---

## ✅ Paso 8: Obtener URL del Backend

### 8.1 Generar Dominio

1. En Railway, click en tu servicio backend
2. Ve a la pestaña **"Settings"**
3. Scroll hasta **"Networking"**
4. Click en **"Generate Domain"**
5. Railway generará una URL como: `https://miplan-backend-production.up.railway.app`

### 8.2 Actualizar BASE_URL

1. Copia la URL generada
2. Ve a **"Variables"**
3. Edita `BASE_URL` con la URL completa
4. Guarda

---

## ✅ Paso 9: Verificar Deployment

### 9.1 Ver Logs

1. En Railway, ve a la pestaña **"Deployments"**
2. Click en el deployment más reciente
3. Ve a **"View Logs"**
4. Deberías ver:
   ```
   MiPlan Backend iniciado correctamente
   Responding at http://0.0.0.0:8080
   ```

### 9.2 Probar Health Check

Abre tu navegador y ve a:
```
https://TU-URL.up.railway.app/health
```

Debería mostrar: **OK**

---

## ✅ Paso 10: Actualizar App Android

### 10.1 Editar build.gradle.kts

Archivo: `C:\Users\Jesus\AndroidStudioProjects\MiPlan\app\build.gradle.kts`

Cambia:

```kotlin
buildConfigField("String", "BASE_URL", "\"http://192.168.1.146:8080\"")
```

Por:

```kotlin
buildConfigField("String", "BASE_URL", "\"https://TU-URL.up.railway.app\"")
```

### 10.2 Sync y Rebuild

En Android Studio:
1. **File > Sync Project with Gradle Files**
2. **Build > Clean Project**
3. **Build > Rebuild Project**

### 10.3 Ejecutar App

1. **Run > Run 'app'**
2. Prueba hacer login
3. ¡Debería funcionar desde cualquier lugar con internet!

---

## ✅ Paso 11: Probar desde el Móvil

### 11.1 Registrar Nuevo Usuario

1. Abre la app en tu móvil
2. Registra un nuevo usuario
3. Revisa tu email
4. Click en el enlace de verificación
5. Haz login

### 11.2 Crear Tareas

1. Crea algunas tareas
2. Edítalas
3. Elimínalas
4. Todo debería funcionar perfectamente

---

## 🎉 ¡Listo!

Tu app ahora funciona con backend en la nube:

✅ Backend en Railway (24/7)
✅ MySQL en Railway
✅ Emails funcionando
✅ App funciona desde cualquier lugar con internet

---

## 🔧 Mantenimiento

### Ver Logs en Tiempo Real

1. Railway > Tu Proyecto > Backend
2. Pestaña "Deployments"
3. Click en deployment activo
4. "View Logs"

### Actualizar Código

```powershell
cd C:\Users\Jesus\CascadeProjects\MiPlan

# Hacer cambios en el código

git add .
git commit -m "Descripción de cambios"
git push

# Railway redesplegará automáticamente
```

### Monitorear Uso

1. Railway > Dashboard
2. Ve tu uso de:
   - CPU
   - RAM
   - Almacenamiento
   - Ancho de banda

---

## 💰 Costos

**Plan Gratuito de Railway:**
- ✅ $5 de crédito/mes
- ✅ Suficiente para uso personal
- ✅ ~500 horas de ejecución

**Si necesitas más:**
- Plan Hobby: $5/mes
- Plan Pro: $20/mes

---

## 🆘 Troubleshooting

### Error: "Build failed"

**Solución:**
1. Verifica que `railway.json` esté en la carpeta `backend`
2. Verifica que todas las variables de entorno estén configuradas

### Error: "Can't connect to database"

**Solución:**
1. Verifica que MySQL esté corriendo en Railway
2. Verifica las variables `DATABASE_*`
3. Verifica que importaste el schema

### App no conecta

**Solución:**
1. Verifica que la URL en `build.gradle.kts` sea correcta
2. Verifica que incluya `https://`
3. Haz Sync y Rebuild en Android Studio

---

## 📚 Recursos

- Railway Docs: https://docs.railway.app/
- Railway Discord: https://discord.gg/railway
- Ktor Docs: https://ktor.io/docs/

---

¡Felicidades! Tu app está en producción 🚀
