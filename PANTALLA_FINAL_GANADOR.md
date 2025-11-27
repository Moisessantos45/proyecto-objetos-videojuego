PANTALLA FINAL Y DETERMINACION DE GANADOR

DESCRIPCION GENERAL

Cuando el tiempo de juego llega a cero (o se alcanza el limite), el juego muestra una pantalla final que determina el ganador basandose en criterios especificos. En multijugador, todos los jugadores ven el mismo resultado simultaneamente.

TRANSICION A PANTALLA FINAL

Cuando Termina el Tiempo

En GamePanel.java, durante la actualizacion del juego:

if (tiempoRestanteSegundos == 0 && estadoJuego == GameState.JUGANDO) {
    estadoJuego = GameState.JUEGO_TERMINADO;
}

El estado cambia de JUGANDO a JUEGO_TERMINADO.

En GameEngine.java, el timer se controla:

public void update() {
    if (tiempoRestanteSegundos > 0) {
        tiempoRestanteSegundos--;
    }
    if (tiempoRestanteSegundos == 0) {
        juegoTerminado = true;
    }
}

CRITERIOS DE GANADOR

El ganador se determina por:

1. Criterio Primario: Cantidad de Acertijos Resueltos

El jugador con MAS acertijos resueltos GANA.

Si todos resolvieron 5 acertijos cada uno (empate), se aplica:

2. Criterio Secundario: Cantidad de Vida Restante

Si empatan en acertijos, el jugador con MAS vida GANA.

ESTRUCTURA DE DATOS DE GANADOR

En HUDRenderer.java:

private static class Ganador {
    String nombre;
    int acertijos;
    int vida;
    
    Ganador(String nombre, int acertijos, int vida) {
        this.nombre = nombre;
        this.acertijos = acertijos;
        this.vida = vida;
    }
}

LOGICA DE DETERMINACION DEL GANADOR

En HUDRenderer.java, metodo renderJuegoTerminado():

public void renderJuegoTerminado(Graphics2D g2, 
    int pantallaAncho, 
    int pantallaAlto,
    JugadorSystem jugadorSystem,
    PlayerStats statsLocal,
    Map<String, RemotePlayer> remotePlayers) {
    
    Ganador ganador = null;
    int maxAcertijos = statsLocal.getAcertijos();
    int maxVida = statsLocal.getVida();
    ganador = new Ganador(
        statsLocal.getNombre(), 
        maxAcertijos, 
        maxVida
    );
    
    for (RemotePlayer remoto : remotePlayers.values()) {
        int acertijosRemoto = remoto.getStats().getAcertijos();
        int vidaRemoto = remoto.getStats().getVida();
        
        if (acertijosRemoto > maxAcertijos) {
            maxAcertijos = acertijosRemoto;
            ganador = new Ganador(
                remoto.getNombre(), 
                acertijosRemoto, 
                vidaRemoto
            );
        } else if (acertijosRemoto == maxAcertijos && 
                   vidaRemoto > ganador.vida) {
            ganador = new Ganador(
                remoto.getNombre(), 
                acertijosRemoto, 
                vidaRemoto
            );
        }
    }
    
    renderizarResultados(g2, pantallaAncho, pantallaAlto, ganador);
}

Explicacion del Algoritmo

1. Inicializa al jugador local como ganador temporal
2. Itera sobre todos los jugadores remotos
3. Si alguien tiene MAS acertijos, lo hace ganador
4. Si tiene IGUAL cantidad de acertijos pero MAS vida, lo hace ganador
5. Al final, renderiza los resultados

RENDERIZADO DE PANTALLA FINAL

En HUDRenderer.java, metodo renderizarResultados():

