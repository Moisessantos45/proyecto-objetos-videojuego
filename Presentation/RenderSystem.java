package Presentation;

import java.awt.Graphics2D;
import domain.ManejadorMapaInfinito;
import domain.CamaraSystem;
import domain.JugadorSystem;
import domain.EnemigoSystem;
import domain.MapaInfinitoAdapter;
import model.EntidadModel;

public class RenderSystem {
    private EntidadRenderer entidadRenderer;
    private EnemigoRenderer enemigoRenderer;
    private HUDRenderer hudRenderer;
    private int tamanioTile;

    public RenderSystem(int tamanioTile) {
        this.tamanioTile = tamanioTile;
        this.entidadRenderer = new EntidadRenderer(tamanioTile);
        this.enemigoRenderer = new EnemigoRenderer(tamanioTile);
        this.hudRenderer = new HUDRenderer();
    }

    public void renderTodo(Graphics2D g2, 
                           ManejadorMapaInfinito mapa,
                           CamaraSystem camara,
                           JugadorSystem jugadorSystem,
                           EnemigoSystem enemigoSystem,
                           java.util.Map<String, domain.RemotePlayer> remotePlayers,
                           int pantallaAncho,
                           int pantallaAlto,
                           int tiempoRestanteSegundos) {
        
        mapa.draw(g2, camara.getCamaraX(), camara.getCamaraY());
        
        // Renderizar jugadores remotos
        if (remotePlayers != null) {
            for (domain.RemotePlayer remote : remotePlayers.values()) {
                EntidadModel entidad = remote.getEntidad();
                int screenX = entidad.getMundoX() - camara.getCamaraX();
                int screenY = entidad.getMundoY() - camara.getCamaraY();
                
                // Solo renderizar si está en pantalla
                if (screenX > -tamanioTile && screenX < pantallaAncho &&
                    screenY > -tamanioTile && screenY < pantallaAlto) {
                    entidadRenderer.render(g2, entidad, screenX, screenY);
                }
            }
        }
        
        enemigoRenderer.renderTodos(g2, enemigoSystem.getEnemigos(), 
                                    camara.getCamaraX(), camara.getCamaraY());
        
        EntidadModel jugador = jugadorSystem.getJugador();
        int jugadorPantallaX = jugador.getTransform().getX();
        int jugadorPantallaY = jugador.getTransform().getY();
        entidadRenderer.render(g2, jugador, jugadorPantallaX, jugadorPantallaY);
        
        hudRenderer.render(g2, mapa, 
                          jugadorSystem, 
                          tamanioTile,
                          enemigoSystem.getCantidadEnemigos(),
                          pantallaAncho,
                          tiempoRestanteSegundos);
    }

    public void renderPausa(Graphics2D g2, int pantallaAncho, int pantallaAlto) {
        hudRenderer.renderPausa(g2, pantallaAncho, pantallaAlto);
    }

    public void renderMenuPausa(Graphics2D g2, int pantallaAncho, int pantallaAlto) {
        hudRenderer.renderMenuPausa(g2, pantallaAncho, pantallaAlto);
    }

    public void renderBienvenida(Graphics2D g2, int pantallaAncho, int pantallaAlto) {
        hudRenderer.renderBienvenida(g2, pantallaAncho, pantallaAlto);
    }

    public void renderMenuPrincipal(Graphics2D g2, int pantallaAncho, int pantallaAlto) {
        hudRenderer.renderMenuPrincipal(g2, pantallaAncho, pantallaAlto);
    }

    public void renderCrearServidor(Graphics2D g2, int pantallaAncho, int pantallaAlto) {
        hudRenderer.renderCrearServidor(g2, pantallaAncho, pantallaAlto);
    }

    public void renderSalaEsperaHost(Graphics2D g2, int pantallaAncho, int pantallaAlto, 
                                      int usuariosConectados, String serverID, boolean mostrarMensajeCopiado) {
        hudRenderer.renderSalaEsperaHost(g2, pantallaAncho, pantallaAlto, usuariosConectados, serverID, mostrarMensajeCopiado);
    }

    public void renderUnirseServidor(Graphics2D g2, int pantallaAncho, int pantallaAlto, String serverID) {
        hudRenderer.renderUnirseServidor(g2, pantallaAncho, pantallaAlto, serverID);
    }

    public void renderSalaEsperaCliente(Graphics2D g2, int pantallaAncho, int pantallaAlto, int usuariosConectados) {
        hudRenderer.renderSalaEsperaCliente(g2, pantallaAncho, pantallaAlto, usuariosConectados);
    }

    public void renderRespuestaCorrecta(Graphics2D g2, int pantallaAncho, int pantallaAlto) {
        hudRenderer.renderRespuestaCorrecta(g2, pantallaAncho, pantallaAlto);
    }

    public void renderRespuestaIncorrecta(Graphics2D g2, int pantallaAncho, int pantallaAlto, int intentosRestantes) {
        hudRenderer.renderRespuestaIncorrecta(g2, pantallaAncho, pantallaAlto, intentosRestantes);
    }

    public EntidadRenderer getEntidadRenderer() {
        return entidadRenderer;
    }

    public EnemigoRenderer getEnemigoRenderer() {
        return enemigoRenderer;
    }

    public HUDRenderer getHudRenderer() {
        return hudRenderer;
    }
    
    public void renderJuegoTerminado(Graphics2D g2, int pantallaAncho, int pantallaAlto, JugadorSystem jugadorSystem) {
        hudRenderer.renderJuegoTerminado(g2, pantallaAncho, pantallaAlto, jugadorSystem);
    }
}
