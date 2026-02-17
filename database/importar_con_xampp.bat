@echo off
echo ========================================
echo Importar Base de Datos con XAMPP
echo ========================================
echo.

REM Verificar que XAMPP este instalado
if not exist "C:\xampp\mysql\bin\mysql.exe" (
    echo ERROR: XAMPP no esta instalado en C:\xampp
    echo.
    echo Por favor instala XAMPP desde:
    echo https://www.apachefriends.org/download.html
    echo.
    pause
    exit /b 1
)

echo Verificando que MySQL este corriendo...
echo.

REM Importar schema
echo Importando base de datos...
echo.

C:\xampp\mysql\bin\mysql.exe -u root < schema.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Base de datos importada exitosamente!
    echo ========================================
    echo.
    echo Puedes acceder a phpMyAdmin en:
    echo http://localhost/phpmyadmin
    echo.
    echo Credenciales de MySQL:
    echo Usuario: root
    echo Password: (vacio)
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
