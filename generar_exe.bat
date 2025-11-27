@echo off
echo ========================================
echo Generando ejecutable .exe...
echo ========================================

REM Verificar que existe VideoJuego.jar
if not exist VideoJuego.jar (
    echo ERROR: No se encuentra VideoJuego.jar
    echo Ejecuta primero compilar.bat
    pause
    exit /b 1
)

REM Crear carpeta temporal para jpackage
if exist temp-jpackage rmdir /s /q temp-jpackage
mkdir temp-jpackage
copy VideoJuego.jar temp-jpackage\

REM Limpiar carpeta de salida anterior
if exist VideoJuego rmdir /s /q VideoJuego

REM Generar ejecutable con jpackage (app-image)
echo.
echo Generando ejecutable portable...
jpackage ^
    --input temp-jpackage ^
    --name VideoJuego ^
    --main-jar VideoJuego.jar ^
    --main-class Main.Main ^
    --type app-image ^
    --dest .

if %errorlevel% neq 0 (
    echo ERROR: La generacion del .exe fallo
    pause
    exit /b 1
)

REM Limpiar archivos temporales
rmdir /s /q temp-jpackage

echo.
echo ========================================
echo Ejecutable generado exitosamente!
echo Carpeta: VideoJuego\
echo Ejecutable: VideoJuego\VideoJuego.exe
echo ========================================
echo.
echo Puedes distribuir toda la carpeta VideoJuego
echo o ejecutar directamente VideoJuego\VideoJuego.exe
echo ========================================
pause