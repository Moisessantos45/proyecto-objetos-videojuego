@echo off
REM Script para compilar y ejecutar el videojuego en Windows

echo === Compilando el Videojuego ===
echo.

REM Crear el directorio bin si no existe
if not exist bin mkdir bin

REM Limpiar compilaciones anteriores para evitar conflictos
del /Q bin\* 2>nul

REM Encontrar todos los archivos .java y compilarlos
dir /s /B *.java > sources.txt
javac -encoding UTF-8 -cp "lib/*" -d bin @sources.txt

REM Verificar si la compilación fue exitosa
if %ERRORLEVEL% EQU 0 (
    echo [32m✅ Compilacion exitosa.[0m
    echo.
    echo === Ejecutando el Videojuego ===
    REM Ejecutar el juego (en Windows usamos ; en lugar de :)
    java -cp "bin;lib/*" Main.Main
) else (
    echo [31m❌ Error en la compilacion.[0m
)

REM Limpiar el archivo de fuentes
del sources.txt 2>nul
