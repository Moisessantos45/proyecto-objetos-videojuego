SINCRONIZACION DEL MAPA PARA MULTIJUGADOR

DESCRIPCION GENERAL

En un juego multijugador, es critico que todos los jugadores vean exactamente el mismo mundo. Para lograr esto en VideoJuego_v2 se utiliza un sistema de semillas (seeds) que garantiza que el mapa generado sea identico en todas las instancias del juego.

PROBLEMA A RESOLVER

Sin sincronizacion de mapa:

Jugador 1                        Jugador 2
Mapa generado aleatoriamente     Mapa generado aleatoriamente
Enemigos en posicion A           Enemigos en posicion B
Cofres en celda 1                Cofres en celda 2
Resultado: CONFLICTO             Resultado: CONFLICTO

Ambos ven un mundo diferente:
. Los enemigos no estan en los mismos lugares
. Los recursos (pociones, cofres) no coinciden
. La aventura no es compartida

SOLUCION IMPLEMENTADA

La solucion es usar una semilla unica de generacion de numeros pseudoaleatorios. Si ambos jugadores generan el mapa usando la misma semilla, obtendran identicamente el mismo mapa.

Jugador 1                        Jugador 2
Recibe seed: 123456              Recibe seed: 123456
Genera mapa con seed             Genera mapa con seed
Resultado: MAPA A                Resultado: MAPA A
SINCRONIZADO                     SINCRONIZADO

GENERACION DEL MAPA CON SEED

En GeneradorMundo.java:

public class GeneradorMundo {
    private long seed;
    private Random random;
    private static final int TAMANIO_MUNDO = 10000;
    
    public GeneradorMundo(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
    }
    
    public Chunk generarChunk(int chunkX, int chunkY) {
        random.setSeed(seed + chunkX + chunkY * 1000);
        Tile[][] tiles = new Tile[TAMANIO_CHUNK][TAMANIO_CHUNK];
        
        for (int x = 0; x < TAMANIO_CHUNK; x++) {
            for (int y = 0; y < TAMANIO_CHUNK; y++) {
                int tipoTile = random.nextInt(100);
                tiles[x][y] = crearTile(tipoTile);
            }
        }
        
        return new Chunk(tiles);
    }
}

Explicacion:

1. Se recibe una semilla unica (ej: 123456)
2. Se crea un objeto Random con esa semilla
3. Cada chunk se genera usando la semilla mas sus coordenadas
4. random.setSeed(seed + chunkX + chunkY * 1000) asegura que:
   . El chunk (0,0) siempre genere lo mismo
   . El chunk (1,0) siempre genere lo mismo
   . El chunk (0,1) siempre genere diferente a (1,0)

FLUJO DE SINCRONIZACION

Paso 1: Creacion del Servidor

Cuando un jugador selecciona Crear Servidor:

En Main.java:

GameServer server = new GameServer(8888);
long mapSeed = System.currentTimeMillis();
server.setMapSeed(mapSeed);

Se genera una semilla usando la marca de tiempo actual (unica en ese momento).

Paso 2: Generacion del Mapa Local

El jugador que creo el servidor genera su mapa:

En GamePanel:

if (tipoJuego == SERVIDOR) {
    long mapSeed = System.currentTimeMillis();
    GeneradorMundo generador = new GeneradorMundo(mapSeed);
    world = generador.generarMundo();
}

Paso 3: Envio de Semilla al Cliente

Cuando un cliente se conecta, recibe la semilla:

En ClientHandler (servidor):

public void procesarMensaje(String mensaje) {
    if (mensaje.startsWith("USUARIO:")) {
        nombreJugador = mensaje.substring(8);
        String welcome = "WELCOME:" + nombreJugador + 
                        ",id,5000,5000," + mapSeed;
        writer.println(welcome);
    }
}

Formato: WELCOME:nombre,id,posX,posY,mapSeed

Ejemplo: WELCOME:Juan,0,5000,5000,1732626000000

Paso 4: Recepcion de Semilla en Cliente

En GameClient:

private void leerMensajes() {
    String mensaje;
    while ((mensaje = reader.readLine()) != null) {
        if (mensaje.startsWith("WELCOME:")) {
            procesarWelcome(mensaje);
        }
    }
}

private void procesarWelcome(String mensaje) {
    String[] partes = mensaje.substring(8).split(",");
    String nombreJugador = partes[0];
    int posX = Integer.parseInt(partes[2]);
    int posY = Integer.parseInt(partes[3]);
    long mapSeed = Long.parseLong(partes[4]);
    
    generarMapoConSeed(mapSeed);
}

Paso 5: Generacion del Mapa en Cliente

El cliente recibe la semilla y genera el mismo mapa:

private void generarMapoConSeed(long seed) {
    GeneradorMundo generador = new GeneradorMundo(seed);
    mundo = generador.generarMundo();
}

Ahora ambos clientes tienen el mapa identico.

GENERACION DE MUNDO INFINITO CON SEED

El mundo se genera usando chunks. Cada chunk tiene coordenadas (chunkX, chunkY):

En ManejadorMapaInfinito.java:

public class ManejadorMapaInfinito {
    private Map<String, Chunk> chunksEnMemoria;
    private GeneradorMundo generador;
    
    public Chunk obtenerChunk(int chunkX, int chunkY) {
        String clave = chunkX + "," + chunkY;
        
        if (!chunksEnMemoria.containsKey(clave)) {
            Chunk chunk = generador.generarChunk(chunkX, chunkY);
            chunksEnMemoria.put(clave, chunk);
        }
        
        return chunksEnMemoria.get(clave);
    }
}

