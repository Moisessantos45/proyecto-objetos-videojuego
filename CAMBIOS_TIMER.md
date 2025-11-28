================================================================================

IMPLEMENTACION DEL TEMPORIZADOR DE 3 MINUTOS

================================================================================


ESTADO: COMPLETADO


1. RESUMEN

Se implemento exitosamente un sistema de temporizador de 3 minutos que termina
el juego automaticamente y muestra las estadisticas del jugador cuando el 
tiempo llega a 0.


2. CARACTERISTICAS PRINCIPALES

2.1 Durante el Juego

    - Timer visible en la parte superior de la pantalla
    - Color amarillo cuando quedan 30 segundos o mas
    - Color rojo cuando quedan menos de 30 segundos (alerta visual)
    - Formato: Tiempo: M:SS (ejemplos: 3:00, 2:15, 0:29)

2.2 Al Terminar el Tiempo

Pantalla final con:
    - Vida final del personaje
    - Pociones en arsenal  
    - Acertijos resueltos

2.3 Opciones Disponibles

    - ENTER - Reiniciar el juego (resetea todo a valores iniciales)
    - ESC - Volver al menu principal


3. ARCHIVOS MODIFICADOS

3.1 Main/GamePanel.java

Codigo agregado:

    // Nuevo estado agregado
    public enum GameState {
        // ... estados existentes ...
        JUEGO_TERMINADO  // Nuevo estado
    }

Cambios:
    - Agregado estado JUEGO_TERMINADO
    - Deteccion de fin de tiempo en el loop de actualizacion
    - Manejo de inputs en pantalla final
    - Renderizado de pantalla final


3.2 domain/GameEngine.java

Codigo agregado:

    // Nuevas variables
    private static final long TIEMPO_LIMITE_MS = 3 * 60 * 1000; // 3 minutos
    private long tiempoInicioJuego;
    private long tiempoTranscurrido;
    private boolean juegoTerminado;

Cambios:
    - Inicializacion del timer al empezar el juego
    - Actualizacion continua del tiempo transcurrido
    - Deteccion automatica cuando se agota el tiempo
    - Metodos getter para obtener tiempo restante
    - Reinicio del timer al reiniciar el juego


3.3 Presentation/HUDRenderer.java

Codigo agregado:

    // Timer durante el juego
    public void render(..., int tiempoRestanteSegundos) {
        int minutos = tiempoRestanteSegundos / 60;
        int segundos = tiempoRestanteSegundos % 60;
        String textoTimer = String.format("Tiempo: %d:%02d", minutos, segundos);
        Color colorTimer = tiempoRestanteSegundos < 30 ? Color.RED : Color.YELLOW;
        // ... dibuja el timer ...
    }

Cambios:
    - Renderizado del timer con cambio de color dinamico
    - Nueva pantalla de "Juego Terminado" con estadisticas
    - Diseno visual atractivo con colores diferenciados


3.4 Presentation/RenderSystem.java

Cambios:
    - Actualizado renderTodo() para incluir parametro del timer
    - Agregado metodo renderJuegoTerminado()


4. FLUJO DE EJECUCION

Secuencia de eventos:

    1. INICIO DEL JUEGO
       - Usuario selecciona "Jugar Solo" (opcion 1)
       - Timer inicia en 3:00

    2. DURANTE EL JUEGO (cada frame)
       - Timer cuenta regresivamente
       - Tiempo: 3:00, 2:59, 2:58 ... hasta 0:00
       - Color cambia a ROJO cuando llega a 0:29

    3. TIEMPO TERMINADO (0:00)
       - Juego se detiene automaticamente
       - Aparece pantalla de "TIEMPO TERMINADO"
       - Muestra estadisticas:
         . Vida final
         . Pociones recolectadas
         . Acertijos resueltos

    4. OPCIONES FINALES  
       - ENTER - Reiniciar (todo vuelve a inicial)
       - ESC - Menu Principal


5. VISUALIZACION DEL TIMER

