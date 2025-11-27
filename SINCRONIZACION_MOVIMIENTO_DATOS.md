SINCRONIZACION DE MOVIMIENTO Y DATOS DEL JUGADOR

DESCRIPCION GENERAL

En multijugador es esencial que todos los jugadores vean los movimientos y cambios de estado de todos los demas. Este documento explica como se sincronizan tres aspectos criticos: movimientos de jugadores, estadisticas de vida y acertijos resueltos.

COMPONENTES PRINCIPALES

1. Protocolo de Mensajes POS

Transmite posicion y direccion del jugador cada frame.

2. Protocolo de Mensajes VIDA

Transmite la cantidad de vida cuando cambia.

3. Protocolo de Mensajes ACERTIJOS

Transmite la cantidad de acertijos resueltos.

4. Sistema de Cacheo

Evita enviar datos que no han cambiado.

SINCRONIZACION DE MOVIMIENTO

Protocolo POS

Formato: POS:x,y,direccion,nombreJugador

Ejemplo: POS:5050,5100,DOWN,Juan

Componentes:
. x, y: Posicion mundial del jugador
. direccion: DOWN, UP, LEFT, RIGHT (direccion hacia la que mira)
. nombreJugador: Identificador unico del jugador

Envio del Movimiento

En GamePanel.java, cada frame durante el juego:

private void updateMultiplayer() {
    if (gameClient != null && gameClient.isConectado()) {
        int x = jugadorSystem.getJugador().getMundoX();
        int y = jugadorSystem.getJugador().getMundoY();
        String dir = jugadorSystem.getJugador().getDireccion().toString();
        
        gameClient.sendPosition(x, y, dir, nombreJugador);
    }
}

Este metodo se llama en cada frame de actualizacion.

En GameClient.java:

public void sendPosition(int x, int y, String direccion, String nombreJugador) {
    if (writer != null) {
        String mensaje = "POS:" + x + "," + y + "," + direccion + "," + nombreJugador;
        writer.println(mensaje);
    }
}

Retransmision del Servidor

En ClientHandler.java, cuando se recibe un mensaje POS:

private void procesarMensaje(String mensaje) {
    if (mensaje.startsWith("POS:")) {
        transmitirATodos(mensaje);
    }
}

private void transmitirATodos(String mensaje) {
    for (ClientHandler cliente : todosLosClientes.values()) {
        cliente.enviarMensaje(mensaje);
    }
}

El servidor retransmite a TODOS los clientes conectados (incluido el que envio).

Recepcion en Clientes

En GameClient.java, en el thread de lectura:

private void leerMensajes() {
    String linea;
    while ((linea = reader.readLine()) != null) {
        if (linea.startsWith("POS:")) {
            String[] partes = linea.substring(4).split(",");
            int x = Integer.parseInt(partes[0]);
            int y = Integer.parseInt(partes[1]);
            String direccion = partes[2];
            String nombre = partes[3];
            
            if (!nombre.equals(nombreLocal)) {
                RemotePlayer remoto = remotePlayers.get(nombre);
                if (remoto == null) {
                    remoto = new RemotePlayer(nombre);
                    remotePlayers.put(nombre, remoto);
                }
                remoto.actualizarPosicion(x, y, direccion);
            }
        }
    }
}

Lo importante:
. Se ignora el propio mensaje (if !nombre.equals(nombreLocal))
. Se obtiene o crea el RemotePlayer si no existe
. Se actualiza su posicion

Renderizado de Jugadores Remotos

En RenderSystem.java, durante el renderizado:

for (RemotePlayer remote : remotePlayers.values()) {
    EntidadModel entidad = remote.getEntidad();
    int screenX = entidad.getMundoX() - camara.getCamaraX();
    int screenY = entidad.getMundoY() - camara.getCamaraY();
    
    if (screenX > -tamanioTile && screenX < pantallaAncho &&
        screenY > -tamanioTile && screenY < pantallaAlto) {
        entidadRenderer.render(g2, entidad, screenX, screenY);
    }
}

Resultado final: Ves otros jugadores moverse en tiempo real.

SINCRONIZACION DE VIDA

Protocolo VIDA

Formato: VIDA:cantidad,nombreJugador

Ejemplo: VIDA:85,Maria

Componentes:
. cantidad: Puntos de vida actuales (0-100)
. nombreJugador: Jugador cuya vida cambio

Cuando se Envia

En GameEngine.java, en el metodo update():

if (vidaActual != lastVidaSent) {
    gameClient.sendVida(vidaActual, nombreJugador);
    lastVidaSent = vidaActual;
}

Se envia SOLO cuando cambia. No se envia cada frame si no cambio.

En GameClient.java:

public void sendVida(int vida, String nombreJugador) {
    if (writer != null) {
        String mensaje = "VIDA:" + vida + "," + nombreJugador;
        writer.println(mensaje);
    }
}

