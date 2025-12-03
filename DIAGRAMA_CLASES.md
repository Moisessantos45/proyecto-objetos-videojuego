# Diagrama de Clases UML del Proyecto

Este documento contiene el diagrama de clases UML en formato Mermaid.
Este diagrama visualiza la estructura estática del sistema, mostrando las clases principales, sus atributos, métodos y las relaciones entre ellas.

## Diagrama

```mermaid
classDiagram
    direction LR

    class GamePanel {
        +GameEngine gameEngine
        +RenderSystem renderSystem
        +InputService inputService
        +GameState estadoJuego
        +paintComponent()
        +actualizar()
    }

    class GameEngine {
        +JugadorSystem jugadorSystem
        -EnemigoSystem enemigoSystem
        +ManejadorMapaInfinito mapaInfinito
        +GameClient gameClient
        +update()
        +reiniciarJuego()
    }
    
    class EntidadModel {
        +Transform transform
        +int mundoX
        +int mundoY
    }

    class JugadorSystem {
        +EntidadModel jugador
        +update()
    }

    class EnemigoSystem {
        +List~EnemigoModel~ enemigos
        +update()
    }
    
    class EnemigoModel {
        +AIState state
    }

    class ManejadorMapaInfinito {
        -Map~String, Chunk~ chunksActivos
        -GeneradorMundo generador
        +actualizarChunksActivos()
        +regenerarConNuevoSeed()
    }

    class GeneradorMundo {
        -long seed
        +generarChunk()
    }

    class RenderSystem {
        +renderTodo()
        +renderJuegoTerminado()
    }
    
    class GameServer {
        -Map~String, ClientHandler~ clientes
        +aceptarConexiones()
    }

    class GameClient {
        -Socket socket
        -Map~String, RemotePlayer~ remotePlayers
        +sendPosition()
    }

    class ClientHandler {
        -Socket socket
        +run()
        +transmitirATodos()
    }

    EnemigoModel --|> EntidadModel

    GamePanel o-- GameEngine
    GamePanel o-- RenderSystem
    
    GameEngine o-- JugadorSystem
    GameEngine o-- EnemigoSystem
    GameEngine o-- ManejadorMapaInfinito
    GameEngine o-- GameClient
    
    ManejadorMapaInfinito o-- GeneradorMundo
    EnemigoSystem "1" -- "*" EnemigoModel : contiene
    
    GameServer "1" -- "*" ClientHandler : gestiona
    GameClient "1" -- "1" GameServer : conecta con
```