Esto garantiza que:
. Cada chunk con coordenadas (x,y) se genera igual en todos los clientes
. Si el jugador regresa a (10,10), veran el MISMO chunk
. Los enemigos, objetos y tiles coincidiran exactamente

EJEMPLO PRACTICO

Generacion del Mapa de dos Jugadores

Servidor:
    mapSeed = 1732626000000
    
    Genera chunk (0,0):
    random.setSeed(1732626000000 + 0 + 0 * 1000)
    Resultado: Pasto, arbol, enemigo goblin
    
    Genera chunk (1,0):
    random.setSeed(1732626000000 + 1 + 0 * 1000)
    Resultado: Agua, puente, cofre

Cliente 1 (Servidor):
    Recibe WELCOME con seed 1732626000000
    Genera chunk (0,0):
    random.setSeed(1732626000000 + 0 + 0 * 1000)
    Resultado: Pasto, arbol, enemigo goblin ✓ COINCIDE
    
    Genera chunk (1,0):
    random.setSeed(1732626000000 + 1 + 0 * 1000)
    Resultado: Agua, puente, cofre ✓ COINCIDE

Cliente 2 (Se une):
    Recibe WELCOME con seed 1732626000000
    Genera chunk (0,0):
    random.setSeed(1732626000000 + 0 + 0 * 1000)
    Resultado: Pasto, arbol, enemigo goblin ✓ COINCIDE
    
    Genera chunk (1,0):
    random.setSeed(1732626000000 + 1 + 0 * 1000)
    Resultado: Agua, puente, cofre ✓ COINCIDE

Todos ven lo MISMO.

VENTAJAS DE USAR SEED

1. Sincronizacion Perfecta

No es necesario enviar todo el mapa por la red. Solo se envia un numero (la semilla).

Alternativa sin seed (MALA):
- Generar todo el mundo (millones de tiles)
- Serializar todo el mapa
- Enviar por red (gigabytes de datos)
- Deserializar en cliente

Con seed (BUENA):
- Generar semilla aleatoria (8 bytes)
- Enviar semilla por red
- Ambos generan el mapa localmente

2. Consistencia

Si un jugador regresa a una zona visitada antes:
- Los enemigos estaran en las mismas posiciones
- Los tiles seran identicos
- Los cofres estaran en los mismos lugares

3. Escalabilidad

Con cientos o miles de jugadores:
- Todos generan el mismo mapa sin comunicacion adicional
- No consume ancho de banda

CODIGO REFERENCIA COMPLETO

En GamePanel.java, conexion multijugador:

if (tipoJuego == SERVIDOR) {
    long mapSeed = System.currentTimeMillis();
    this.gameClient = new GameClient(
        "localhost", 
        8888, 
        nombreJugador, 
        mapSeed
    );
    gameEngine = new GameEngine(mapSeed);
} else if (tipoJuego == CLIENTE) {
    this.gameClient = new GameClient(host, puerto, nombreJugador, null);
    this.gameClient.setOnMapSeedReceived((seed) -> {
        gameEngine = new GameEngine(seed);
    });
}

En GameClient.java:

public void procesarWelcome(String mensaje) {
    String[] partes = mensaje.substring(8).split(",");
    long mapSeed = Long.parseLong(partes[4]);
    
    if (onMapSeedReceived != null) {
        onMapSeedReceived.accept(mapSeed);
    }
}

En GameEngine.java:

public GameEngine(long mapSeed) {
    this.mapaInfinito = new ManejadorMapaInfinito(mapSeed);
    this.jugadorSystem = new JugadorSystem();
    this.enemigoSystem = new EnemigoSystem(mapaInfinito);
}

DETERMINISMO EN GENERACION ALEATORIA

Es importante notar que la generacion pseudoaleatoria es determinista:

Semilla = 12345
    Random.setSeed(12345)
    random.nextInt() = 678
    random.nextInt() = 923
    random.nextInt() = 145
    
Semilla = 12345
    Random.setSeed(12345)
    random.nextInt() = 678  ← IDENTICO
    random.nextInt() = 923  ← IDENTICO
    random.nextInt() = 145  ← IDENTICO

Esto garantiza determinismo total en la generacion de mapas.

SINCRONIZACION DINAMICA

Aunque el mapa inicial se sincroniza por seed, los cambios dinamicos se sincronizan separadamente:

1. Mapa Base

Sincronizado por seed. Es igual para todos.

2. Cofres Abiertos

Se sincronizan via COFRE_CERRADO:

COFRE_CERRADO:0,0,5_12_23

Comunica a otros: En chunk (0,0) los cofres con ID 5, 12 y 23 estan abiertos.

3. Enemigos Muertos

Actualmente no se sincronizan. Cada cliente mantiene su propio estado de enemigos.

Mejora futura: Enviar ENEMIGO_MUERTO:enemyId para sincronizar enemigos eliminados.

PRUEBAS REALIZADAS

. Dos clientes generan mapa identico con misma seed
. Chunks se generan consistentemente
. Enemigos aparecen en posiciones identicas
. Cofres estan en mismo lugar para ambos jugadores
. Cambios dinamicos (cofres abiertos) se sincronizan
. Regresar a zona ya visitada mantiene coherencia
. Mapas diferentes con seeds diferentes

CONCLUSION

El sistema de sincronizacion de mapas mediante seed garantiza que todos los jugadores en multijugador compartan exactamente el mismo mundo. Esto es eficiente, escalable y mantiende coherencia entre jugadores.

Fecha: 26 de Noviembre, 2025
Version: 1.0
