@echo off
echo ========================================
echo Subir MiPlan a GitHub
echo ========================================
echo.

cd /d "%~dp0"

echo IMPORTANTE: Asegurate de haber creado el repositorio en GitHub
echo URL: https://github.com/new
echo.
echo Ingresa tu usuario de GitHub:
set /p GITHUB_USER="Usuario: "

echo.
echo Conectando con GitHub...
git remote add origin https://github.com/%GITHUB_USER%/MiPlan.git

echo.
echo Cambiando a rama main...
git branch -M main

echo.
echo Subiendo codigo a GitHub...
echo.
echo NOTA: Te pedira credenciales:
echo - Usuario: Tu usuario de GitHub
echo - Password: Personal Access Token (NO tu contraseña normal)
echo.
echo Si no tienes token, crealo en: https://github.com/settings/tokens
echo.

git push -u origin main

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Codigo subido exitosamente a GitHub!
    echo ========================================
    echo.
    echo Repositorio: https://github.com/%GITHUB_USER%/MiPlan
    echo.
    echo SIGUIENTE PASO: Desplegar en Railway
    echo Sigue la guia: GUIA_DEPLOYMENT_RAILWAY.md
    echo O el inicio rapido: INICIO_RAPIDO_RAILWAY.md
    echo.
) else (
    echo.
    echo ERROR: No se pudo subir el codigo
    echo.
    echo Posibles causas:
    echo 1. No creaste el repositorio en GitHub
    echo 2. Usuario incorrecto
    echo 3. Credenciales incorrectas (usa Personal Access Token)
    echo.
    echo Crea un token en: https://github.com/settings/tokens
    echo Marca: repo (Full control of private repositories)
    echo.
)

pause
