package Presentation;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.List;
import model.PlayerStats;
import domain.RemotePlayer;
import java.util.Map;

/**
 * Renderiza el HUD multijugador con estadísticas de todos los jugadores
 */
public class MultiplayerHUDRenderer {
    private static final int BARRA_ANCHO = 200;
    private static final int BARRA_ALTO = 20;
    private static final int SPACING = 10;
    private static final int MARGEN = 15;
    
    private Font fontNombre;
    private Font fontEstadisticas;

    public MultiplayerHUDRenderer() {
        this.fontNombre = new Font("Arial", Font.BOLD, 14);
        this.fontEstadisticas = new Font("Arial", Font.PLAIN, 12);
    }

    /**
     * Renderiza el HUD del jugador local en la esquina superior izquierda
     */
    public void renderJugadorLocal(Graphics2D g2, PlayerStats statsLocal, 
                                   int pantallaAncho, int pantallaAlto) {
        if (statsLocal == null) {
            return;  // No renderizar si no hay stats disponibles
        }
        
        int x = MARGEN;
        int y = MARGEN;
        
        renderJugadorCompacto(g2, statsLocal, x, y, true);
    }

    /**
     * Renderiza los HUDs de los jugadores remotos en columna vertical
     */
    public void renderJugadoresRemotos(Graphics2D g2, 
                                       Map<String, RemotePlayer> remotePlayers,
                                       int pantallaAncho, int pantallaAlto) {
        if (remotePlayers == null || remotePlayers.isEmpty()) {
            return;
        }

        int x = MARGEN;
        int y = MARGEN;
        int numeroJugador = 0;

        // Renderizar cada jugador remoto
        for (RemotePlayer remotePlayer : remotePlayers.values()) {
            PlayerStats stats = remotePlayer.getStats();
            renderJugadorCompacto(g2, stats, x, y, false);
            y += BARRA_ALTO + SPACING + 30; // Alto para nombre, barra, acertijos
            numeroJugador++;
            
            // Si hay muchos jugadores, cambiar a siguiente columna
            if (numeroJugador >= 5) {
                x += BARRA_ANCHO + 30;
                y = MARGEN;
                numeroJugador = 0;
            }
        }
    }

    /**
     * Renderiza un jugador en formato compacto
     */
    private void renderJugadorCompacto(Graphics2D g2, PlayerStats stats, 
                                       int x, int y, boolean esLocal) {
        // Nombre del jugador
        g2.setFont(fontNombre);
        g2.setColor(esLocal ? Color.GREEN : Color.YELLOW);
        g2.drawString(stats.getAlias(), x, y);
        y += 20;

        // Fondo de la barra de vida
        g2.setColor(new Color(50, 50, 50));
        g2.fillRect(x, y, BARRA_ANCHO, BARRA_ALTO);

        // Barra de vida
        float porcentajeVida = stats.getPorcentajeVida();
        int anchoBarra = (int) (BARRA_ANCHO * porcentajeVida);
        
        Color colorVida;
        if (porcentajeVida > 0.5f) {
            colorVida = new Color(0, 200, 0); // Verde
        } else if (porcentajeVida > 0.25f) {
            colorVida = new Color(255, 200, 0); // Naranja
        } else {
            colorVida = new Color(255, 0, 0); // Rojo
        }
        
        g2.setColor(colorVida);
        g2.fillRect(x, y, anchoBarra, BARRA_ALTO);

        // Borde de la barra
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRect(x, y, BARRA_ANCHO, BARRA_ALTO);

        // Texto de vida dentro de la barra
        g2.setFont(fontEstadisticas);
        g2.setColor(Color.WHITE);
        String textVida = stats.getVida() + "/" + stats.getVidaMaxima();
        int textWidth = g2.getFontMetrics().stringWidth(textVida);
        g2.drawString(textVida, x + (BARRA_ANCHO - textWidth) / 2, y + BARRA_ALTO - 4);

        y += BARRA_ALTO + 5;

        // Estadísticas: Acertijos resueltos
        g2.setFont(fontEstadisticas);
        g2.setColor(Color.WHITE);
        String textAcertijos = "Acertijos: " + stats.getAcertijosResueltos();
        g2.drawString(textAcertijos, x, y);

        // Indicador LOCAL o REMOTO
        if (esLocal) {
            g2.setColor(new Color(0, 200, 0, 150));
            g2.fillRect(x, y - 25, 60, 15);
            g2.setColor(Color.WHITE);
            g2.drawString("(Local)", x + 5, y - 12);
        }
    }

