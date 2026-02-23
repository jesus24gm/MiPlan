@echo off
echo ========================================
echo Copiando codigo de MiPlan
echo ========================================
echo.

set ORIGEN=C:\Users\Jesus\CascadeProjects\MiPlan\android\app\src\main\java\com\miplan
set DESTINO=C:\Users\Jesus\AndroidStudioProjects\MiPlan\app\src\main\java\com\miplan

echo Creando estructura de carpetas...
mkdir "%DESTINO%" 2>nul
mkdir "%DESTINO%\data" 2>nul
mkdir "%DESTINO%\data\local" 2>nul
mkdir "%DESTINO%\data\remote" 2>nul
mkdir "%DESTINO%\data\remote\dto" 2>nul
mkdir "%DESTINO%\data\remote\dto\request" 2>nul
mkdir "%DESTINO%\data\remote\dto\response" 2>nul
mkdir "%DESTINO%\data\repository" 2>nul
mkdir "%DESTINO%\domain" 2>nul
mkdir "%DESTINO%\domain\model" 2>nul
mkdir "%DESTINO%\domain\repository" 2>nul
mkdir "%DESTINO%\ui" 2>nul
mkdir "%DESTINO%\ui\screens" 2>nul
mkdir "%DESTINO%\ui\screens\auth" 2>nul
mkdir "%DESTINO%\ui\screens\home" 2>nul
mkdir "%DESTINO%\ui\theme" 2>nul
mkdir "%DESTINO%\ui\navigation" 2>nul
mkdir "%DESTINO%\viewmodel" 2>nul
mkdir "%DESTINO%\di" 2>nul

echo.
echo Copiando archivos...
xcopy "%ORIGEN%\*" "%DESTINO%\" /E /I /Y /Q

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Codigo copiado exitosamente!
    echo ========================================
    echo.
    echo Ahora necesitas:
    echo 1. Actualizar build.gradle.kts
    echo 2. Actualizar AndroidManifest.xml
    echo 3. Copiar recursos (res/)
    echo.
    echo Ver: INSTRUCCIONES_MIGRACION.md
    echo.
) else (
    echo.
    echo ERROR: No se pudo copiar el codigo
    echo Verifica que la ruta de origen existe
    echo.
)

pause
