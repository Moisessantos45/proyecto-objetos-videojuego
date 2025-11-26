# 📍 Flujo de Carga del Mapa y Timer

## 🎯 Respuesta Directa

El mapa se carga cuando inicia la aplicación, pero de forma inteligente y progresiva:

1. ✅ Al iniciar la aplicación: Se cargan las imágenes de los tiles (texturas)
2. ✅ Al crear GameEngine: Se generan los chunks iniciales alrededor del jugador
3. ✅ Durante el juego: Se generan nuevos chunks a medida que el jugador explora

El timer ahora inicia cuando el jugador comienza a jugar (cuando selecciona "Jugar Solo" y el juego está en estado JUGANDO).

---

## 🔄 Flujo Detallado de Inicialización

### 1️⃣ INICIO DE LA APLICACIÓN (Main.java)

java
public static void main(String args) {
    // ... configuración ...
    
    // PASO 1: Crear el mapa
    MapaInfinitoAdapter mapaAdapter = new MapaInfinitoAdapter(
        config.getTamanioTile(), 
        config.getAnchoPantalla(), 
        config.getAltoPantalla()
    );
    
    // PASO 2: Crear sistemas del juego
    // ...
    
    // PASO 3: Crear GameEngine
    GameEngine gameEngine = new GameEngine(
        config,
        jugadorSystem,
        camaraSystem,
        mapaAdapter,
        enemigoSystem,
        inputService
    );
}


Qué sucede:
- Se crea el objeto MapaInfinitoAdapter
- Se cargan las imágenes de los tiles (pasto, agua, árbol, muro, etc.)
- Se inicializa el sistema de chunks (vacío por ahora)
- NO se genera ningún chunk todavía

### 2️⃣ CREACIÓN DEL MAPA INFINITO (ManejadorMapaInfinito.java)

java
public ManejadorMapaInfinito(int tamanioTile, int anchoPantalla, int altoPantalla, long seed) {
    this.tamanioTile = tamanioTile;
    this.anchoPantalla = anchoPantalla;
    this.altoPantalla = altoPantalla;
    this.chunksActivos = new HashMap<>();  // ← HashMap vacío
    this.generador = new GeneradorMundo(seed, this);
    this.itemsConsumibles = new ArrayList<>();

    cargarTilesVisuales();  // ← Carga imágenes PNG de los tiles
}

private void cargarTilesVisuales() {
    tiles = new Tile11;
    
    tiles0 = new Tile();
    tiles0.setImage(ImageIO.read(new File("tiles/agua.png")));
    
    tiles1 = new Tile();
    tiles1.setImage(ImageIO.read(new File("tiles/arbol.png")));
    
    // ... etc para todos los tiles ...
}


Qué sucede:
- Se cargan en memoria las texturas/imágenes de todos los tipos de tile
- Se crea el GeneradorMundo con un seed (para generación procedural)
- El HashMap de chunks está vacío (sin chunks generados)

### 3️⃣ INICIALIZACIÓN DEL GAME ENGINE

java
public GameEngine(...) {
    // ... asignación de variables ...
    
    inicializar();  // ← Llama a inicializar()
}

private void inicializar() {
    // 1. Posicionar cámara
    camaraSystem.seguirEntidad(
        jugadorSystem.getMundoX(),   // 5000
        jugadorSystem.getMundoY()    // 5000
    );
    
    // 2. GENERAR CHUNKS INICIALES ← AQUÍ SE CARGA EL MAPA
    mapaInfinito.actualizarChunksActivos(
        jugadorSystem.getMundoX(),   // 5000
        jugadorSystem.getMundoY()    // 5000
    );
    
    // 3. Generar enemigos iniciales
    enemigoSystem.generarEnemigosIniciales(...);
    
    // 4. Timer NO inicia aquí (se inicia al comenzar a jugar)
    tiempoInicioJuego = 0;  // ← 0 = no iniciado
    tiempoTranscurrido = 0;
    juegoTerminado = false;
}


Qué sucede:
- Se llama a actualizarChunksActivos() con la posición inicial (5000, 5000)
- Se generan los chunks visibles y cercanos al jugador
- Se generan 5 enemigos iniciales
- El timer NO inicia (esperará a que el jugador comience a jugar)

### 4️⃣ GENERACIÓN DE CHUNKS INICIALES

