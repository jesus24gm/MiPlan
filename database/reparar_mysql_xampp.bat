@echo off
echo ========================================
echo Reparacion de MySQL en XAMPP
echo ========================================
echo.

cd /d "%~dp0"

echo IMPORTANTE: Este script intentara reparar MySQL
echo.
echo Asegurate de que MySQL este DETENIDO en XAMPP Control Panel
echo.
pause

echo.
echo Paso 1: Haciendo backup de la base de datos actual...
echo.

if not exist "C:\xampp\mysql\backup" mkdir "C:\xampp\mysql\backup"

set timestamp=%date:~-4%%date:~3,2%%date:~0,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set timestamp=%timestamp: =0%

echo Copiando archivos a backup_%timestamp%...
xcopy "C:\xampp\mysql\data" "C:\xampp\mysql\backup\data_%timestamp%\" /E /I /Y

echo.
echo Paso 2: Verificando archivos problematicos...
echo.

if exist "C:\xampp\mysql\data\ibdata1" (
    echo Encontrado ibdata1
) else (
    echo ERROR: No se encuentra ibdata1
)

if exist "C:\xampp\mysql\data\ib_logfile0" (
    echo Encontrado ib_logfile0
) else (
    echo ERROR: No se encuentra ib_logfile0
)

echo.
echo Paso 3: Eliminando archivos de log de InnoDB...
echo.

del /F /Q "C:\xampp\mysql\data\ib_logfile0" 2>nul
del /F /Q "C:\xampp\mysql\data\ib_logfile1" 2>nul
del /F /Q "C:\xampp\mysql\data\ibtmp1" 2>nul

echo Archivos de log eliminados.

echo.
echo ========================================
echo Reparacion completada
echo ========================================
echo.
echo Ahora intenta iniciar MySQL en XAMPP Control Panel
echo.
echo Si sigue sin funcionar, ejecuta: restaurar_mysql_xampp.bat
echo.

pause
