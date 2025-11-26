#!/bin/bash

# Ponerle colorinchis a la salida
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "=== Compilando el Videojuego ==="

# Crear el directorio bin si no existe
mkdir -p bin

# Limpiar compilaciones anteriores para evitar conflictos
rm -rf bin/*

# Encontrar todos los archivos .java y compilarlos
find . -name "*.java" > sources.txt
javac -cp "lib/*" -d bin @sources.txt

# Verificar si la compilación fue exitosa
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Compilación exitosa.${NC}"
    echo ""
    echo "=== Ejecutando el Videojuego ==="
    # Ejecutar el juego
    java -cp "bin:lib/*" Main.Main
else
    echo -e "${RED}❌ Error en la compilación.${NC}"
fi

# Limpiar el archivo de fuentes
rm sources.txt