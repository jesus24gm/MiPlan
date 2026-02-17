# Solución: Usar XAMPP con MySQL existente

## ¿Por qué XAMPP es buena opción?

- ✅ Se instala en `C:\xampp` (no interfiere con MySQL existente)
- ✅ Usa su propia configuración
- ✅ Incluye phpMyAdmin
- ✅ No requiere contraseña por defecto
- ✅ Fácil de iniciar/detener

## Paso 1: Descargar XAMPP

1. Ve a: https://www.apachefriends.org/download.html
2. Descarga la versión para Windows
3. Ejecuta el instalador

## Paso 2: Instalación

Durante la instalación:
- ✅ Marca: Apache, MySQL, phpMyAdmin
- ❌ Desmarca: FileZilla, Mercury, Tomcat (no los necesitas)
- Instala en: `C:\xampp`

## Paso 3: Configurar puerto (si hay conflicto)

Si tu MySQL existente usa el puerto 3306, XAMPP puede dar error.

### Cambiar puerto de XAMPP:

1. Abre: `C:\xampp\mysql\bin\my.ini`
2. Busca la línea: `port=3306`
3. Cámbiala a: `port=3307`
4. Guarda el archivo

### Actualizar phpMyAdmin:

1. Abre: `C:\xampp\phpMyAdmin\config.inc.php`
2. Busca: `$cfg['Servers'][$i]['port'] = '';`
3. Cámbiala a: `$cfg['Servers'][$i]['port'] = '3307';`
4. Guarda el archivo

## Paso 4: Iniciar XAMPP

1. Abre **XAMPP Control Panel** (como Administrador)
2. Haz clic en **Start** junto a MySQL
3. Debería aparecer en verde "Running"

## Paso 5: Acceder a phpMyAdmin

1. Abre tu navegador
2. Ve a: http://localhost/phpmyadmin
   - Si cambiaste el puerto: http://localhost/phpmyadmin (el puerto de Apache es diferente)

## Paso 6: Importar schema.sql

En phpMyAdmin:
1. Haz clic en "SQL" en el menú superior
2. Haz clic en "Choose File"
3. Selecciona: `C:\Users\Jesus\CascadeProjects\MiPlan\database\schema.sql`
4. Haz clic en "Go" o "Ejecutar"
5. Verifica que aparezca `miplan_db` en el panel izquierdo

## Paso 7: Configurar Backend

Edita: `backend/src/main/resources/application.conf`

**Si usas puerto 3306 (por defecto):**
```hocon
database {
    url = "jdbc:mysql://localhost:3306/miplan_db"
    driver = "com.mysql.cj.jdbc.Driver"
    user = "root"
    password = ""  # XAMPP no tiene contraseña por defecto
    maxPoolSize = 10
}
```

**Si cambiaste a puerto 3307:**
```hocon
database {
    url = "jdbc:mysql://localhost:3307/miplan_db"
    driver = "com.mysql.cj.jdbc.Driver"
    user = "root"
    password = ""
    maxPoolSize = 10
}
```

## Verificar que funciona

En PowerShell:

```powershell
# Si usas puerto 3306
C:\xampp\mysql\bin\mysql.exe -u root miplan_db -e "SHOW TABLES;"

# Si usas puerto 3307
C:\xampp\mysql\bin\mysql.exe -u root -P 3307 miplan_db -e "SHOW TABLES;"
```

Deberías ver:
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

## Troubleshooting

### Error: Puerto 3306 en uso

**Solución:** Cambia el puerto de XAMPP a 3307 (ver Paso 3)

### Error: MySQL no inicia en XAMPP

**Solución 1:** Detén el MySQL existente
```powershell
# Como Administrador
net stop MySQL80
# O
net stop MySQL
```

**Solución 2:** Usa puerto diferente (3307)

### Error: No puedo acceder a phpMyAdmin

**Verifica que Apache esté corriendo:**
- En XAMPP Control Panel, Apache debe estar en verde
- Prueba: http://localhost

## Ventajas de esta solución

✅ No necesitas la contraseña del MySQL antiguo
✅ No interfiere con instalaciones previas
✅ Fácil de usar con interfaz visual
✅ Puedes iniciar/detener cuando quieras
✅ Perfecto para desarrollo

## Comandos útiles

```powershell
# Conectar a MySQL de XAMPP
C:\xampp\mysql\bin\mysql.exe -u root

# Ver bases de datos
C:\xampp\mysql\bin\mysql.exe -u root -e "SHOW DATABASES;"

# Ejecutar script
C:\xampp\mysql\bin\mysql.exe -u root < schema.sql

# Backup
C:\xampp\mysql\bin\mysqldump.exe -u root miplan_db > backup.sql
```
