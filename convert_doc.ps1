## Script para convertir documentacion_renderizado_detallado.txt a formato profesional

$rutaArchivo = "c:\Users\Moises\Documents\proyecto-objetos-videojuego\documentacion_renderizado_detallado.txt"
$rutaBackup = "c:\Users\Moises\Documents\proyecto-objetos-videojuego\documentacion_renderizado_detallado.txt.backup"

# Crear backup
Copy-Item $rutaArchivo $rutaBackup -Force

# Leer contenido
$contenido = Get-Content $rutaArchivo -Raw -Encoding UTF8

# Remover simbolos markdown
$contenido = $contenido -replace '^# (.+)$', "`r`n================================================================================`r`n`r`n`$1`r`n`r`n================================================================================`r`n"
$contenido = $contenido -replace '^## (.+)$', "`r`n`$1`r`n"
  
$contenido = $contenido -replace '^### (.+)$', '`$1'
$contenido = $contenido -replace '^---+$', '================================================================================'

# Convertir flechas Unicode  a ASCII
$contenido = $contenido -replace '▼', 'v'
$contenido = $contenido -replace '→', '->'
$contenido = $contenido -replace '←', '<-'
$contenido = $contenido -replace '↑', '^'
$contenido = $contenido -replace '↓', 'v'

# Guardar
$contenido | Out-File -FilePath $rutaArchivo -Encoding UTF8 -NoNewline

Write-Host "Conversion completada. Backup guardado en: $rutaBackup"
