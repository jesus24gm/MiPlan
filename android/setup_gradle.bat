@echo off
echo ========================================
echo Configurando Gradle Wrapper
echo ========================================
echo.

cd /d "%~dp0"

echo Descargando Gradle Wrapper JAR...
echo.

powershell -Command "& {Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar'}"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Gradle Wrapper configurado exitosamente!
    echo ========================================
    echo.
    echo Ahora puedes:
    echo 1. Abrir el proyecto en Android Studio
    echo 2. O ejecutar: gradlew.bat build
    echo.
) else (
    echo.
    echo ERROR: No se pudo descargar Gradle Wrapper
    echo.
)

pause
