================================================================================
            REGENERACION DEL MAPA EN CADA PARTIDA
================================================================================


1. PROBLEMA IDENTIFICADO
================================================================================

Cuando el jugador reiniciaba el juego o comenzaba una nueva partida, el mapa 
NO cambiaba. Esto causaba que:

   - Todos los cofres estaban en las mismas posiciones
   - Los items (pociones/venenos) aparecían en los mismos lugares
   - El terreno (agua, árboles, volcanes) era idéntico
   - La experiencia se volvía repetitiva y predecible


2. SOLUCION IMPLEMENTADA
================================================================================

Ahora, cada vez que se inicia una nueva partida, el mapa se regenera con un 
nuevo seed aleatorio, creando un mundo completamente diferente.


2.1 CAMBIOS REALIZADOS
-----------------------

2.1.1 Nuevo Método en GeneradorMundo.java

    public void cambiarSeed(long nuevoSeed) {
        setSeed(nuevoSeed);
        System.out.println("Generador de mundo actualizado con nuevo seed: " + nuevoSeed);
    }

Propósito: Permite cambiar el seed del generador de mundos para crear terrenos 
diferentes.


2.1.2 Nuevo Método en ManejadorMapaInfinito.java

    public void regenerarConNuevoSeed(long nuevoSeed) {
        // Cambiar el seed del generador
        generador.cambiarSeed(nuevoSeed);
        
        // Limpiar chunks e items
        chunksActivos.clear();
        itemsConsumibles.clear();
        
        System.out.println("Mapa regenerado con nuevo seed: " + nuevoSeed);
    }

    public long getSeedActual() {
        return generador.getSeed();
    }

Propósito: 
   - Cambia el seed del generador
   - Limpia todos los chunks existentes
   - Limpia todos los items del mapa
   - Prepara el mapa para regeneración completa


2.1.3 Modificación en GameEngine.reiniciarJuego()

ANTES:

    // Reiniciar items consumibles del mapa
    mapaInfinito.getItemsConsumibles().clear();

    // Reiniciar chunks del mapa para regenerar el mundo
    mapaInfinito.reiniciarChunks();

AHORA:

    // REGENERAR MAPA CON NUEVO SEED
    long nuevoSeed = System.currentTimeMillis();
    mapaInfinito.regenerarConNuevoSeed(nuevoSeed);

Propósito: En lugar de solo limpiar los chunks, ahora se genera un nuevo seed 
basado en el timestamp actual, lo que garantiza que cada partida tenga un mapa 
único.


3. COMO FUNCIONA EL SEED
================================================================================

3.1 QUE ES UN SEED
------------------

Un seed es un número que inicializa el generador de números aleatorios. Con el 
mismo seed, siempre se genera el mismo "aleatorio".

    // Ejemplo:
    Random random1 = new Random(12345);  // Seed fijo
    Random random2 = new Random(12345);  // Mismo seed

    random1.nextInt(100);  // Genera: 51
    random2.nextInt(100);  // Genera: 51 (¡igual!)


3.2 GENERACION DEL SEED
------------------------

Usamos System.currentTimeMillis() como seed:

    long nuevoSeed = System.currentTimeMillis();
    // Ejemplo: 1731223813308 (timestamp único)

Ventajas:
   - Cada milisegundo = un seed diferente
   - Prácticamente imposible obtener el mismo seed dos veces
   - Simple y efectivo


3.3 FLUJO DE REGENERACION
--------------------------

    1. Jugador reinicia el juego
       ↓
    2. Se genera nuevo seed: System.currentTimeMillis()
       ↓
    3. GeneradorMundo.cambiarSeed(nuevoSeed)
       - Actualiza this.seed
       - Crea nuevo Random(nuevoSeed)
       ↓
    4. ManejadorMapaInfinito limpia todo
       - chunksActivos.clear()
       - itemsConsumibles.clear()
       ↓
    5. Se actualizan chunks activos
       - actualizarChunksActivos(5000, 5000)
       ↓
    6. GeneradorMundo genera nuevos chunks
       - Usa el nuevo Random para generar terreno
       ↓
    7. ¡Mapa completamente nuevo!


4. COMPARACION: ANTES VS AHORA
================================================================================