java
public void actualizarChunksActivos(int jugadorX, int jugadorY) {
    // Convertir posición del jugador a coordenadas de chunk
    int tileX = jugadorX / tamanioTile;  // 5000 / 48 = ~104
    int tileY = jugadorY / tamanioTile;  // 5000 / 48 = ~104
    
    int chunkJugadorX = Math.floorDiv(tileX, Chunk.CHUNK_SIZE);  // ~6
    int chunkJugadorY = Math.floorDiv(tileY, Chunk.CHUNK_SIZE);  // ~6
    
    // Calcular rango de chunks a cargar (con margen)
    minChunkX = chunkJugadorX - anchoVista / 2 - MARGEN_CHUNKS;
    maxChunkX = chunkJugadorX + anchoVista / 2 + MARGEN_CHUNKS;
    minChunkY = chunkJugadorY - altoVista / 2 - MARGEN_CHUNKS;
    maxChunkY = chunkJugadorY + altoVista / 2 + MARGEN_CHUNKS;
    
    // Generar chunks en el rango visible
    for (int cy = minChunkY; cy <= maxChunkY; cy++) {
        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            cargarChunk(cx, cy);  // ← Genera cada chunk
        }
    }
}

private void cargarChunk(int chunkX, int chunkY) {
    String key = Chunk.crearKey(chunkX, chunkY);  // ej: "6_6"
    
    if (!chunksActivos.containsKey(key)) {
        Chunk chunk = new Chunk(chunkX, chunkY);
        generador.generarChunk(chunk);  // ← Generación procedural
        chunksActivos.put(key, chunk);
    }
}


Qué sucede:
- Se calculan qué chunks son visibles desde la posición del jugador
- Se generan aproximadamente 9-25 chunks (3x3 a 5x5) alrededor del jugador
- Cada chunk contiene 16x16 tiles (256 tiles por chunk)
- Los tiles se generan usando Perlin Noise (generación procedural)

### 5️⃣ USUARIO NAVEGA POR LOS MENÚS


Pantalla de Bienvenida
         ↓
    Menú Principal
         ↓
1 Jugar Solo  ← Usuario selecciona esta opción
2 Crear Servidor
3 Unirse a Servidor
4 Salir


En este punto:
- El mapa ya está cargado (chunks iniciales generados)
- Los enemigos ya están generados
- El timer aún NO ha comenzado (tiempoInicioJuego = 0)

### 6️⃣ INICIO DEL TIMER (CUANDO COMIENZA LA PARTIDA)

java
@Override
public void update() {
    // ... manejo de modales ...
    
    if (!jugando) return;  // Si está pausado, no ejecuta
    
    // ← AQUÍ INICIA EL TIMER LA PRIMERA VEZ
    if (tiempoInicioJuego == 0) {
        tiempoInicioJuego = System.currentTimeMillis();
    }
    
    // Actualizar timer
    tiempoTranscurrido = System.currentTimeMillis() - tiempoInicioJuego;
    // ...
}


Cuándo se ejecuta:
- PRIMERA VEZ: Cuando el estado cambia a JUGANDO (usuario selecciona 1 Jugar Solo)
- El timer comienza a contar desde 3:00
- Esto sucede en el primer frame del estado JUGANDO

---

## ⏱️ Comparación: Antes vs Ahora

### ❌ ANTES (Comportamiento Incorrecto)


1. Usuario abre la aplicación
2. Main.java se ejecuta
3. GameEngine se crea
4. inicializar() se ejecuta
   ├─ Se generan chunks
   └─ Timer inicia ← ❌ MAL: Timer ya está corriendo
5. Usuario ve pantalla de bienvenida (timer cuenta en el fondo)
6. Usuario ve menú principal (timer sigue contando)
7. Usuario selecciona 1 Jugar Solo
8. Juego comienza pero ya pasaron 20-30 segundos ← PROBLEMA


Problema: El jugador perdía tiempo navegando por los menús.

### ✅ AHORA (Comportamiento Correcto)


1. Usuario abre la aplicación
2. Main.java se ejecuta
3. GameEngine se crea
4. inicializar() se ejecuta
   ├─ Se generan chunks
   └─ Timer NO inicia (tiempoInicioJuego = 0) ← ✅ CORRECTO
5. Usuario ve pantalla de bienvenida (sin timer)
6. Usuario ve menú principal (sin timer)
7. Usuario selecciona 1 Jugar Solo
8. Estado cambia a JUGANDO
9. update() se ejecuta por primera vez
10. Timer inicia: tiempoInicioJuego = now() ← ✅ AQUÍ INICIA
11. Jugador tiene exactamente 3:00 minutos para jugar


