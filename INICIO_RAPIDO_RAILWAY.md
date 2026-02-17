# ⚡ Inicio Rápido: Desplegar en Railway

## 🎯 Resumen en 5 Pasos

### 1️⃣ Configurar Git (5 min)

```powershell
cd C:\Users\Jesus\CascadeProjects\MiPlan
.\SETUP_GIT.bat
```

### 2️⃣ Crear Repositorio en GitHub (2 min)

1. Ve a: https://github.com/new
2. Nombre: `MiPlan`
3. Click "Create repository"
4. Copia la URL

### 3️⃣ Subir Código (3 min)

```powershell
git remote add origin https://github.com/TU_USUARIO/MiPlan.git
git branch -M main
git push -u origin main
```

### 4️⃣ Desplegar en Railway (10 min)

1. Ve a: https://railway.app/
2. Login con GitHub
3. "New Project" > "Deploy from GitHub repo"
4. Selecciona "MiPlan"
5. Agrega MySQL: "+ New" > "Database" > "MySQL"
6. Configura variables de entorno (ver guía completa)
7. "Generate Domain" para obtener URL

### 5️⃣ Actualizar App Android (5 min)

En `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "BASE_URL", "\"https://TU-URL.up.railway.app\"")
```

Sync > Clean > Rebuild > Run

---

## ✅ Variables de Entorno en Railway

Copia y pega en Railway > Backend > Variables:

```
DATABASE_URL=jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}
DATABASE_USER=${{MySQL.MYSQLUSER}}
DATABASE_PASSWORD=${{MySQL.MYSQLPASSWORD}}
JWT_SECRET=miplan-production-secret-change-this
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=jeez24897@gmail.com
EMAIL_PASSWORD=eircvkpvdhhiunoa
EMAIL_FROM=MiPlan <jeez24897@gmail.com>
BASE_URL=https://TU-URL.up.railway.app
PORT=8080
```

---

## 🔍 Verificar que Funciona

1. **Health Check:**
   ```
   https://TU-URL.up.railway.app/health
   ```
   Debe mostrar: `OK`

2. **Desde la App:**
   - Registra un usuario
   - Verifica email
   - Haz login
   - Crea una tarea

---

## 📚 Guía Completa

Ver: `GUIA_DEPLOYMENT_RAILWAY.md` para instrucciones detalladas.

---

## 🆘 Ayuda Rápida

**Build falla:**
- Verifica `railway.json` en carpeta `backend`
- Verifica variables de entorno

**No conecta a BD:**
- Importa schema.sql a MySQL de Railway
- Verifica variables `DATABASE_*`

**App no conecta:**
- Verifica URL en `build.gradle.kts`
- Incluye `https://`
- Sync y Rebuild

---

## ⏱️ Tiempo Total: ~25 minutos

¡Tu app estará en producción! 🚀