4.1 ANTES (Mapa Repetitivo)
---------------------------

    Partida 1:
       - Seed: 123456789 (fijo)
       - Cofre en: (5120, 5080)
       - Volcán en: (5200, 5150)
       - Pociones en: (5100, 5090), (5130, 5100)

    Partida 2 (reinicio):
       - Seed: 123456789 (¡IGUAL!)
       - Cofre en: (5120, 5080)  ← Misma posición
       - Volcán en: (5200, 5150) ← Misma posición
       - Pociones en: (5100, 5090), (5130, 5100) ← Iguales

¡EL JUGADOR MEMORIZA DÓNDE ESTÁ TODO!


4.2 AHORA (Mapa Dinámico)
--------------------------

    Partida 1:
       - Seed: 1731223813308
       - Cofre en: (5120, 5080)
       - Volcán en: (5200, 5150)
       - Pociones en: (5100, 5090), (5130, 5100)

    Partida 2 (reinicio):
       - Seed: 1731223925451 (¡DIFERENTE!)
       - Cofre en: (5310, 5240)  ← Posición diferente
       - Volcán en: (5050, 5090) ← Posición diferente
       - Pociones en: (5180, 5200), (5090, 5110) ← Diferentes

¡CADA PARTIDA ES UNA NUEVA EXPERIENCIA!


5. IMPACTO EN EL JUEGO
================================================================================

5.1 ELEMENTOS QUE CAMBIAN
--------------------------

Elemento                      Cambia en cada partida    Como cambia
--------------------------------------------------------------------------------
Terreno (pasto, agua, arena)  Si                        Generación procedural con nuevo seed
Arboles                       Si                        Posiciones y densidad diferentes
Volcanes                      Si                        Posiciones aleatorias
Cofres                        Si                        Nuevas ubicaciones
Pociones                      Si                        Diferentes cantidades y posiciones
Venenos                       Si                        Diferentes cantidades y posiciones
Enemigos                      Si                        Se regeneran (pero no por seed)


5.2 ELEMENTOS QUE NO CAMBIAN
-----------------------------

Elemento                        Cambia    Por que
--------------------------------------------------------------------------------
Posición inicial del jugador    No        Siempre (5000, 5000)
Cantidad inicial de enemigos    No        Siempre 5 enemigos
Tamaño del mapa                 No        Infinito en todas las partidas
Tipos de tiles disponibles      No        Siempre los mismos 11 tipos


6. EJEMPLO TECNICO
================================================================================

6.1 GENERACION DE UN CHUNK
---------------------------

    // PARTIDA 1 (Seed: 1000)
    Random random = new Random(1000);
    int tileType = random.nextInt(11);  // Resultado: 3 (MURO)

    // PARTIDA 2 (Seed: 2000)
    Random random = new Random(2000);
    int tileType = random.nextInt(11);  // Resultado: 8 (VOLCÁN)

El mismo código, pero con seeds diferentes, produce resultados completamente 
distintos.


6.2 GENERACION DE PERLIN NOISE
-------------------------------

El Perlin Noise también se ve afectado por el seed:

    // El mismo punto (worldX, worldY) con diferentes seeds
    // produce valores de ruido diferentes:

    Seed 1000 -> Punto (5100, 5100) -> Noise: 0.45 -> PASTO
    Seed 2000 -> Punto (5100, 5100) -> Noise: 0.82 -> AGUA


7. BENEFICIOS DE LA IMPLEMENTACION
================================================================================

7.1 Rejugabilidad

   - Cada partida es única
   - No se puede memorizar ubicaciones
   - Mayor desafío y diversión


7.2 Exploración

   - Incentiva a explorar en cada partida
   - No se sabe dónde estarán los cofres
   - Búsqueda de items más emocionante


7.3 Estrategia

   - Los jugadores deben adaptarse al mapa
   - No hay "rutas óptimas" memorizadas
   - Cada partida requiere nuevas decisiones


7.4 Equidad

   - Todos los jugadores empiezan en igualdad
   - No hay ventaja por conocer el mapa
   - Experiencia justa


8. PRUEBAS REALIZADAS
================================================================================

   - Compilación exitosa
   - Primera partida genera mapa correctamente
   - Al reiniciar, el mapa es diferente
   - Cofres aparecen en nuevas posiciones
   - Items se regeneran en lugares distintos
   - Terreno cambia en cada partida
   - No hay errores en consola
   - Chunks se limpian correctamente  


