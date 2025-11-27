SISTEMA DE COMUNICACION EN TIEMPO REAL

DESCRIPCION GENERAL

El sistema de comunicacion en tiempo real permite que multiples jugadores se conecten a un servidor compartido, compartan informacion de sus posiciones, estadisticas y estado del juego. Se implementa utilizando sockets TCP de Java para la transferencia de datos entre cliente y servidor.

ARQUITECTURA DE RED

La arquitectura se compone de tres elementos principales:

1. Cliente de Juego
   El cliente local que maneja la entrada del usuario y la logica del juego local.

2. Servidor Dedicado
   El servidor que actua como intermediario entre todos los clientes conectados.

3. Protocolo de Comunicacion Personalizado
   Un protocolo basado en texto que define como se intercambia informacion entre cliente y servidor.

CONFIGURACION DE RED

Puerto del Servidor: 8888
Protocolo: TCP
Tipo de Conexion: Cliente-Servidor
Concurrencia: Multi-cliente (soporta multiples conexiones simultaneas)

ESTABLECIMIENTO DE CONEXION

Cuando un cliente se conecta al servidor, ocurren los siguientes pasos:

1. Creacion del Socket

En GameClient.java:

private Socket socket;
private PrintWriter writer;
private BufferedReader reader;

public GameClient(String host, int puerto) {
    try {
        this.socket = new Socket(host, puerto);
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Thread lecturaThread = new Thread(this::leerMensajes);
        lecturaThread.setDaemon(true);
        lecturaThread.start();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

2. Autenticacion del Cliente

Una vez establecida la conexion, el cliente envia su nombre de usuario:

public void enviarNombreUsuario(String nombre) {
    if (writer != null) {
        writer.println("USUARIO:" + nombre);
    }
}

3. Respuesta del Servidor

El servidor responde con informacion de bienvenida y configuracion del juego:

WELCOME:nombreUsuario,idJugador,posicionX,posicionY,mapSeed

PROTOCOLO DE COMUNICACION

El protocolo utiliza mensajes basados en texto con formato especifico. Cada mensaje comienza con un identificador de tipo seguido de dos puntos y los datos:

TIPOS DE MENSAJE

1. USUARIO - Autenticacion de Usuario

USUARIO:nombre_jugador

Ejemplo: USUARIO:Juan

2. POS - Posicion del Jugador

POS:x,y,direccion,nombreJugador

Ejemplo: POS:5000,5000,DOWN,Juan

Se envia en cada frame de actualizacion del juego para sincronizar posiciones de todos los jugadores.

3. VIDA - Puntos de Vida del Jugador

VIDA:cantidadVida,nombreJugador

Ejemplo: VIDA:100,Juan

Se envia cuando el jugador recibe dano o usa pociones.

4. ACERTIJOS - Cantidad de Acertijos Resueltos

ACERTIJOS:cantidad,nombreJugador

Ejemplo: ACERTIJOS:5,Juan

Se envia cuando el jugador resuelve un acertijo con exito.

5. COFRE_CERRADO - Estado de los Cofres

COFRE_CERRADO:chunkX,chunkY,cofresIds,nombreJugador

Ejemplo: COFRE_CERRADO:0,0,1_2_3,Juan

Se envia cuando un jugador abre un cofre para informar a otros jugadores que este cofre ya no contiene items.

6. MAP_SEED - Semilla del Mapa

MAP_SEED:seed

Ejemplo: MAP_SEED:12345

Se envia cuando un jugador crea un servidor para que todos los clientes generen el mismo mapa.

7. START_GAME - Inicio del Juego

START_GAME

Indica que el juego debe comenzar (usado para sincronizar inicio en multijugador).

8. SERVIDOR_CERRADO - Cierre del Servidor

SERVIDOR_CERRADO

Notifica a los clientes que el servidor ha sido cerrado.

FLUJO DE COMUNICACION

Conexion Inicial

Cliente Juego
     |
     | conectar a 8888
     |
   Servidor TCP
     |
     | socket aceptado
     |
Cliente Juego
     |
     | enviar USUARIO:nombre
     |
   Servidor TCP
     |
     | enviar WELCOME:nombre,id,x,y,seed
     |
Cliente Juego
     |
     v registrado y listo

Sincronizacion Durante el Juego

Cada Frame (aproximadamente 60 veces por segundo):

Cliente Juego 1
     |
     | enviar POS
     | enviar VIDA
     | enviar ACERTIJOS
     |
   Servidor TCP
     |
     | broadcast a todos
     |
Cliente Juego 2
Cliente Juego 3
...

ESTRUCTURA DEL SERVIDOR

En Main.java se inicia el servidor:

public class GameServer {
    private ServerSocket serverSocket;
    private java.util.Map<String, ClientHandler> clientes;
    private long mapSeed;
    
    public GameServer(int puerto) {
        try {
            this.serverSocket = new ServerSocket(puerto);
            this.clientes = new ConcurrentHashMap();
            this.mapSeed = System.currentTimeMillis();
            System.out.println("Servidor iniciado en puerto: " + puerto);
            aceptarConexiones();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void aceptarConexiones() {
        new Thread(() -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(
                        clientSocket, 
                        clientes, 
                        mapSeed
                    );
                    new Thread(clientHandler).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

ESTRUCTURA DEL MANEJADOR DE CLIENTE

Cada cliente conectado es manejado por un ClientHandler en un thread separado:

public class ClientHandler implements Runnable {
    private Socket socket;
    private String nombreJugador;
    private Map<String, ClientHandler> todosLosClientes;
    private long mapSeed;
    
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            );
            PrintWriter writer = new PrintWriter(
                socket.getOutputStream(), 
                true
            );
            
            String mensaje;
            while ((mensaje = reader.readLine()) != null) {
                procesarMensaje(mensaje, writer);
                transmitirATodos(mensaje);
            }
        } catch (IOException e) {
            System.out.println(nombreJugador + " desconectado");
            todosLosClientes.remove(nombreJugador);
        }
    }
    
    private void procesarMensaje(String mensaje, PrintWriter writer) {
        if (mensaje.startsWith("USUARIO:")) {
            nombreJugador = mensaje.substring(8);
            todosLosClientes.put(nombreJugador, this);
            writer.println("WELCOME:" + nombreJugador + ",0,5000,5000," + mapSeed);
        } else if (mensaje.startsWith("MAP_SEED:")) {
            transmitirATodos(mensaje);
        }
    }
    
    private void transmitirATodos(String mensaje) {
        for (ClientHandler cliente : todosLosClientes.values()) {
            cliente.enviarMensaje(mensaje);
        }
    }
    
    public void enviarMensaje(String mensaje) {
        if (writer != null) {
            writer.println(mensaje);
        }
    }
}

SINCRONIZACION EN TIEMPO REAL

Posiciones de Jugadores

En GamePanel.java, cada frame se envia la posicion:

if (gameClient != null) {
    gameClient.sendPosition(
        jugadorSystem.getJugador().getMundoX(),
        jugadorSystem.getJugador().getMundoY(),
        jugadorSystem.getJugador().getDireccion().toString(),
        nombreJugador
    );
}

En el servidor, este mensaje se retransmite a todos los clientes:

POS:5000,5050,DOWN,Juan

En el cliente receptor, se actualiza la posicion del jugador remoto:

if (mensaje.startsWith("POS:")) {
    String[] partes = mensaje.substring(4).split(",");
    int x = Integer.parseInt(partes[0]);
    int y = Integer.parseInt(partes[1]);
    String direccion = partes[2];
    String nombre = partes[3];
    
    RemotePlayer remoto = remotePlayers.get(nombre);
    if (remoto != null) {
        remoto.actualizarPosicion(x, y, direccion);
    }
}

Sincronizacion de Semilla de Mapa

Para que todos los jugadores tengan el mismo mapa:

1. El jugador que crea el servidor obtiene una semilla

En GameServer:
this.mapSeed = System.currentTimeMillis();

2. Se la envia al primer cliente conectado

En WELCOME:
WELCOME:nombre,id,5000,5000,123456789

3. El cliente genera el mapa con esa semilla

En GameEngine:
GeneradorMundo generador = new GeneradorMundo(mapSeed);
mundo = generador.generarMundo();

4. Los clientes que se unen reciben la misma semilla

Al conectarse:
WELCOME:nombre,id,5000,5000,123456789

Resultado: Todos ven exactamente el mismo mapa.

MANEJO DE DESCONEXION

Cuando un Cliente se Desconecta

En ClientHandler.run():

} catch (IOException e) {
    System.out.println(nombreJugador + " desconectado");
    todosLosClientes.remove(nombreJugador);
}

El servidor automáticamente:
. Elimina al jugador de la lista de clientes
. Notifica a otros clientes (esto podria mejorarse)

Cuando el Servidor se Cierra

Si el servidor se cierra, los clientes reciben:

SERVIDOR_CERRADO

En GameClient:

if (mensaje.equals("SERVIDOR_CERRADO")) {
    System.out.println("El servidor se ha cerrado");
    estadoJuego = GameState.MENU_PRINCIPAL;
    conexionActiva = false;
}

COMUNICACION MULTIJUGADOR

Cuando el Creador de Sala Termina el Juego

Si el jugador que creo la sala (servidor) es quien termina:

1. El juego de ese jugador termina

En GamePanel, tiempo llega a 0:
estadoJuego = GameState.JUEGO_TERMINADO;

2. Los otros clientes siguen jugando

No hay notificacion de termino de juego en otros clientes.

Cuando Otro Jugador Termina

Si un cliente que se unio a la sala termina:

1. Su juego termina localmente
2. No afecta a otros jugadores

El servidor NO cierra, por lo que otros pueden continuar jugando.

ESTRUCTURA DE DATOS DE JUGADORES REMOTOS

Cada cliente mantiene una referencia a los otros jugadores en remotePlayers:

private Map<String, RemotePlayer> remotePlayers = new ConcurrentHashMap();

La clase RemotePlayer contiene:

public class RemotePlayer {
    private String nombre;
    private EntidadModel entidad;
    private PlayerStats stats;
    private int acertijosResueltos;
    
    public RemotePlayer(String nombre) {
        this.nombre = nombre;
        this.entidad = new EntidadModel(5000, 5000);
        this.stats = new PlayerStats(nombre);
        this.acertijosResueltos = 0;
    }
    
    public void actualizarPosicion(int x, int y, String direccion) {
        entidad.setMundoX(x);
        entidad.setMundoY(y);
    }
    
    public void actualizarVida(int vida) {
        stats.setVida(vida);
    }
    
    public void actualizarAcertijos(int cantidad) {
        stats.setAcertijos(cantidad);
    }
}

CONSIDERACIONES DE RENDIMIENTO

Threads Separados para Lectura

Cada cliente tiene un thread dedicado para leer mensajes:

Thread lecturaThread = new Thread(this::leerMensajes);
lecturaThread.setDaemon(true);
lecturaThread.start();

Esto evita bloquear el thread principal de renderizado.

ConcurrentHashMap para Sincronizacion

Se usa ConcurrentHashMap para thread-safety:

private Map<String, RemotePlayer> remotePlayers = new ConcurrentHashMap();

Esto permite que multiples threads accedan sin riesgo de condiciones de carrera.

Cola de Mensajes

Se utiliza una cola de mensajes para procesar datos de forma ordenada:

private Queue<String> messageQueue = new ConcurrentLinkedQueue();

LIMITACIONES Y MEJORAS FUTURAS

Limitaciones Actuales

1. No hay control de ancho de banda
2. No hay compresion de datos
3. Los mensajes no se validan
4. No hay reintento de conexion
5. No hay feedback de latencia

Mejoras Posibles

1. Implementar UDP para menor latencia
2. Delta compression para posiciones
3. Validacion de mensajes en servidor
4. Heartbeat para detectar desconexiones
5. Mostrar ping en HUD
6. Queue de comandos con rollback si necesario

PRUEBAS REALIZADAS

. Dos clientes conectados simultaneamente funcionan
. Posiciones se sincronizan correctamente
. Vida se actualiza en tiempo real
. Acertijos se cuentan para todos
. Cofres abiertos no reaparecen
. Cierre de servidor notifica clientes
. Misma semilla genera mismo mapa
. Multiples conexiones y desconexiones manejadas

CONCLUSION

El sistema de comunicacion en tiempo real utilizando sockets TCP proporciona una base solida para el juego multijugador. Todos los jugadores ven las mismas entidades, el mismo mapa y pueden interactuar entre si. El protocolo es extensible y puede adaptarse para agregar mas tipos de mensajes segun sea necesario.

Fecha: 26 de Noviembre, 2025
Version: 1.0