Retransmision en Servidor

En ClientHandler.java:

if (mensaje.startsWith("VIDA:")) {
    transmitirATodos(mensaje);
}

Se retransmite a todos sin modificacion.

Recepcion en Clientes

En GameClient.java:

if (linea.startsWith("VIDA:")) {
    String[] partes = linea.substring(5).split(",");
    int vida = Integer.parseInt(partes[0]);
    String nombre = partes[1];
    
    if (nombre.equals(nombreLocal)) {
        statsLocal.setVida(vida);
    } else {
        RemotePlayer remoto = remotePlayers.get(nombre);
        if (remoto != null) {
            remoto.getStats().setVida(vida);
        }
    }
}

Logica:
. Si es el propio jugador, actualiza estadisticas locales
. Si es otro jugador, actualiza su RemotePlayer

Almacenamiento de Estadisticas

En PlayerStats.java:

public class PlayerStats {
    private String nombre;
    private int vida;
    private int vidaMaxima;
    private int acertijos;
    private int pociones;
    
    public void setVida(int vida) {
        if (vida >= 0 && vida <= vidaMaxima) {
            this.vida = vida;
        }
    }
    
    public int getVida() {
        return vida;
    }
}

Cada jugador remoto tiene su propia instancia de PlayerStats.

Renderizado de Vida en HUD

En HUDRenderer.java:

public void renderHUDVida(Graphics2D g2, PlayerStats stats) {
    String texto = "VIDA: " + stats.getVida() + "/" + stats.getVidaMaxima();
    g2.drawString(texto, 10, 30);
}

En MultiplayerHUDRenderer.java, se renderizan las vidas de todos:

public void renderJugadoresRemotos(Graphics2D g2, 
    Map<String, RemotePlayer> remotePlayers) {
    int y = 100;
    for (RemotePlayer remoto : remotePlayers.values()) {
        String nombre = remoto.getNombre();
        int vida = remoto.getStats().getVida();
        String texto = nombre + ": " + vida + " HP";
        g2.drawString(texto, 10, y);
        y += 25;
    }
}

Resultado: Ves la vida de todos en tiempo real.

SINCRONIZACION DE ACERTIJOS

Protocolo ACERTIJOS

Formato: ACERTIJOS:cantidad,nombreJugador

Ejemplo: ACERTIJOS:7,Pedro

Componentes:
. cantidad: Cantidad total de acertijos resueltos
. nombreJugador: Jugador que resolvio el acertijo

Cuando se Envia

En GameEngine.java, en update():

int acertijosActuales = statsLocal.getAcertijos();
if (acertijosActuales != lastAcertijosCount) {
    gameClient.sendAcertijos(acertijosActuales, nombreJugador);
    lastAcertijosCount = acertijosActuales;
}

Se envia cuando cambia la cantidad.

En GameClient.java:

public void sendAcertijos(int cantidad, String nombreJugador) {
    if (writer != null) {
        String mensaje = "ACERTIJOS:" + cantidad + "," + nombreJugador;
        writer.println(mensaje);
    }
}

Retransmision en Servidor

En ClientHandler.java:

if (mensaje.startsWith("ACERTIJOS:")) {
    transmitirATodos(mensaje);
}

Recepcion en Clientes

En GameClient.java:

if (linea.startsWith("ACERTIJOS:")) {
    String[] partes = linea.substring(10).split(",");
    int cantidad = Integer.parseInt(partes[0]);
    String nombre = partes[1];
    
    if (nombre.equals(nombreLocal)) {
        statsLocal.setAcertijos(cantidad);
    } else {
        RemotePlayer remoto = remotePlayers.get(nombre);
        if (remoto != null) {
            remoto.getStats().setAcertijos(cantidad);
        }
    }
}

Almacenamiento

En PlayerStats.java:

private int acertijos;

public void setAcertijos(int cantidad) {
    this.acertijos = cantidad;
}

public int getAcertijos() {
    return acertijos;
}

Renderizado en HUD

En MultiplayerHUDRenderer.java:

public void renderRanking(Graphics2D g2, 
    PlayerStats statsLocal, 
    Map<String, RemotePlayer> remotePlayers) {
    
    g2.drawString("RANKING DE ACERTIJOS", 10, 60);
    
    g2.drawString(statsLocal.getNombre() + ": " + 
        statsLocal.getAcertijos(), 10, 90);
    
    int y = 120;
    for (RemotePlayer remoto : remotePlayers.values()) {
        g2.drawString(remoto.getNombre() + ": " + 
            remoto.getStats().getAcertijos(), 10, y);
        y += 25;
    }
}

ESTRUCTURA DE REMOTEPLAYERCLASS

La clase RemotePlayer mantiene toda la informacion de un jugador remoto:

public class RemotePlayer {
    private String nombre;
    private EntidadModel entidad;
    private PlayerStats stats;
    