9. ARCHIVOS MODIFICADOS
================================================================================

1. domain/GeneradorMundo.java
   - Agregado método cambiarSeed(long nuevoSeed)

2. domain/ManejadorMapaInfinito.java
   - Agregado método regenerarConNuevoSeed(long nuevoSeed)
   - Agregado método getSeedActual()

3. domain/GameEngine.java
   - Modificado reiniciarJuego() para usar regenerarConNuevoSeed()
   - Genera nuevo seed con System.currentTimeMillis()


10. CASOS DE USO
================================================================================

10.1 CASO 1: REINICIAR DESDE EL MENÚ DE PAUSA
----------------------------------------------

Escenario:
   1. Jugador está en partida 1
   2. Encuentra un cofre en (5120, 5080)
   3. Presiona ESC -> Menú de Pausa
   4. Selecciona 2 Reiniciar
   5. Nueva partida comienza

Resultado:
   - Nuevo seed generado
   - El cofre YA NO está en (5120, 5080)
   - Está en una posición completamente nueva
   - Todo el terreno es diferente


10.2 CASO 2: VOLVER AL MENÚ Y JUGAR DE NUEVO
---------------------------------------------

Escenario:
   1. Jugador completa partida 1
   2. El tiempo llega a 0:00
   3. Presiona ESC para volver al menú
   4. Selecciona 1 Jugar Solo

Resultado:
   - Nuevo seed generado
   - Mapa completamente diferente
   - Nueva experiencia de juego


10.3 CASO 3: JUEGO TERMINADO -> JUGAR DE NUEVO
----------------------------------------------

Escenario:
   1. Tiempo termina (0:00)
   2. Aparece pantalla de estadísticas
   3. Presiona ENTER para jugar de nuevo

Resultado:
   - Nuevo seed generado
   - Mundo regenerado
   - Experiencia fresca


11. POSIBLES MEJORAS FUTURAS
================================================================================

11.1 SEED PERSONALIZADO
------------------------

Permitir al jugador ingresar un seed específico:

    public void usarSeedPersonalizado(long seedElegido) {
        mapaInfinito.regenerarConNuevoSeed(seedElegido);
    }

Uso: Competencias entre amigos con el mismo mapa


11.2 GUARDAR SEEDS FAVORITOS
-----------------------------

    // Guardar seeds de mapas interesantes
    List<Long> seedsFavoritos = new ArrayList<>();
    seedsFavoritos.add(1731223813308L);


11.3 COMPARTIR SEEDS
--------------------

    // Mostrar el seed actual en pantalla
    System.out.println("Seed actual: " + mapaInfinito.getSeedActual());

Uso: Compartir mapas interesantes con amigos


11.4 DIFICULTAD POR SEED
-------------------------

    // Ciertos rangos de seeds = mapas más difíciles
    if (seed % 2 == 0) {
        // Mapa con más enemigos
    } else {
        // Mapa con más items
    }


11.5 GALERÍA DE MAPAS
----------------------

   - Guardar screenshots de seeds interesantes
   - Calificar mapas
   - Elegir de una galería


12. ESTADISTICAS DE VARIEDAD
================================================================================

Con System.currentTimeMillis() como seed:

   - Seeds posibles: ~9,223,372,036,854,775,807 (Long.MAX_VALUE)
   - Probabilidad de repetición: Prácticamente 0%
   - Seeds por segundo: 1,000 (milisegundos)
   - Seeds por hora: 3,600,000
   - Seeds por día: 86,400,000

Conclusión: Cada jugador puede jugar miles de partidas sin repetir un mapa.


13. CONCLUSION
================================================================================

La regeneración del mapa con un nuevo seed en cada partida transforma el juego 
de una experiencia repetitiva a una aventura única cada vez. Los jugadores 
ahora deben explorar, adaptarse y estrategizar en cada nueva partida, 
aumentando significativamente la rejugabilidad y el valor del juego.


================================================================================
Fecha: 10 de Noviembre, 2024  
Versión: 1.0  
Impacto: Alto - Aumenta significativamente la rejugabilidad
================================================================================
