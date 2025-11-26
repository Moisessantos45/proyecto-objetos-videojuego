# 🎮 Implementación del Temporizador de 3 Minutos

## ✅ Estado: COMPLETADO

## 📋 Resumen

Se implementó exitosamente un sistema de temporizador de 3 minutos que termina el juego automáticamente y muestra las estadísticas del jugador cuando el tiempo llega a 0.

## 🎯 Características Principales

### Durante el Juego
- ⏱️ Timer visible en la parte superior de la pantalla
- 🟡 Color amarillo cuando quedan ≥ 30 segundos
- 🔴 Color rojo cuando quedan < 30 segundos (alerta visual)
- 📊 Formato: Tiempo: M:SS (ej: 3:00, 2:15, 0:29)

### Al Terminar el Tiempo
Pantalla final con:
- ❤️ Vida final del personaje
- 🧪 Pociones en arsenal
- 🎯 Acertijos resueltos

### Opciones Disponibles
- ENTER → Reiniciar el juego (resetea todo a valores iniciales)
- ESC → Volver al menú principal

## 📁 Archivos Modificados

### 1. Main/GamePanel.java
java
// Nuevo estado agregado
public enum GameState {
    // ... estados existentes ...
    JUEGO_TERMINADO  // ← NUEVO
}


Cambios:
- Agregado estado JUEGO_TERMINADO
- Detección de fin de tiempo en el loop de actualización
- Manejo de inputs en pantalla final
- Renderizado de pantalla final

### 2. domain/GameEngine.java
java
// Nuevas variables
private static final long TIEMPO_LIMITE_MS = 3  60  1000; // 3 minutos
private long tiempoInicioJuego;
private long tiempoTranscurrido;
private boolean juegoTerminado;


Cambios:
- Inicialización del timer al empezar el juego
- Actualización continua del tiempo transcurrido
- Detección automática cuando se agota el tiempo
- Métodos getter para obtener tiempo restante
- Reinicio del timer al reiniciar el juego

### 3. Presentation/HUDRenderer.java
java
// Timer durante el juego
public void render(..., int tiempoRestanteSegundos) {
    int minutos = tiempoRestanteSegundos / 60;
    int segundos = tiempoRestanteSegundos % 60;
    String textoTimer = String.format("Tiempo: %d:%02d", minutos, segundos);
    Color colorTimer = tiempoRestanteSegundos < 30 ? Color.RED : Color.YELLOW;
    // ... dibuja el timer ...
}


Cambios:
- Renderizado del timer con cambio de color dinámico
- Nueva pantalla de "Juego Terminado" con estadísticas
- Diseño visual atractivo con colores diferenciados

### 4. Presentation/RenderSystem.java
Cambios:
- Actualizado renderTodo() para incluir parámetro del timer
- Agregado método renderJuegoTerminado()

## 🔄 Flujo de Ejecución


┌─────────────────────────────────────────────────────────┐
│ 1. INICIO DEL JUEGO                                     │
│    - Usuario selecciona "Jugar Solo" 1               │
│    - Timer inicia en 3:00                               │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│ 2. DURANTE EL JUEGO (cada frame)                        │
│    - Timer cuenta regresivamente                        │
│    - Tiempo: 3:00 → 2:59 → 2:58 ... → 0:00            │
│    - Color cambia a ROJO en 0:29                        │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│ 3. TIEMPO TERMINADO (0:00)                              │
│    - Juego se detiene automáticamente                   │
│    - Aparece pantalla de "¡TIEMPO TERMINADO!"           │
│    - Muestra estadísticas:                              │
│      • Vida final                                       │
│      • Pociones recolectadas                            │
│      • Acertijos resueltos                              │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│ 4. OPCIONES FINALES                                     │
│    ENTER → Reiniciar (todo vuelve a inicial)          │
│    ESC → Menú Principal                               │
└─────────────────────────────────────────────────────────┘


## 🎨 Visualización del Timer

### En Pantalla Durante el Juego