5.1 En Pantalla Durante el Juego

    ===================================================
    Barra de Vida (representacion visual)
    
    Tiempo: 2:45 (AMARILLO)
    
    Contenido del juego - personaje, mapa, etc
    ===================================================


5.2 Alerta (Menos de 30 segundos)

    ===================================================
    Barra de Vida (representacion visual)
    
    Tiempo: 0:15 (ROJO - Alerta)
    
    Contenido del juego - personaje, mapa, etc
    ===================================================


5.3 Pantalla Final

    ===================================================
    
    TIEMPO TERMINADO (ROJO - Texto Grande)
    
    Estadisticas Finales (AMARILLO)
    
    Vida final: 65 HP (BLANCO)
    Pociones en arsenal: 5 (CYAN)
    Acertijos resueltos: 3 (VERDE)
    
    ENTER - Jugar de nuevo (AMARILLO)
    ESC - Menu Principal (ROJO)
    
    ===================================================


6. CONFIGURACION

6.1 Cambiar el Tiempo Limite

Editar en domain/GameEngine.java:

    // Cambiar 3 por el numero de minutos deseado
    private static final long TIEMPO_LIMITE_MS = 3 * 60 * 1000;
    
    // Ejemplos:
    // 1 minuto:   1 * 60 * 1000
    // 5 minutos:  5 * 60 * 1000
    // 10 minutos: 10 * 60 * 1000


6.2 Cambiar el Umbral de Alerta Roja

Editar en Presentation/HUDRenderer.java:

    // Cambiar 30 por los segundos deseados
    Color colorTimer = tiempoRestanteSegundos < 30 ? Color.RED : Color.YELLOW;


7. PRUEBAS REALIZADAS

Estado de verificacion:

    - Compilacion exitosa sin errores
    - Timer se muestra correctamente en pantalla
    - Timer cuenta regresivamente desde 3:00
    - Cambio de color a rojo funciona correctamente
    - Juego termina al llegar a 0:00
    - Pantalla final muestra estadisticas correctas
    - Boton ENTER reinicia el juego
    - Boton ESC vuelve al menu principal
    - Timer se resetea correctamente al reiniciar


8. COMO PROBAR

8.1 Compilar el Proyecto

Desde el directorio raiz:

    cd /home/moy45/Proyecto_Objetos/VideoJuego_v2
    javac -d bin -cp "lib/*" $(find . -name "*.java")


8.2 Ejecutar el Juego

    cd bin
    java -cp ".:../lib/*" Main.Main


8.3 Probar el Timer

Pasos:
    1. Seleccionar opcion 1 - Jugar Solo
    2. Observar el timer en la parte superior
    3. Esperar a que llegue a 0:29 (se vuelve rojo)
    4. Esperar a que llegue a 0:00 (aparece pantalla final)
    5. Probar las opciones ENTER y ESC


8.4 Prueba Rapida (10 segundos)

Para probar sin esperar 3 minutos, modificar temporalmente en GameEngine.java:

    private static final long TIEMPO_LIMITE_MS = 10 * 1000; // 10 segundos


9. DOCUMENTACION ADICIONAL

Para mas detalles tecnicos, consultar:
    - documentacion_timer_3_minutos.txt - Documentacion completa y detallada


10. MEJORAS FUTURAS SUGERIDAS

Posibles extensiones del sistema:

    - Sistema de bonificacion por tiempo restante
    - Tabla de records (mejores tiempos)
    - Power-ups que anaden tiempo extra
    - Sonidos de alerta en los ultimos segundos
    - Animaciones del timer (pulsar, parpadear)
    - Diferentes niveles de dificultad (tiempos variables)
    - Modo contrarreloj con objetivos
    - Estadisticas acumuladas entre partidas


11. RESULTADO FINAL

El sistema de temporizador se ha implementado exitosamente, agregando un 
elemento de urgencia y desafio al juego. El jugador ahora debe explorar el 
mapa y resolver acertijos bajo la presion del tiempo, lo que hace el juego 
mas dinamico e interesante.


================================================================================

Autor: Implementacion de Sistema de Timer
Fecha: 10 de Noviembre, 2024
Version: 1.0

================================================================================