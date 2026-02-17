# Resetear Contraseña de MySQL en Windows

## Método 1: Usando MySQL Workbench (Más Fácil)

1. **Abre MySQL Workbench** (si lo tienes instalado)
2. En la pantalla de conexión, haz clic en "Forgot Password"
3. Sigue el asistente para resetear la contraseña

## Método 2: Usando línea de comandos

### Paso 1: Detener el servicio MySQL

Abre PowerShell como **Administrador** y ejecuta:

```powershell
# Detener el servicio
net stop MySQL80
# O si es otra versión:
net stop MySQL
```

### Paso 2: Crear archivo de reset

Crea un archivo `C:\mysql-init.txt` con este contenido:

```sql
ALTER USER 'root'@'localhost' IDENTIFIED BY 'nueva_contraseña_segura';
FLUSH PRIVILEGES;
```

### Paso 3: Iniciar MySQL en modo seguro

```powershell
# Navega a la carpeta de MySQL (ajusta la ruta según tu instalación)
cd "C:\Program Files\MySQL\MySQL Server 8.0\bin"

# Inicia MySQL con el archivo de reset
.\mysqld.exe --init-file=C:\mysql-init.txt --console
```

### Paso 4: En otra ventana de PowerShell

Espera unos segundos y luego presiona `Ctrl+C` en la ventana donde corre mysqld.

### Paso 5: Reiniciar el servicio normalmente

```powershell
net start MySQL80
```

### Paso 6: Probar la nueva contraseña

```powershell
mysql -u root -p
# Ingresa: nueva_contraseña_segura
```

### Paso 7: Eliminar el archivo temporal

```powershell
del C:\mysql-init.txt
```

## Método 3: Reinstalar MySQL (Última opción)

Si nada funciona:

1. **Desinstalar MySQL completamente:**
   - Panel de Control > Programas > Desinstalar MySQL
   - Eliminar carpeta: `C:\Program Files\MySQL`
   - Eliminar carpeta: `C:\ProgramData\MySQL`

2. **Instalar MySQL de nuevo:**
   - Descarga: https://dev.mysql.com/downloads/installer/
   - Durante la instalación, configura una nueva contraseña que recuerdes

## Verificar instalación de MySQL

Para encontrar dónde está instalado MySQL:

```powershell
# Buscar el servicio
Get-Service | Where-Object {$_.Name -like "*mysql*"}

# Ver la ruta del ejecutable
Get-WmiObject win32_service | Where-Object {$_.Name -like "*mysql*"} | Select-Object Name, DisplayName, PathName
```

## Después de resetear la contraseña

Actualiza el archivo `backend/src/main/resources/application.conf`:

```hocon
database {
    url = "jdbc:mysql://localhost:3306/miplan_db"
    driver = "com.mysql.cj.jdbc.Driver"
    user = "root"
    password = "tu_nueva_contraseña"  # La que acabas de configurar
    maxPoolSize = 10
}
```

Luego ejecuta el script de creación:

```powershell
mysql -u root -p < C:\Users\Jesus\CascadeProjects\MiPlan\database\schema.sql
# Ingresa tu nueva contraseña
```
