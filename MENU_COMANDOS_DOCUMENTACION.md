MENU COMANDOS DEL JUEGO

DESCRIPCION GENERAL

Se ha agregado una nueva opcion al menu principal que permite a los jugadores visualizar todos los comandos disponibles en el juego. Esta funcionalidad proporciona un acceso rapido a la documentacion de controles sin necesidad de consultar manuales externos.

NAVEGACION DEL MENU

En el menu principal aparecen las siguientes opciones:

[1] Jugar Solo
[2] Crear Servidor
[3] Unirse a Servidor
[4] Comandos del Juego
[5] Salir

Al presionar la tecla 4, el jugador accede a la pantalla de Comandos del Juego. Desde esta pantalla, puede presionar ESC para volver al menu principal.

IMPLEMENTACION TECNICA

1. Estado del Juego

Se agrego un nuevo estado en GameState enum:

public enum GameState {
    MENU_PRINCIPAL,
    CREAR_SERVIDOR,
    UNIRSE_SERVIDOR,
    MENU_COMANDOS,
    JUGANDO,
    MENU_PAUSA,
    JUEGO_TERMINADO
}

2. Manejo de Entrada

En GamePanel.java, la logica de entrada del menu principal se actualizo:

case MENU_PRINCIPAL:
    if (inputService.isTecla1()) {
        estadoJuego = GameState.JUGANDO;
        inputService.setTecla1(false);
    } else if (inputService.isTecla2()) {
        estadoJuego = GameState.CREAR_SERVIDOR;
        resetearInputs();
        inputService.setTecla2(false);
    } else if (inputService.isTecla3()) {
        estadoJuego = GameState.UNIRSE_SERVIDOR;
        resetearInputs();
        inputService.setTecla3(false);
    } else if (inputService.isTecla4()) {
        estadoJuego = GameState.MENU_COMANDOS;
        inputService.setTecla4(false);
    } else if (inputService.isTecla5()) {
        System.exit(0);
    }
    break;

Cuando el jugador esta en el menu de comandos:

case MENU_COMANDOS:
    if (inputService.isTeclaEscape()) {
        estadoJuego = GameState.MENU_PRINCIPAL;
        inputService.setTeclaEscape(false);
    }
    break;

3. Renderizado

En GamePanel.java, la logica de renderizado se actualizo para mostrar el menu de comandos:

case MENU_COMANDOS:
    renderSystem.renderMenuComandos(g2d, pantallaAncho, pantallaAlto);
    break;

4. Renderizador de Menu Comandos

Se implemento el metodo renderMenuComandos() en HUDRenderer.java:

public void renderMenuComandos(Graphics2D g2, int pantallaAncho, int pantallaAlto) {
    g2.setColor(new Color(0, 0, 0, 180));
    g2.fillRect(0, 0, pantallaAncho, pantallaAlto);

    g2.setFont(fuenteTitulo);
    g2.setColor(new Color(255, 215, 0));
    String titulo = "COMANDOS DEL JUEGO";
    int anchoTitulo = g2.getFontMetrics().stringWidth(titulo);
    g2.drawString(titulo, (pantallaAncho - anchoTitulo) / 2, 50);

    g2.setFont(fuenteNormal);
    g2.setColor(Color.WHITE);
    
    String[] comandos = {
        "MOVIMIENTO:",
        "  flechas o WASD  =  Mover personaje",
        "  Q  =  Tomar una pocion",
        "",
        "COMBATE Y ACCION:",
        "  E  =  Interactuar con cofres",
        "  A-D  =  Seleccionar respuesta",
        "  ENTER  =  Confirmar respuesta",
        "",
        "POCIONES:",
        "  R  =  Usar pocion (cura 40 HP)",
        "",
        "CONTROL DE JUEGO:",
        "  P  =  Pausar_Reanudar",
        "  ESC  =  Menu de pausa",
        "  ENTER  =  Confirmar acciones",
        "",
        "EN MULTIJUGADOR:",
        "  Todos los cambios se sincronizan en tiempo real",
        "  Ver HUD para estadisticas de otros jugadores"
    };

    int y = 120;
    int lineHeight = 25;
    
    for (String comando : comandos) {
        if (comando.isEmpty()) {
            y += 10;
        } else if (comando.endsWith(":")) {
            g2.setColor(new Color(100, 200, 255));
            g2.drawString(comando, 100, y);
            g2.setColor(Color.WHITE);
            y += lineHeight;
        } else {
            g2.drawString(comando, 100, y);
            y += lineHeight;
        }
    }

    g2.setFont(fuenteNormal);
    g2.setColor(Color.YELLOW);
    String regreso = "[ESC] Volver al Menu Principal";
    int anchoRegreso = g2.getFontMetrics().stringWidth(regreso);
    g2.drawString(regreso, (pantallaAncho - anchoRegreso) / 2, pantallaAlto - 40);
}

5. Servicios de Entrada

Se agregaron metodos en InputService.java para soportar la tecla 5:

private boolean tecla5;

En keyPressed():

case KeyEvent.VK_5:
    tecla5 = true;
    break;

En keyReleased():

case KeyEvent.VK_5:
    tecla5 = false;
    break;

Metodos getter y setter:

public boolean isTecla5() {
    return tecla5;
}

public void setTecla5(boolean estado) {
    this.tecla5 = estado;
}

CONTENIDO DEL MENU

El menu de comandos muestra 5 secciones principales:

1. MOVIMIENTO
   . Teclas direccionales o WASD para mover el personaje
   . Tecla Q para tomar una pocion

2. COMBATE Y ACCION
   . Tecla E para interactuar con cofres
   . Teclas A y D para seleccionar respuestas de acertijos
   . ENTER para confirmar

3. POCIONES
   . Tecla R para usar pocion (restaura 40 HP)

4. CONTROL DE JUEGO
   . Tecla P para pausar_reanudar
   . ESC para abrir menu de pausa
   . ENTER para confirmar acciones

5. EN MULTIJUGADOR
   . Informacion sobre sincronizacion en tiempo real
   . Referencia al HUD para ver estadisticas

FLUJO DE NAVEGACION

Menu Principal
         |
      [4]
         |
Menu Comandos del Juego
         |
    [ESC]
         |
Menu Principal

ARCHIVOS MODIFICADOS

. Main_GamePanel.java
  . Agregado MENU_COMANDOS a enum GameState
  . Agregado manejo de entrada Tecla4 para navegar a MENU_COMANDOS
  . Agregado manejo de ESC en MENU_COMANDOS para volver
  . Agregado renderizado de MENU_COMANDOS en paintComponent()

. Presentation_HUDRenderer.java
  . Agregado metodo renderMenuComandos()

. Presentation_RenderSystem.java
  . Agregado metodo renderMenuComandos() que delega a HUDRenderer

. infrastructure_InputService.java
  . Agregado campo tecla5
  . Agregado manejo de KeyEvent.VK_5 en keyPressed() y keyReleased()
  . Agregado metodos isTecla5() y setTecla5()

PRUEBAS REALIZADAS

. Compilacion exitosa sin errores
. Navegacion desde menu principal a menu de comandos funciona
. ESC desde menu de comandos vuelve correctamente al menu principal
. Tecla 5 en menu principal cierra el programa
. Todos los comandos se muestran correctamente formateados
. El menu es visualmente claro y profesional

CONCLUSION

La adicion del menu de comandos proporciona una forma facil y directa para que los jugadores consulten todos los controles disponibles en el juego sin salir de la aplicacion. La implementacion se integra seamlessly con el sistema existente de estados y entrada.

Fecha: 26 de Noviembre, 2025
Version: 1.0