┌───────────────────────────────────────────────────┐
│          ████████████ (Barra de Vida)             │
│                                                   │
│            Tiempo: 2:45 AMARILLO                │
│                                                   │
│  Contenido del juego - personaje, mapa, etc    │
└───────────────────────────────────────────────────┘


### Alerta (< 30 segundos)

┌───────────────────────────────────────────────────┐
│          ████████████ (Barra de Vida)             │
│                                                   │
│            Tiempo: 0:15 ROJO  ⚠️               │
│                                                   │
│  Contenido del juego - personaje, mapa, etc    │
└───────────────────────────────────────────────────┘


### Pantalla Final

┌───────────────────────────────────────────────────┐
│                                                   │
│         ¡TIEMPO TERMINADO! ROJO GRANDE          │
│                                                   │
│         Estadísticas Finales AMARILLO           │
│                                                   │
│         Vida final: 65 HP BLANCO                │
│         Pociones en arsenal: 5 CYAN             │
│         Acertijos resueltos: 3 VERDE            │
│                                                   │
│         ENTER Jugar de nuevo AMARILLO         │
│         ESC Menú Principal ROJO               │
│                                                   │
└───────────────────────────────────────────────────┘


## ⚙️ Configuración

### Cambiar el Tiempo Límite
Editar en domain/GameEngine.java:
java
// Cambiar 3 por el número de minutos deseado
private static final long TIEMPO_LIMITE_MS = 3  60  1000;

// Ejemplos:
// 1 minuto:  1  60  1000
// 5 minutos: 5  60  1000
// 10 minutos: 10  60  1000


### Cambiar el Umbral de Alerta Roja
Editar en Presentation/HUDRenderer.java:
java
// Cambiar 30 por los segundos deseados
Color colorTimer = tiempoRestanteSegundos < 30 ? Color.RED : Color.YELLOW;


## 🧪 Pruebas Realizadas

✅ Compilación exitosa sin errores  
✅ Timer se muestra correctamente en pantalla  
✅ Timer cuenta regresivamente desde 3:00  
✅ Cambio de color a rojo funciona correctamente  
✅ Juego termina al llegar a 0:00  
✅ Pantalla final muestra estadísticas correctas  
✅ Botón ENTER reinicia el juego  
✅ Botón ESC vuelve al menú principal  
✅ Timer se resetea correctamente al reiniciar  

## 🚀 Cómo Probar

1. Compilar el proyecto:
   bash
   cd /home/moy45/Proyecto_Objetos/VideoJuego_v2
   javac -d bin -cp "lib/" $(find . -name ".java")
   

2. Ejecutar el juego:
   bash
   cd bin
   java -cp ".:../lib/" Main.Main
   

3. Probar el timer:
   - Seleccionar opción 1 Jugar Solo
   - Observar el timer en la parte superior
   - Esperar a que llegue a 0:29 (se vuelve rojo)
   - Esperar a que llegue a 0:00 (aparece pantalla final)
   - Probar las opciones ENTER y ESC

### Prueba Rápida (10 segundos)
Para probar sin esperar 3 minutos, modificar temporalmente en GameEngine.java:
java
private static final long TIEMPO_LIMITE_MS = 10  1000; // 10 segundos


## 📚 Documentación Adicional

Para más detalles técnicos, consultar:
- documentacion_timer_3_minutos.txt - Documentación completa y detallada

## 💡 Mejoras Futuras Sugeridas

-  Sistema de bonificación por tiempo restante
-  Tabla de récords (mejores tiempos)
-  Power-ups que añaden tiempo extra
-  Sonidos de alerta en los últimos segundos
-  Animaciones del timer (pulsar, parpadear)
-  Diferentes niveles de dificultad (tiempos variables)
-  Modo contrarreloj con objetivos
-  Estadísticas acumuladas entre partidas

## 🎯 Resultado Final

El sistema de temporizador se ha implementado exitosamente, agregando un elemento de urgencia y desafío al juego. El jugador ahora debe explorar el mapa y resolver acertijos bajo la presión del tiempo, lo que hace el juego más dinámico e interesante.

---
Autor: Implementación de Sistema de Timer  
Fecha: 10 de Noviembre, 2024  
Versión: 1.0