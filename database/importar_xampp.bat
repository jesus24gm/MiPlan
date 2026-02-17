@echo off
echo ========================================
echo Importando Base de Datos MiPlan
echo ========================================
echo.

cd /d "%~dp0"

echo Importando schema.sql...
echo.

C:\xampp\mysql\bin\mysql.exe -u root < schema.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Base de datos importada exitosamente!
    echo ========================================
    echo.
    echo Puedes verificar en phpMyAdmin:
    echo http://localhost/phpmyadmin
    echo.
    echo Base de datos: miplan_db
    echo.
    echo Usuario Admin de MiPlan:
    echo Email: admin@miplan.com
    echo Password: admin123
    echo.
) else (
    echo.
    echo ERROR: No se pudo importar la base de datos
    echo.
    echo Verifica que:
    echo 1. XAMPP Control Panel este abierto
    echo 2. MySQL este corriendo (verde)
    echo 3. Intenta de nuevo
    echo.
)

pause
