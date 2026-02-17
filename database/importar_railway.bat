@echo off
echo ========================================
echo Importar Base de Datos a Railway
echo ========================================
echo.

cd /d "%~dp0"

echo IMPORTANTE: Necesitas las credenciales de MySQL de Railway
echo.
echo Para obtenerlas:
echo 1. Ve a Railway Dashboard
echo 2. Click en el servicio MySQL
echo 3. Ve a la pestana "Variables"
echo 4. Copia los valores de:
echo    - MYSQLHOST
echo    - MYSQLPORT
echo    - MYSQLDATABASE
echo    - MYSQLUSER
echo    - MYSQLPASSWORD
echo.
pause

echo.
echo Ingresa las credenciales de Railway:
echo.

set /p MYSQL_HOST="MYSQLHOST: "
set /p MYSQL_PORT="MYSQLPORT: "
set /p MYSQL_DATABASE="MYSQLDATABASE: "
set /p MYSQL_USER="MYSQLUSER: "
set /p MYSQL_PASSWORD="MYSQLPASSWORD: "

echo.
echo Conectando a MySQL de Railway...
echo.

C:\xampp\mysql\bin\mysql.exe -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER% -p%MYSQL_PASSWORD% %MYSQL_DATABASE% < schema.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Base de datos importada exitosamente!
    echo ========================================
    echo.
) else (
    echo.
    echo ERROR: No se pudo importar la base de datos
    echo.
    echo Verifica:
    echo 1. Las credenciales son correctas
    echo 2. MySQL de Railway esta corriendo
    echo 3. Tienes conexion a internet
    echo.
)

pause
