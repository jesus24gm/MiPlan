# 📝 Importar Base de Datos Manualmente en Railway

## Problema

El cliente MySQL de XAMPP es muy antiguo y no es compatible con el método de autenticación de Railway.

## ✅ Solución: Usar Railway CLI o phpMyAdmin

---

## Opción 1: Railway CLI (Recomendado)

### Paso 1: Instalar Railway CLI

```powershell
npm install -g @railway/cli
```

O descarga desde: https://docs.railway.app/develop/cli

### Paso 2: Login

```powershell
railway login
```

### Paso 3: Conectar al Proyecto

```powershell
cd C:\Users\Jesus\CascadeProjects\MiPlan
railway link
```

Selecciona tu proyecto "MiPlan"

### Paso 4: Importar Base de Datos

```powershell
railway run mysql -h shinkansen.proxy.rlwy.net -P 21599 -u root -pQMhzJGtIKOKXvqsTtmYirRtajKxUPyBF railway < database/schema.sql
```

---

## Opción 2: MySQL Workbench (Visual)

### Paso 1: Descargar MySQL Workbench

https://dev.mysql.com/downloads/workbench/

### Paso 2: Crear Conexión

1. Abre MySQL Workbench
2. Click en **"+"** para nueva conexión
3. Configura:
   - **Connection Name:** Railway MiPlan
   - **Hostname:** `shinkansen.proxy.rlwy.net`
   - **Port:** `21599`
   - **Username:** `root`
   - **Password:** Click "Store in Vault" y pega: `QMhzJGtIKOKXvqsTtmYirRtajKxUPyBF`
   - **Default Schema:** `railway`

### Paso 3: Conectar y Ejecutar

1. Click en la conexión
2. Abre el archivo: `File > Open SQL Script`
3. Selecciona: `C:\Users\Jesus\CascadeProjects\MiPlan\database\schema.sql`
4. Click en el rayo ⚡ para ejecutar
5. ¡Listo!

---

## Opción 3: Copiar y Pegar (Más Simple)

### Paso 1: Abrir schema.sql

Abre el archivo en un editor de texto:
```
C:\Users\Jesus\CascadeProjects\MiPlan\database\schema.sql
```

### Paso 2: Copiar Todo el Contenido

Selecciona todo (Ctrl+A) y copia (Ctrl+C)

### Paso 3: Usar Railway Web Console

1. Ve a Railway Dashboard
2. Click en tu servicio **MySQL**
3. Ve a la pestaña **"Data"** o **"Query"**
4. Pega el contenido del schema.sql
5. Click en **"Execute"** o **"Run"**

---

## Opción 4: Usar DBeaver (Gratis y Potente)

### Paso 1: Descargar DBeaver

https://dbeaver.io/download/

### Paso 2: Crear Conexión

1. Abre DBeaver
2. Click en **"Nueva Conexión"**
3. Selecciona **MySQL**
4. Configura:
   - **Host:** `shinkansen.proxy.rlwy.net`
   - **Puerto:** `21599`
   - **Base de datos:** `railway`
   - **Usuario:** `root`
   - **Contraseña:** `QMhzJGtIKOKXvqsTtmYirRtajKxUPyBF`
5. Click en **"Test Connection"**
6. Click en **"Finish"**

### Paso 3: Ejecutar Script

1. Click derecho en la conexión > **SQL Editor > Open SQL Script**
2. Selecciona `schema.sql`
3. Click en **Execute SQL Script** (Ctrl+Alt+X)
4. ¡Listo!

---

## ⚡ Opción Rápida: Usar Node.js y MySQL2

Si tienes Node.js instalado:

### Paso 1: Instalar mysql2

```powershell
npm install -g mysql2
```

### Paso 2: Crear script de importación

Guarda esto como `import.js`:

```javascript
const mysql = require('mysql2');
const fs = require('fs');

const connection = mysql.createConnection({
  host: 'shinkansen.proxy.rlwy.net',
  port: 21599,
  user: 'root',
  password: 'QMhzJGtIKOKXvqsTtmYirRtajKxUPyBF',
  database: 'railway',
  multipleStatements: true
});

const sql = fs.readFileSync('database/schema.sql', 'utf8');

connection.query(sql, (error, results) => {
  if (error) {
    console.error('Error:', error);
  } else {
    console.log('✅ Base de datos importada exitosamente!');
  }
  connection.end();
});
```

### Paso 3: Ejecutar

```powershell
node import.js
```

---

## 🎯 Recomendación

**Para ti:** Usa **MySQL Workbench** (Opción 2)

Es visual, fácil de usar y muy confiable.

1. Descarga: https://dev.mysql.com/downloads/workbench/
2. Instala (siguiente, siguiente, siguiente)
3. Crea conexión con los datos de arriba
4. Abre schema.sql y ejecuta

**Tiempo:** 5-10 minutos

---

## ✅ Verificar que Funcionó

Después de importar, verifica en Railway:

1. Ve a MySQL > Data
2. Deberías ver las tablas:
   - `users`
   - `tasks`
   - `boards`
   - `notifications`
   - `board_members`

O prueba desde la app Android:
- Registra un usuario
- Haz login
- Crea una tarea

Si funciona, ¡la base de datos está importada correctamente! 🎉
