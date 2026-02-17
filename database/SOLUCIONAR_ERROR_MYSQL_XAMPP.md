# 🔧 Solucionar Error: MySQL shutdown unexpectedly en XAMPP

## 🔴 Error

```
Error: MySQL shutdown unexpectedly.
This may be due to a blocked port, missing dependencies,
improper privileges, a crash, or a shutdown by another method.
```

## 🎯 Soluciones (en orden de más fácil a más compleja)

---

## ✅ Solución 1: Eliminar Archivos de Log de InnoDB (Más Común)

### Paso 1: Detener MySQL

1. Abre **XAMPP Control Panel**
2. Si MySQL está corriendo (verde), click en **"Stop"**
3. Espera a que se detenga completamente

### Paso 2: Ejecutar Script de Reparación

**Opción A: Usar el script automático**

1. Ejecuta: `database\reparar_mysql_xampp.bat`
2. Sigue las instrucciones
3. Intenta iniciar MySQL en XAMPP

**Opción B: Manual**

1. Ve a: `C:\xampp\mysql\data`
2. **Elimina** estos archivos:
   - `ib_logfile0`
   - `ib_logfile1`
   - `ibtmp1` (si existe)
3. **NO BORRES** `ibdata1` ni las carpetas de bases de datos
4. Intenta iniciar MySQL en XAMPP

### Paso 3: Iniciar MySQL

1. En XAMPP Control Panel
2. Click en **"Start"** junto a MySQL
3. Debería iniciarse correctamente

---

## ✅ Solución 2: Verificar Puerto 3306

### Paso 1: Verificar si el puerto está ocupado

Abre PowerShell y ejecuta:
```powershell
netstat -ano | findstr :3306
```

Si aparece algo, el puerto está ocupado.

### Paso 2: Detener el proceso que usa el puerto

```powershell
# Encuentra el PID (última columna del comando anterior)
taskkill /PID NUMERO_PID /F
```

### Paso 3: Intenta iniciar MySQL de nuevo

---

## ✅ Solución 3: Cambiar el Puerto de MySQL

Si el puerto 3306 está ocupado y no puedes liberar:

### Paso 1: Editar configuración

1. Abre: `C:\xampp\mysql\bin\my.ini`
2. Busca la línea: `port=3306`
3. Cámbiala a: `port=3307`
4. Guarda el archivo

### Paso 2: Actualizar application.conf del backend

Edita: `backend/src/main/resources/application.conf`

```hocon
database {
    url = "jdbc:mysql://localhost:3307/miplan_db"  # ← Cambiar 3306 a 3307
    ...
}
```

### Paso 3: Reiniciar MySQL

---

## ✅ Solución 4: Reinstalar MySQL de XAMPP

### Paso 1: Backup de la base de datos

**Si MySQL inicia aunque sea por unos segundos:**

1. Abre phpMyAdmin rápidamente
2. Exporta `miplan_db`
3. Guarda el archivo SQL

**Si no puedes acceder:**

Copia la carpeta completa:
```
C:\xampp\mysql\data
```
A un lugar seguro.

### Paso 2: Reinstalar

1. Cierra XAMPP
2. Ve a: `C:\xampp\mysql`
3. Renombra la carpeta a: `mysql_old`
4. Descarga XAMPP de nuevo: https://www.apachefriends.org/
5. Instala solo MySQL
6. Copia las carpetas de bases de datos de `mysql_old\data` a `mysql\data`

---

## ✅ Solución 5: Usar MySQL Standalone (Alternativa)

Si XAMPP sigue dando problemas, instala MySQL standalone:

### Paso 1: Descargar MySQL

https://dev.mysql.com/downloads/installer/

### Paso 2: Instalar

1. Ejecuta el instalador
2. Selecciona "Developer Default"
3. Configura contraseña root
4. Completa la instalación

### Paso 3: Importar base de datos

```powershell
cd C:\Users\Jesus\CascadeProjects\MiPlan\database
mysql -u root -p < schema.sql
```

### Paso 4: Actualizar backend

Edita `application.conf` si cambiaste la contraseña:
```hocon
database {
    password = "tu-nueva-contraseña"
}
```

---

## 🔍 Diagnóstico Avanzado

### Ver Logs de Error

```powershell
Get-Content "C:\xampp\mysql\data\mysql_error.log" -Tail 50
```

### Errores Comunes en Logs

**Error: "Can't create/write to file"**
- Solución: Ejecuta XAMPP como Administrador

**Error: "Table 'mysql.plugin' doesn't exist"**
- Solución: Reinstala MySQL de XAMPP

**Error: "InnoDB: Operating system error number 32"**
- Solución: Cierra todos los programas que puedan estar usando MySQL

---

## 📋 Checklist de Verificación

Antes de pedir ayuda, verifica:

- [ ] MySQL está detenido en XAMPP
- [ ] No hay otro MySQL corriendo (servicios de Windows)
- [ ] Puerto 3306 no está ocupado
- [ ] Archivos `ib_logfile*` eliminados
- [ ] XAMPP ejecutado como Administrador
- [ ] Antivirus no está bloqueando MySQL
- [ ] Hay espacio en disco (mínimo 1GB libre)

---

## 🆘 Si Nada Funciona

### Opción 1: Usar Docker

```powershell
# Instalar Docker Desktop
# Luego ejecutar:
docker run -d --name miplan-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=password mysql:8.0
```

### Opción 2: Usar Base de Datos en la Nube

- **PlanetScale** (gratis): https://planetscale.com/
- **Railway** (gratis): https://railway.app/
- **Clever Cloud** (gratis): https://www.clever-cloud.com/

---

## 📞 Información para Soporte

Si necesitas pedir ayuda, proporciona:

1. **Versión de XAMPP:**
   - Ve a XAMPP Control Panel > About

2. **Últimas líneas del log de error:**
   ```powershell
   Get-Content "C:\xampp\mysql\data\mysql_error.log" -Tail 30
   ```

3. **Procesos usando puerto 3306:**
   ```powershell
   netstat -ano | findstr :3306
   ```

4. **Servicios MySQL:**
   ```powershell
   Get-Service | Where-Object {$_.Name -like '*mysql*'}
   ```

---

## ✅ Después de Solucionar

Una vez que MySQL inicie correctamente:

1. **Verifica que la base de datos existe:**
   - Abre: http://localhost/phpmyadmin
   - Busca `miplan_db`

2. **Si no existe, impórtala:**
   ```powershell
   cd C:\Users\Jesus\CascadeProjects\MiPlan\database
   .\importar_xampp.bat
   ```

3. **Inicia el backend:**
   ```powershell
   cd C:\Users\Jesus\CascadeProjects\MiPlan\backend
   .\gradlew run
   ```

4. **Prueba la app Android**

---

## 🎯 Prevención

Para evitar este error en el futuro:

1. ✅ Siempre detén MySQL correctamente (botón Stop en XAMPP)
2. ✅ No apagues la PC con MySQL corriendo
3. ✅ Haz backups regulares de la base de datos
4. ✅ Mantén XAMPP actualizado
5. ✅ No edites archivos en `C:\xampp\mysql\data` manualmente