private void renderizarResultados(Graphics2D g2, 
    int pantallaAncho, 
    int pantallaAlto,
    Ganador ganador) {
    
    g2.setColor(new Color(0, 0, 0, 200));
    g2.fillRect(0, 0, pantallaAncho, pantallaAlto);
    
    g2.setFont(fuenteTitulo);
    g2.setColor(new Color(255, 215, 0));
    String titulo = "JUEGO TERMINADO";
    int anchoTitulo = g2.getFontMetrics().stringWidth(titulo);
    g2.drawString(titulo, (pantallaAncho - anchoTitulo) / 2, 80);
    
    g2.setFont(fuenteSubtitulo);
    g2.setColor(new Color(100, 255, 100));
    String textoGanador = "GANADOR: " + ganador.nombre;
    int anchoGanador = g2.getFontMetrics().stringWidth(textoGanador);
    g2.drawString(textoGanador, (pantallaAncho - anchoGanador) / 2, 150);
    
    g2.setFont(fuenteNormal);
    g2.setColor(Color.WHITE);
    
    String estadisticas = "Acertijos: " + ganador.acertijos + 
                          " | Vida: " + ganador.vida;
    int anchoEstadisticas = g2.getFontMetrics().stringWidth(estadisticas);
    g2.drawString(estadisticas, 
        (pantallaAncho - anchoEstadisticas) / 2, 220);
    
    g2.setColor(Color.YELLOW);
    String instruccion = "[ENTER] Jugar de Nuevo    [ESC] Menu Principal";
    int anchoInstruccion = g2.getFontMetrics().stringWidth(instruccion);
    g2.drawString(instruccion, 
        (pantallaAncho - anchoInstruccion) / 2, 
        pantallaAlto - 40);
}

ELEMENTOS DE LA PANTALLA

Fondo

Rectangulo semitransparente negro:

g2.setColor(new Color(0, 0, 0, 200));
g2.fillRect(0, 0, pantallaAncho, pantallaAlto);

Titulo

Texto dorado centrado:

Titulo: JUEGO TERMINADO

Texto Ganador

Texto verde brillante que muestra el nombre del ganador:

Texto: GANADOR: nombreJugador

Estadisticas del Ganador

Muestra acertijos y vida final:

Estadisticas: Acertijos: 7 | Vida: 65

Botones de Accion

Instruccion amarilla en la parte inferior:

Instruccion: [ENTER] Jugar de Nuevo    [ESC] Menu Principal

EJEMPLO DE PANTALLA FINAL

Supongamos dos jugadores:

Jugador 1 (Local)
. Nombre: Juan
. Acertijos: 7
. Vida: 45

Jugador 2 (Remoto)
. Nombre: Maria
. Acertijos: 7
. Vida: 80

Algoritmo de Determinacion:

1. Inicializa ganador como Juan (7 acertijos, 45 vida)
2. Compara con Maria
3. Maria tiene 7 acertijos = igual que Juan
4. Maria tiene 80 vida > 45 vida (Juan)
5. Actualiza ganador a Maria
6. Resultado: GANADOR MARIA

Pantalla Mostrada:

JUEGO TERMINADO

GANADOR: Maria

Acertijos: 7 | Vida: 80

[ENTER] Jugar de Nuevo    [ESC] Menu Principal

MANEJO DE ENTRADA EN PANTALLA FINAL

En GamePanel.java, en el metodo update():

case JUEGO_TERMINADO:
    if (inputService.isTeclaEnter()) {
        gameEngine.reiniciarJuego();
        estadoJuego = GameState.JUGANDO;
        inputService.setTeclaEnter(false);
    } else if (inputService.isTeclaEscape()) {
        gameEngine.reiniciarJuego();
        estadoJuego = GameState.MENU_PRINCIPAL;
        inputService.setTeclaEscape(false);
    }
    break;

Opciones:

. ENTER: Reinicia el juego y vuelve a JUGANDO
. ESC: Reinicia el juego y vuelve a MENU_PRINCIPAL

Importancia del reinicio:

Se debe llamar gameEngine.reiniciarJuego() para:
. Resetear temporizador a 3:00
. Limpiar enemigos
. Regenerar mapa
. Resetear vida a 100
. Resetear acertijos a 0
. Resetear pociones a 0