    /**
     * Renderiza un ranking global de todos los jugadores
     */
    public void renderRanking(Graphics2D g2, PlayerStats statsLocal,
                              Map<String, RemotePlayer> remotePlayers,
                              int pantallaAncho, int pantallaAlto) {
        List<PlayerStats> todosLosJugadores = new ArrayList<>();
        todosLosJugadores.add(statsLocal);
        
        if (remotePlayers != null) {
            for (RemotePlayer remote : remotePlayers.values()) {
                todosLosJugadores.add(remote.getStats());
            }
        }

        // Ordenar por acertijos resueltos (descendente)
        todosLosJugadores.sort((a, b) -> 
            Integer.compare(b.getAcertijosResueltos(), a.getAcertijosResueltos())
        );

        // Renderizar ranking en la esquina superior derecha
        int x = pantallaAncho - 250;
        int y = MARGEN;

        // Título
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(new Color(255, 215, 0)); // Dorado
        g2.drawString("RANKING", x, y);
        y += 25;

        // Línea separadora
        g2.setColor(new Color(255, 215, 0));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(x, y, x + 220, y);
        y += 15;

        // Listar jugadores
        g2.setFont(fontEstadisticas);
        int posicion = 1;
        for (PlayerStats stats : todosLosJugadores) {
            g2.setColor(Color.WHITE);
            String rankText = String.format("%d. %s - %d acertijos (Vida: %d)",
                posicion, stats.getAlias(), stats.getAcertijosResueltos(), stats.getVida());
            g2.drawString(rankText, x, y);
            y += 18;
            posicion++;
        }
    }

    /**
     * Renderiza información de distancia a jugadores cercanos (minimapa local)
     */
    public void renderDistanciaJugadores(Graphics2D g2, PlayerStats statsLocal,
                                        Map<String, RemotePlayer> remotePlayers,
                                        int pantallaAncho, int pantallaAlto) {
        if (remotePlayers == null || remotePlayers.isEmpty()) {
            return;
        }

        int x = pantallaAncho - 250;
        int y = pantallaAlto - 150;

        // Título
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(new Color(200, 200, 255));
        g2.drawString("Jugadores Cercanos", x, y);
        y += 20;

        // Línea separadora
        g2.setColor(new Color(200, 200, 255));
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(x, y, x + 200, y);
        y += 15;

        // Calcular distancias
        int myX = statsLocal.getPosicionX();
        int myY = statsLocal.getPosicionY();

        for (RemotePlayer remote : remotePlayers.values()) {
            PlayerStats stats = remote.getStats();
            int otherX = stats.getPosicionX();
            int otherY = stats.getPosicionY();

            double distancia = Math.sqrt(
                Math.pow(myX - otherX, 2) + Math.pow(myY - otherY, 2)
            );

            // Convertir a tiles (asumiendo tile de 32px)
            int tiles = (int) (distancia / 32);

            // Determinar color según distancia
            Color colorDistancia;
            if (tiles < 5) {
                colorDistancia = new Color(255, 0, 0); // Muy cerca - Rojo
            } else if (tiles < 15) {
                colorDistancia = new Color(255, 200, 0); // Cerca - Naranja
            } else if (tiles < 30) {
                colorDistancia = new Color(200, 200, 255); // Lejano - Azul
            } else {
                colorDistancia = new Color(100, 100, 100); // Muy lejano - Gris
            }

            g2.setFont(fontEstadisticas);
            g2.setColor(colorDistancia);
            String distText = String.format("%s: %d tiles", stats.getAlias(), tiles);
            g2.drawString(distText, x, y);
            y += 18;
        }
    }
}
