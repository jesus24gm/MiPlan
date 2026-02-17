# 🚀 Guía Rápida: Configurar Base de Datos con XAMPP

## ✅ Paso 1: Iniciar MySQL

1. **Abre XAMPP Control Panel**
   - Busca en el menú de inicio: "XAMPP Control Panel"
   - O ejecuta: `C:\xampp\xampp-control.exe`

2. **Inicia MySQL**
   - Busca la fila que dice "MySQL"
   - Click en el botón **"Start"**
   - Espera unos segundos
   - Debería aparecer:
     - Fondo **verde**
     - Texto **"Running"**
     - Puerto: **3306**

3. **Si da error al iniciar:**
   - Otro programa está usando el puerto 3306
   - Cierra otros servicios MySQL
   - O cambia el puerto en XAMPP (ver sección Troubleshooting)

## ✅ Paso 2: Importar Base de Datos

### Opción A: phpMyAdmin (Recomendado - Visual)

1. **Abre tu navegador** (Chrome, Firefox, Edge)

2. **Ve a:** http://localhost/phpmyadmin

3. **Deberías ver:**
   - Panel izquierdo con bases de datos
   - Menú superior con opciones

4. **Click en "SQL"** (en el menú superior)

5. **Importar el archivo:**
   - Click en **"Choose File"** o pestaña **"Import"**
   - Navega a: `C:\Users\Jesus\CascadeProjects\MiPlan\database\schema.sql`
   - Selecciona el archivo
   - Click en **"Go"** o **"Ejecutar"**

6. **Espera** a que termine (puede tardar 5-10 segundos)

7. **Verifica:**
   - En el panel izquierdo debería aparecer **"miplan_db"**
   - Click en "miplan_db"
   - Deberías ver 6 tablas:
     - boards
     - notifications
     - roles
     - tasks
     - user_tasks
     - users

### Opción B: Línea de Comandos (Automático)

1. **Asegúrate de que MySQL esté corriendo** (verde en XAMPP)

2. **Abre PowerShell** en la carpeta database:
   ```powershell
   cd C:\Users\Jesus\CascadeProjects\MiPlan\database
   ```

3. **Ejecuta el script:**
   ```powershell
   .\importar_xampp.bat
   ```

4. **Deberías ver:**
   ```
   ========================================
   Base de datos importada exitosamente!
   ========================================
   ```

## ✅ Paso 3: Verificar la Instalación

### En phpMyAdmin:

1. **Ve a:** http://localhost/phpmyadmin

2. **Click en "miplan_db"** (panel izquierdo)

3. **Click en la tabla "users"**

4. **Click en "Browse"** o "Examinar"

5. **Deberías ver:**
   - Un usuario con email: `admin@miplan.com`
   - Nombre: Admin
   - Role ID: 2 (ADMIN)

### En línea de comandos:

```powershell
C:\xampp\mysql\bin\mysql.exe -u root -e "USE miplan_db; SHOW TABLES;"
```

**Resultado esperado:**
```
+---------------------+
| Tables_in_miplan_db |
+---------------------+
| boards              |
| notifications       |
| roles               |
| tasks               |
| user_tasks          |
| users               |
+---------------------+
```

## ✅ Paso 4: Configurar Backend

Ahora que la base de datos está lista, configura el backend:

1. **Abre:** `C:\Users\Jesus\CascadeProjects\MiPlan\backend\src\main\resources\application.conf`

2. **Verifica la configuración:**
   ```hocon
   database {
       url = "jdbc:mysql://localhost:3306/miplan_db"
       driver = "com.mysql.cj.jdbc.Driver"
       user = "root"
       password = ""  # XAMPP no tiene contraseña por defecto
       maxPoolSize = 10
   }
   ```

3. **Guarda el archivo**

## ✅ Paso 5: Iniciar Backend

```powershell
cd C:\Users\Jesus\CascadeProjects\MiPlan\backend
.\gradlew run
```

**Deberías ver:**
```
[main] INFO ktor.application - Responding at http://0.0.0.0:8080
```

## ✅ Paso 6: Probar la Conexión

### Desde el navegador:

http://localhost:8080/health

**Respuesta esperada:** `OK`

### Desde PowerShell:

```powershell
curl http://localhost:8080/health
```

## 🎯 Resumen de Credenciales

### MySQL (XAMPP):
- **Host:** localhost
- **Puerto:** 3306
- **Usuario:** root
- **Contraseña:** (vacía)
- **Base de datos:** miplan_db

### Usuario Admin de MiPlan:
- **Email:** admin@miplan.com
- **Password:** admin123

### Backend:
- **URL:** http://localhost:8080
- **Health Check:** http://localhost:8080/health

### Android App (Emulador):
- **Backend URL:** http://10.0.2.2:8080

### Android App (Dispositivo Físico):
- **Backend URL:** http://TU_IP_LOCAL:8080
- Encuentra tu IP: `ipconfig` (busca IPv4)

## 🐛 Troubleshooting

### Error: "Can't connect to MySQL server"

**Causa:** MySQL no está corriendo

**Solución:**
1. Abre XAMPP Control Panel
2. Click en "Start" junto a MySQL
3. Espera a que aparezca en verde

### Error: "Port 3306 already in use"

**Causa:** Otro servicio MySQL está usando el puerto

**Solución 1:** Detener el otro MySQL
```powershell
# Como Administrador
net stop MySQL80
# O
net stop MySQL
```

**Solución 2:** Cambiar puerto de XAMPP
1. Abre: `C:\xampp\mysql\bin\my.ini`
2. Busca: `port=3306`
3. Cambia a: `port=3307`
4. Guarda
5. Reinicia MySQL en XAMPP
6. Actualiza `application.conf` con el nuevo puerto

### Error: "Access denied for user 'root'"

**Causa:** XAMPP tiene contraseña configurada

**Solución:**
1. Ve a phpMyAdmin
2. Click en "User accounts"
3. Busca la contraseña de root
4. Actualiza `application.conf` con la contraseña

### phpMyAdmin no carga

**Causa:** Apache no está corriendo

**Solución:**
1. En XAMPP Control Panel
2. Click en "Start" junto a Apache
3. Espera a que aparezca en verde
4. Intenta de nuevo: http://localhost/phpmyadmin

### Base de datos ya existe

**Solución:**
1. En phpMyAdmin
2. Click en "miplan_db"
3. Click en "Operations"
4. Scroll abajo
5. Click en "Drop the database"
6. Confirma
7. Importa el schema.sql de nuevo

## 📊 Verificación Final

Marca cada item cuando lo completes:

- [ ] XAMPP instalado
- [ ] MySQL corriendo (verde)
- [ ] Base de datos `miplan_db` creada
- [ ] 6 tablas visibles en phpMyAdmin
- [ ] Usuario admin existe
- [ ] Backend configurado
- [ ] Backend corriendo
- [ ] Health check responde OK
- [ ] App Android ejecutándose

## 🎉 ¡Listo!

Una vez completados todos los pasos, tu stack completo estará funcionando:

```
✅ Base de Datos (MySQL) → Puerto 3306
✅ Backend (Ktor) → Puerto 8080
✅ App Android → Emulador/Dispositivo
```

## 🚀 Siguiente Paso

Prueba el login en la app Android:
- Email: `admin@miplan.com`
- Password: `admin123`

O registra un nuevo usuario desde la app.