MULTIJUGADOR Y PANTALLA FINAL

En Juego Multijugador

Cuando termina el tiempo en multijugador:

1. Todos reciben la actualizacion del tiempo llegando a 0
2. Todos transicionan a JUEGO_TERMINADO simultaneamente
3. Cada cliente calcula quien es el ganador localmente
4. Se muestran los mismos resultados para todos (debido a que tienen el mismo estado)

En GamePanel.java, para multijugador:

if (gameClient != null && gameClient.isConectado()) {
    PlayerStats statsLocal = gameEngine.getStatsLocal();
    Map<String, RemotePlayer> remotePlayers = 
        gameEngine.getRemotePlayers();
    
    renderSystem.renderJuegoTerminado(
        g2d, 
        pantallaAncho, 
        pantallaAlto, 
        jugadorSystem, 
        statsLocal, 
        remotePlayers
    );
} else {
    renderSystem.renderJuegoTerminado(
        g2d, 
        pantallaAncho, 
        pantallaAlto, 
        jugadorSystem, 
        null, 
        null
    );
}

En Juego Local (Un Solo Jugador)

Cuando es juego local, no hay otros jugadores:

Ganador: Siempre el jugador local (por defecto)

En HUDRenderer.java, manejo para juego local:

if (remotePlayers == null || remotePlayers.isEmpty()) {
    g2.drawString("Tiempo Terminado", 
        (pantallaAncho - 100) / 2, 150);
    return;
}

TRANSICION DE ESTADO

Flujo Completo de Terminacion

Juego en Progreso
     |
  Tiempo = 0
     |
estadoJuego = JUEGO_TERMINADO
     |
Pantalla Final Mostrada
     |
Jugador Presiona ENTER o ESC
     |
gameEngine.reiniciarJuego()
     |
estadoJuego = JUGANDO o MENU_PRINCIPAL
     |
Nueva Sesion Comienza

RENDERIZADO EN SWITCH DE ESTADOS

En GamePanel.java, paintComponent():

switch (estadoJuego) {
    case MENU_PRINCIPAL:
        renderSystem.renderMenuPrincipal(g2d, pantallaAncho, pantallaAlto);
        break;
    case JUGANDO:
        renderTodo();
        break;
    case JUEGO_TERMINADO:
        renderSystem.renderJuegoTerminado(
            g2d, 
            pantallaAncho, 
            pantallaAlto,
            jugadorSystem,
            statsLocal,
            remotePlayers
        );
        break;
}

INFORMACION ADICIONAL EN PANTALLA FINAL

Podria extenderse para mostrar mas informacion:

Ranking Completo

En lugar de solo mostrar ganador, mostrar ranking:

1. Maria    - 7 Acertijos, 80 Vida
2. Juan     - 7 Acertijos, 45 Vida
3. Pedro    - 5 Acertijos, 90 Vida

Estadisticas de Sesion

. Tiempo jugado
. Enemigos derrotados
. Pociones usadas
. Distancia recorrida

Esto podria agregarse en futuras versiones.

PRUEBAS REALIZADAS

. Pantalla final aparece cuando tiempo llega a 0
. Ganador se determina correctamente por acertijos
. Empates se resuelven por vida
. Ambos jugadores ven el mismo ganador en multijugador
. ENTER reinicia correctamente
. ESC vuelve al menu
. Pantalla tiene formato claro y legible
. Colores y fuentes son consistentes
. Botones de instruccion son visibles

CONCLUSION

La pantalla final proporciona una forma clara y visual de comunicar el resultado del juego. La determinacion del ganador es transparente y basada en criterios objetivos. La integracion con el sistema multijugador permite que todos los jugadores sepan quien gano, independientemente de si jugaban juntos.

Fecha: 26 de Noviembre, 2025
Version: 1.0
