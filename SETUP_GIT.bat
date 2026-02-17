@echo off
echo ========================================
echo Configurar Git para MiPlan
echo ========================================
echo.

cd /d "%~dp0"

echo Paso 1: Configurar Git (si es primera vez)
echo.
echo Ingresa tu nombre para Git:
set /p GIT_NAME="Nombre: "

echo.
echo Ingresa tu email de GitHub:
set /p GIT_EMAIL="Email: "

git config --global user.name "%GIT_NAME%"
git config --global user.email "%GIT_EMAIL%"

echo.
echo Configuracion guardada:
git config --global user.name
git config --global user.email

echo.
echo Paso 2: Inicializar repositorio Git
echo.

git init

echo.
echo Paso 3: Anadir archivos
echo.

git add .

echo.
echo Paso 4: Hacer commit inicial
echo.

git commit -m "Initial commit: MiPlan app with Ktor backend"

echo.
echo ========================================
echo Git configurado correctamente
echo ========================================
echo.
echo SIGUIENTE PASO:
echo.
echo 1. Crea un repositorio en GitHub: https://github.com/new
echo 2. Copia la URL del repositorio
echo 3. Ejecuta estos comandos:
echo.
echo    git remote add origin https://github.com/TU_USUARIO/MiPlan.git
echo    git branch -M main
echo    git push -u origin main
echo.
echo 4. Sigue la guia: GUIA_DEPLOYMENT_RAILWAY.md
echo.

pause
