@echo off
echo ========================================
echo Compilando proyecto VideoJuego...
echo ========================================

REM Limpiar compilaciones anteriores
if exist bin rmdir /s /q bin
mkdir bin

REM 1. Compila TODO en la carpeta bin
echo.
echo [1/5] Compilando archivos Java...
dir /s /B *.java > sources.txt
javac -cp "lib/*" -d bin @sources.txt
if %errorlevel% neq 0 (
    echo ERROR: La compilacion fallo
    del sources.txt
    pause
    exit /b 1
)
del sources.txt
echo Compilacion exitosa!

REM 2. Extraer dependencias JAR en bin
echo.
echo [2/5] Extrayendo dependencias...
cd bin
for %%j in (..\lib\*.jar) do (
    echo Extrayendo %%~nxj...
    jar xf "%%j"
)
cd ..
echo Dependencias extraidas!

REM 3. Copiar recursos al bin (tiles, sprites, data)
echo.
echo [3/5] Copiando recursos...
xcopy /E /I /Y tiles bin\tiles > nul
xcopy /E /I /Y spritesjugador bin\spritesjugador > nul
xcopy /E /I /Y spritesenemigos bin\spritesenemigos > nul
xcopy /E /I /Y data bin\data > nul
echo Recursos copiados!

REM 4. Crea manifest correcto
echo.
echo [4/5] Creando manifest...
echo Main-Class: Main.Main> manifest.txt
echo.>> manifest.txt
echo Manifest creado!

REM 5. JAR desde bin (incluye todo: clases + dependencias + recursos)
echo.
echo [5/5] Creando archivo JAR...
jar cfm VideoJuego.jar manifest.txt -C bin .
if %errorlevel% neq 0 (
    echo ERROR: La creacion del JAR fallo
    pause
    exit /b 1
)
echo JAR creado exitosamente!

REM 6. Test
echo.
echo [6/5] Ejecutando VideoJuego.jar...
echo ========================================
java -jar VideoJuego.jar
if %errorlevel% neq 0 (
    echo ERROR: La ejecucion fallo
    pause
    exit /b 1
)

pause