Ventaja: El jugador tiene los 3 minutos completos de juego real.

---

## 🗺️ Sistema de Generación de Chunks (Mapa Infinito)

### Características del Sistema

#### 1. Generación Perezosa (Lazy Loading)
- No se genera TODO el mapa al inicio
- Solo se generan chunks cuando son necesarios
- Los chunks se generan a medida que el jugador explora

#### 2. Chunks Activos en Memoria
java
private HashMap<String, Chunk> chunksActivos;
private static final int MAX_CHUNKS_EN_MEMORIA = 100;

- Se mantienen máximo 100 chunks en memoria
- Cada chunk = 16x16 tiles = 256 tiles
- Total en memoria = 25,600 tiles máximo

#### 3. Limpieza Automática
java
if (chunksActivos.size() > MAX_CHUNKS_EN_MEMORIA) {
    limpiarChunksLejanos();  // Elimina chunks lejanos
}

- Cuando hay más de 100 chunks, se eliminan los más lejanos
- Libera memoria automáticamente
- Los chunks eliminados se pueden regenerar si el jugador vuelve

#### 4. Generación Procedural
java
generador.generarChunk(chunk);  // Usa Perlin Noise

- Cada chunk se genera con Perlin Noise (algoritmo de ruido)
- El seed garantiza que el mismo chunk siempre sea igual
- Si eliminas un chunk y vuelves, se regenerará idéntico

### Ejemplo Visual de Chunks


Jugador en posición (5000, 5000)

┌─────────────────────────────────────────┐
│  3,3 4,3 5,3 6,3 7,3 8,3   │  Chunks lejanos
│  3,4 4,4 5,4 6,4 7,4 8,4   │  (se eliminan de memoria)
│  3,5 4,5 5,5 6,5 7,5 8,5   │
│  3,6 4,6 5,6 🎮6 7,6 8,6   │  🎮 = Jugador
│  3,7 4,7 5,7 6,7 7,7 8,7   │  (chunk 6,6)
│  3,8 4,8 5,8 6,8 7,8 8,8   │
│  3,9 4,9 5,9 6,9 7,9 8,9   │  Chunks cercanos
└─────────────────────────────────────────┘  (cargados en memoria)

       ↑ Chunks visibles (5x5 = 25 chunks)
       ↑ Chunks con margen (7x7 = 49 chunks)


---

## 📊 Resumen del Flujo Completo

| Momento | Evento | Mapa | Timer |
|-------------|-----------|----------|-----------|
| 1. Main.main() | Aplicación inicia | Texturas cargadas | No iniciado |
| 2. MapaInfinitoAdapter() | Mapa se crea | HashMap vacío | No iniciado |
| 3. GameEngine() | Motor se crea | - | No iniciado |
| 4. inicializar() | Generación inicial | ✅ Chunks generados | tiempoInicioJuego = 0 |
| 5. Pantalla Bienvenida | Usuario ve intro | Chunks en memoria | No iniciado |
| 6. Menú Principal | Usuario navega | Chunks en memoria | No iniciado |
| 7. 1 Jugar Solo | Estado → JUGANDO | Chunks en memoria | No iniciado |
| 8. Primer update() | Primer frame | Chunks activos | ✅ Timer inicia |
| 9. Jugando | Cada frame | Se actualizan chunks | Timer cuenta |
| 10. Jugador se mueve | Explora | Nuevos chunks generan | Timer cuenta |

---

## ✅ Conclusión

### Comportamiento Actual (Correcto):

1. Mapa: Se carga progresivamente
   - Texturas: Al inicio de la aplicación
   - Chunks iniciales: Al crear GameEngine
   - Chunks nuevos: A medida que el jugador explora

2. Timer: Se inicia al comenzar la partida
   - NO cuenta durante los menús
   - Inicia cuando el estado cambia a JUGANDO
   - El jugador tiene exactamente 3:00 minutos de juego real

3. Integración: Ambos sistemas funcionan correctamente
   - El mapa está listo cuando el timer inicia
   - No hay pérdida de tiempo en los menús
   - El jugador tiene la experiencia completa de 3 minutos

---

Última actualización: 10 de Noviembre, 2024  
Versión: 1.1 (Corrección del inicio del timer)