    public RemotePlayer(String nombre) {
        this.nombre = nombre;
        this.entidad = new EntidadModel(5000, 5000);
        this.stats = new PlayerStats(nombre);
    }
    
    public void actualizarPosicion(int x, int y, String direccion) {
        entidad.setMundoX(x);
        entidad.setMundoY(y);
        entidad.setDireccion(Direccion.valueOf(direccion));
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public EntidadModel getEntidad() {
        return entidad;
    }
    
    public PlayerStats getStats() {
        return stats;
    }
}

INTEGRACION EN GAMEENGINE

En GameEngine.java se coordina toda la sincronizacion:

public void updateMultiplayer() {
    if (gameClient == null || !gameClient.isConectado()) {
        return;
    }
    
    PlayerStats statsLocal = this.statsLocal;
    int vidaActual = statsLocal.getVida();
    
    if (vidaActual != lastVidaSent) {
        gameClient.sendVida(vidaActual, nombreJugador);
        lastVidaSent = vidaActual;
    }
    
    int acertijosActuales = statsLocal.getAcertijos();
    if (acertijosActuales != lastAcertijosCount) {
        gameClient.sendAcertijos(acertijosActuales, nombreJugador);
        lastAcertijosCount = acertijosActuales;
    }
}

Campos para cacheo:

private int lastVidaSent = -1;
private int lastAcertijosCount = -1;

FLUJO COMPLETO DE SINCRONIZACION

Escenario: Jugador A se mueve y resuelve un acertijo mientras Jugador B mira

Frame 1:
Jugador A se mueve (5000,5000) -> (5050,5000)

    Jugador A envia: POS:5050,5000,RIGHT,Juan
    
    Servidor recibe, retransmite a B
    
    Jugador B recibe: POS:5050,5000,RIGHT,Juan
    
    B actualiza remoto y lo ve moverse

Frame 2:
Jugador A resuelve acertijo (3 -> 4)

    Jugador A envia: ACERTIJOS:4,Juan
    
    Servidor recibe, retransmite a B
    
    Jugador B recibe: ACERTIJOS:4,Juan
    
    B ve el contador de acertijos aumentar en HUD

Frame 3:
Jugador A se mueve (5050,5000) -> (5100,5000) y su vida baja (100 -> 85)

    Jugador A envia: POS:5100,5000,RIGHT,Juan
    Jugador A envia: VIDA:85,Juan
    
    Servidor recibe ambos, retransmite
    
    Jugador B recibe ambos
    
    B ve jugador moverse Y su vida bajar

OPTIMIZACIONES IMPLEMENTADAS

1. Cacheo de Datos

Se envia VIDA y ACERTIJOS solo cuando cambian:

if (vidaActual != lastVidaSent) {
    gameClient.sendVida(vidaActual, nombreJugador);
    lastVidaSent = vidaActual;
}

No se envian cada frame si no hay cambio.

2. POS cada Frame

La posicion se envia cada frame porque cambia constantemente:

gameClient.sendPosition(x, y, dir, nombreJugador);

3. Thread Separado para Lectura

No bloquea el thread de renderizado:

Thread lecturaThread = new Thread(this::leerMensajes);
lecturaThread.setDaemon(true);
lecturaThread.start();

4. ConcurrentHashMap

Permite acceso thread-safe desde multiples threads:

private Map<String, RemotePlayer> remotePlayers = new ConcurrentHashMap();

TRATAMIENTO DE DATOS PERDIDOS

Si un mensaje se pierde en la red (poco probable con TCP):

El siguiente cambio en ese valor se envia y se recibe correctamente.
Ejemplo: Si se pierde "VIDA:85", el siguiente "VIDA:80" se recibira bien.

CONSIDERACIONES DE LATENCIA

Latencia de Red

Si la latencia es de 50ms:

. Posicion se envia cada 16ms (60 FPS)
. Llega después de 50ms
. Se ve con ~3 frames de delay

Interpolacion (Mejora Futura)

Podria implementarse interpolacion:

posicion_renderizada = posicion_anterior + 
    (posicion_nueva - posicion_anterior) * (tiempo_transcurrido / tiempo_esperado)

Esto suavizaria el movimiento.

PRUEBAS REALIZADAS

. Dos jugadores se mueven y se ven mutuamente
. Vida se actualiza en tiempo real para todos
. Acertijos se cuentan correctamente
. Multiples cambios simultaneos se sincronizan
. Los datos se retransmiten correctamente
. No hay duplicacion de datos

CONCLUSION

El sistema de sincronizacion de movimiento y datos funciona mediante protocolos simples pero efectivos. Cada cambio de estado se propaga a traves del servidor a todos los clientes, permitiendo una experiencia multijugador coherente y en tiempo real.

Fecha: 26 de Noviembre, 2025
Version: 1.0
