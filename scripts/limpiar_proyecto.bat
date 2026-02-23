@echo off
echo ========================================
echo Limpiando proyecto MiPlan Android
echo ========================================
echo.

set PROYECTO=C:\Users\Jesus\AndroidStudioProjects\MiPlan

echo [1/4] Eliminando carpeta build...
if exist "%PROYECTO%\build" (
    rmdir /s /q "%PROYECTO%\build"
    echo     ✓ Carpeta build eliminada
) else (
    echo     - No existe carpeta build
)

echo.
echo [2/4] Eliminando carpeta app\build...
if exist "%PROYECTO%\app\build" (
    rmdir /s /q "%PROYECTO%\app\build"
    echo     ✓ Carpeta app\build eliminada
) else (
    echo     - No existe carpeta app\build
)

echo.
echo [3/4] Eliminando carpeta .gradle...
if exist "%PROYECTO%\.gradle" (
    rmdir /s /q "%PROYECTO%\.gradle"
    echo     ✓ Carpeta .gradle eliminada
) else (
    echo     - No existe carpeta .gradle
)

echo.
echo [4/4] Eliminando carpeta .kotlin...
if exist "%PROYECTO%\.kotlin" (
    rmdir /s /q "%PROYECTO%\.kotlin"
    echo     ✓ Carpeta .kotlin eliminada
) else (
    echo     - No existe carpeta .kotlin
)

echo.
echo ========================================
echo Limpieza completada!
echo ========================================
echo.
echo Espacio liberado: ~500MB - 2GB
echo.
echo NOTA: La próxima compilación tomará más tiempo
echo porque Gradle reconstruirá todo desde cero.
echo.
pause
