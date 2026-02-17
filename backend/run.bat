@echo off
echo ========================================
echo Iniciando Backend MiPlan
echo ========================================
echo.

cd /d "%~dp0"

echo Verificando MySQL...
echo.

.\gradlew.bat run -Pconfig.file=src/main/resources/application.conf

pause
