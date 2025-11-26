package Presentation;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import domain.ManejadorMapaInfinito;
import domain.JugadorSystem;
import model.Acertijo;
import model.Respuesta;

public class HUDRenderer {
    private Font fuenteNormal;
    private Font fuentePequena;
    private Font fuenteTitulo;

    public HUDRenderer() {
        this.fuenteNormal = new Font("Arial", Font.PLAIN, 16);
        this.fuentePequena = new Font("Arial", Font.PLAIN, 12);
        this.fuenteTitulo = new Font("Arial", Font.BOLD, 18);
    }

    public void render(Graphics2D g2, ManejadorMapaInfinito mapa, JugadorSystem jugadorSystem, int tamanioTile, int cantidadEnemigos, int pantallaAncho, int tiempoRestanteSegundos) {
        // --- DIBUJAR BARRA DE VIDA ---
        double vidaMaxima = 100.0; // Asumimos que la vida máxima es 100
        double vidaActual = jugadorSystem.getJugador().getVida();
        double porcentajeVida = vidaActual / vidaMaxima;
        
        int anchoBarra = 200;
        int altoBarra = 20;
        int xBarra = (pantallaAncho / 2) - (anchoBarra / 2);
        int yBarra = 15;

        // Fondo de la barra (rojo)
        g2.setColor(Color.RED);
        g2.fillRect(xBarra, yBarra, anchoBarra, altoBarra);

        // Vida actual (verde)
        g2.setColor(Color.GREEN);
        g2.fillRect(xBarra, yBarra, (int)(anchoBarra * porcentajeVida), altoBarra);

        // Borde de la barra
        g2.setColor(Color.WHITE);
        g2.drawRect(xBarra, yBarra, anchoBarra, altoBarra);

        // --- DIBUJAR TIMER ---
        int minutos = tiempoRestanteSegundos / 60;
        int segundos = tiempoRestanteSegundos % 60;
        String textoTimer = String.format("Tiempo: %d:%02d", minutos, segundos);
        
        g2.setFont(fuenteNormal);
        Color colorTimer = tiempoRestanteSegundos < 30 ? Color.RED : Color.YELLOW;
        g2.setColor(colorTimer);
        int anchoTimer = g2.getFontMetrics().stringWidth(textoTimer);
        g2.drawString(textoTimer, (pantallaAncho - anchoTimer) / 2, yBarra + altoBarra + 25);

        // --- DIBUJAR TEXTO DEBUG ---
        g2.setColor(Color.WHITE);
        g2.setFont(fuentePequena);
        
        g2.drawString(mapa.getEstadisticas(), 10, 30);
        
        int mundoTileX = jugadorSystem.getMundoX() / tamanioTile;
        int mundoTileY = jugadorSystem.getMundoY() / tamanioTile;
        g2.drawString("Posición mundo: (" + mundoTileX + ", " + mundoTileY + ")", 10, 45);
        g2.drawString("Enemigos: " + cantidadEnemigos, 10, 60);
        g2.drawString("Pociones: " + jugadorSystem.getPocionesEnArsenal(), 10, 75);
        g2.drawString("Acertijos resueltos: " + jugadorSystem.getAcertijosResueltos(), 10, 90);
    }

    public void renderFPS(Graphics2D g2, int fps) {
        g2.setColor(Color.YELLOW);
        g2.setFont(fuentePequena);
        g2.drawString("FPS: " + fps, 10, 60);
    }

    public void renderPausa(Graphics2D g2, int pantallaAncho, int pantallaAlto) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, pantallaAncho, pantallaAlto);
        
        g2.setColor(Color.WHITE);
        g2.setFont(fuenteNormal);
        String texto = "PAUSA";
        int textoAncho = g2.getFontMetrics().stringWidth(texto);
        g2.drawString(texto, (pantallaAncho - textoAncho) / 2, pantallaAlto / 2);
    }

    public void renderMenuPausa(Graphics2D g2, int pantallaAncho, int pantallaAlto) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, pantallaAncho, pantallaAlto);

        g2.setFont(fuenteTitulo);
        g2.setColor(Color.WHITE);

        String titulo = "Menú de Pausa";
        int anchoTitulo = g2.getFontMetrics().stringWidth(titulo);
        g2.drawString(titulo, (pantallaAncho - anchoTitulo) / 2, pantallaAlto / 2 - 100);

        g2.setFont(fuenteNormal);
        String opcion1 = "[1] Reanudar";
        String opcion2 = "[2] Reiniciar";
        String opcion3 = "[3] Menú Principal";
        String opcion4 = "[ESC] Volver";

        int y = pantallaAlto / 2 - 20;
        g2.drawString(opcion1, (pantallaAncho - g2.getFontMetrics().stringWidth(opcion1)) / 2, y);
        y += 30;
        g2.drawString(opcion2, (pantallaAncho - g2.getFontMetrics().stringWidth(opcion2)) / 2, y);
        y += 30;
        g2.drawString(opcion3, (pantallaAncho - g2.getFontMetrics().stringWidth(opcion3)) / 2, y);
        y += 30;
        g2.drawString(opcion4, (pantallaAncho - g2.getFontMetrics().stringWidth(opcion4)) / 2, y);
    }

    public void renderBienvenida(Graphics2D g2, int pantallaAncho, int pantallaAlto) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, pantallaAncho, pantallaAlto);

        g2.setFont(fuenteTitulo);
        g2.setColor(Color.WHITE);

        String titulo = "Bienvenido a la Aventura";
        int anchoTitulo = g2.getFontMetrics().stringWidth(titulo);
        g2.drawString(titulo, (pantallaAncho - anchoTitulo) / 2, pantallaAlto / 2 - 50);

        g2.setFont(fuenteNormal);
        String instruccion = "Presiona ENTER para comenzar";
        int anchoInstruccion = g2.getFontMetrics().stringWidth(instruccion);
        g2.drawString(instruccion, (pantallaAncho - anchoInstruccion) / 2, pantallaAlto / 2);
    }

    public void renderMenuPrincipal(Graphics2D g2, int pantallaAncho, int pantallaAlto) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, pantallaAncho, pantallaAlto);

        g2.setFont(fuenteTitulo);
        g2.setColor(Color.WHITE);

        String titulo = "Menú Principal";
        int anchoTitulo = g2.getFontMetrics().stringWidth(titulo);
        g2.drawString(titulo, (pantallaAncho - anchoTitulo) / 2, pantallaAlto / 2 - 100);

        g2.setFont(fuenteNormal);
        String opcion1 = "[1] Iniciar Partida";
        String opcion2 = "[2] Crear Servidor";
        String opcion3 = "[3] Unirse a Servidor";
        String opcion4 = "[4] Salir";

        int y = pantallaAlto / 2 - 20;
        g2.drawString(opcion1, (pantallaAncho - g2.getFontMetrics().stringWidth(opcion1)) / 2, y);
        y += 30;
        g2.drawString(opcion2, (pantallaAncho - g2.getFontMetrics().stringWidth(opcion2)) / 2, y);
        y += 30;
        g2.drawString(opcion3, (pantallaAncho - g2.getFontMetrics().stringWidth(opcion3)) / 2, y);
        y += 30;
        g2.drawString(opcion4, (pantallaAncho - g2.getFontMetrics().stringWidth(opcion4)) / 2, y);
    }

    public void renderCofreModal(Graphics2D g2, int pantallaAncho, int pantallaAlto, Acertijo acertijo) {
        // Fondo semitransparente
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, pantallaAncho, pantallaAlto);

        if (acertijo == null) {
            renderErrorModal(g2, pantallaAncho, pantallaAlto);
            return;
        }

        // Ventana del modal más grande para el acertijo
        int anchoModal = 600;
        int altoModal = 350;
        int xModal = (pantallaAncho - anchoModal) / 2;
        int yModal = (pantallaAlto - altoModal) / 2;

        g2.setColor(new Color(40, 40, 40));
        g2.fillRoundRect(xModal, yModal, anchoModal, altoModal, 15, 15);

        g2.setColor(new Color(200, 150, 50));
        g2.drawRoundRect(xModal, yModal, anchoModal, altoModal, 15, 15);

        // Título
        g2.setColor(new Color(255, 215, 0));
        g2.setFont(fuenteTitulo);
        String titulo = "¡Has encontrado un cofre!";
        int tituloAncho = g2.getFontMetrics().stringWidth(titulo);
        g2.drawString(titulo, (pantallaAncho - tituloAncho) / 2, yModal + 35);

        // Dificultad
        g2.setFont(fuentePequena);
        g2.setColor(Color.LIGHT_GRAY);
        String dificultad = "Dificultad: " + acertijo.getDificultad();
        int dificultadAncho = g2.getFontMetrics().stringWidth(dificultad);
        g2.drawString(dificultad, (pantallaAncho - dificultadAncho) / 2, yModal + 55);

        // Pregunta con salto de línea si es muy larga
        g2.setColor(Color.WHITE);
        g2.setFont(fuenteNormal);
        String pregunta = acertijo.getPregunta();
        int maxAnchoPregunta = anchoModal - 40;
        int yPregunta = yModal + 90;
        
        drawWrappedText(g2, pregunta, xModal + 20, yPregunta, maxAnchoPregunta);

        // Respuestas
        g2.setFont(fuenteNormal);
        int yRespuesta = yModal + 170;
        int espaciadoRespuesta = 35;

        for (Respuesta respuesta : acertijo.getRespuestas()) {
            g2.setColor(new Color(100, 150, 200));
            g2.fillRoundRect(xModal + 30, yRespuesta - 18, anchoModal - 60, 28, 5, 5);
            
            g2.setColor(Color.WHITE);
            g2.drawString(respuesta.getInciso() + ") " + respuesta.getTexto(), xModal + 40, yRespuesta);
            yRespuesta += espaciadoRespuesta;
        }

        // Instrucción
        g2.setFont(fuentePequena);
        g2.setColor(Color.YELLOW);
        String instruccion = "(Presiona Enter para continuar)";
        int instruccionAncho = g2.getFontMetrics().stringWidth(instruccion);
        g2.drawString(instruccion, (pantallaAncho - instruccionAncho) / 2, yModal + altoModal - 20);
    }

    private void drawWrappedText(Graphics2D g2, String texto, int x, int y, int maxAncho) {
        String[] palabras = texto.split(" ");
        StringBuilder lineaActual = new StringBuilder();
        int yActual = y;

        for (String palabra : palabras) {
            String testLinea = lineaActual.length() == 0 ? palabra : lineaActual + " " + palabra;
            int anchoLinea = g2.getFontMetrics().stringWidth(testLinea);

            if (anchoLinea > maxAncho && lineaActual.length() > 0) {
                g2.drawString(lineaActual.toString(), x, yActual);
                lineaActual = new StringBuilder(palabra);
                yActual += 20;
            } else {
                lineaActual = new StringBuilder(testLinea);
            }
        }

        if (lineaActual.length() > 0) {
            g2.drawString(lineaActual.toString(), x, yActual);
        }
    }

    private void renderErrorModal(Graphics2D g2, int pantallaAncho, int pantallaAlto) {
        int anchoModal = 400;
        int altoModal = 150;
        int xModal = (pantallaAncho - anchoModal) / 2;
        int yModal = (pantallaAlto - altoModal) / 2;

        g2.setColor(Color.DARK_GRAY);
        g2.fillRoundRect(xModal, yModal, anchoModal, altoModal, 15, 15);

        g2.setColor(Color.WHITE);
        g2.drawRoundRect(xModal, yModal, anchoModal, altoModal, 15, 15);

        g2.setFont(fuenteNormal);
        g2.setColor(Color.RED);
        String texto = "Error: No se pudo cargar el acertijo";
        int textoAncho = g2.getFontMetrics().stringWidth(texto);
        g2.drawString(texto, (pantallaAncho - textoAncho) / 2, yModal + 70);

        g2.setFont(fuentePequena);
        g2.setColor(Color.WHITE);
        String texto2 = "(Presiona Enter para continuar)";
        int textoAncho2 = g2.getFontMetrics().stringWidth(texto2);
        g2.drawString(texto2, (pantallaAncho - textoAncho2) / 2, yModal + 110);
    }

    public void renderRespuestaCorrecta(Graphics2D g2, int pantallaAncho, int pantallaAlto) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, pantallaAncho, pantallaAlto);

        int anchoModal = 400;
        int altoModal = 200;
        int xModal = (pantallaAncho - anchoModal) / 2;
        int yModal = (pantallaAlto - altoModal) / 2;

        g2.setColor(new Color(40, 40, 40));
        g2.fillRoundRect(xModal, yModal, anchoModal, altoModal, 15, 15);

        g2.setColor(new Color(50, 200, 50));
        g2.drawRoundRect(xModal, yModal, anchoModal, altoModal, 15, 15);

        g2.setFont(fuenteTitulo);
        g2.setColor(Color.GREEN);
        String titulo = "¡Respuesta Correcta!";
        int tituloAncho = g2.getFontMetrics().stringWidth(titulo);
        g2.drawString(titulo, (pantallaAncho - tituloAncho) / 2, yModal + 60);

        g2.setFont(fuenteNormal);
        g2.setColor(Color.WHITE);
        String instruccion = "Presiona ENTER para continuar";
        int instruccionAncho = g2.getFontMetrics().stringWidth(instruccion);
        g2.drawString(instruccion, (pantallaAncho - instruccionAncho) / 2, yModal + 120);
    }

    public void renderRespuestaIncorrecta(Graphics2D g2, int pantallaAncho, int pantallaAlto, int intentosRestantes) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, pantallaAncho, pantallaAlto);

        int anchoModal = 450;
        int altoModal = 250;
        int xModal = (pantallaAncho - anchoModal) / 2;
        int yModal = (pantallaAlto - altoModal) / 2;

        g2.setColor(new Color(40, 40, 40));
        g2.fillRoundRect(xModal, yModal, anchoModal, altoModal, 15, 15);

        g2.setColor(new Color(200, 50, 50));
        g2.drawRoundRect(xModal, yModal, anchoModal, altoModal, 15, 15);

        g2.setFont(fuenteTitulo);
        g2.setColor(Color.RED);
        String titulo = "Respuesta Incorrecta";
        int tituloAncho = g2.getFontMetrics().stringWidth(titulo);
        g2.drawString(titulo, (pantallaAncho - tituloAncho) / 2, yModal + 60);

        g2.setFont(fuenteNormal);
        g2.setColor(Color.WHITE);
        
        String mensaje;
        if (intentosRestantes > 0) {
            mensaje = "Te quedan " + intentosRestantes + " intento(s)";
        } else {
            mensaje = "No te quedan más intentos en este cofre";
        }
        
        int mensajeAncho = g2.getFontMetrics().stringWidth(mensaje);
        g2.drawString(mensaje, (pantallaAncho - mensajeAncho) / 2, yModal + 110);

        g2.setFont(fuentePequena);
        g2.setColor(Color.YELLOW);
        String instruccion = "Presiona ENTER para continuar";
        int instruccionAncho = g2.getFontMetrics().stringWidth(instruccion);
        g2.drawString(instruccion, (pantallaAncho - instruccionAncho) / 2, yModal + 180);
    }

    public void renderCrearServidor(Graphics2D g2, int pantallaAncho, int pantallaAlto) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, pantallaAncho, pantallaAlto);

        g2.setFont(fuenteTitulo);
        g2.setColor(Color.WHITE);

        String titulo = "Crear Servidor";
        int anchoTitulo = g2.getFontMetrics().stringWidth(titulo);
        g2.drawString(titulo, (pantallaAncho - anchoTitulo) / 2, pantallaAlto / 2 - 100);

        g2.setFont(fuenteNormal);
        int y = pantallaAlto / 2;
        
        g2.setColor(Color.CYAN);
        String mensaje = "Se generará un ID único para tu servidor";
        int anchoMensaje = g2.getFontMetrics().stringWidth(mensaje);
        g2.drawString(mensaje, (pantallaAncho - anchoMensaje) / 2, y);
        
        y += 50;
        g2.setColor(Color.YELLOW);
        String mensaje2 = "Compártelo con otros jugadores para que se unan";
        int anchoMensaje2 = g2.getFontMetrics().stringWidth(mensaje2);
        g2.drawString(mensaje2, (pantallaAncho - anchoMensaje2) / 2, y);
        
        y += 70;
        g2.setColor(Color.GREEN);
        String boton = "[ENTER] Crear Servidor";
        int anchoBoton = g2.getFontMetrics().stringWidth(boton);
        g2.drawString(boton, (pantallaAncho - anchoBoton) / 2, y);
        
        y += 30;
        g2.setColor(Color.LIGHT_GRAY);
        g2.setFont(fuentePequena);
        String instruccion = "ESC para volver al menú";
        int anchoInst = g2.getFontMetrics().stringWidth(instruccion);
        g2.drawString(instruccion, (pantallaAncho - anchoInst) / 2, y);
    }

    public void renderSalaEsperaHost(Graphics2D g2, int pantallaAncho, int pantallaAlto, 
                                      int usuariosConectados, String serverID, boolean mostrarMensajeCopiado) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, pantallaAncho, pantallaAlto);

        g2.setFont(fuenteTitulo);
        g2.setColor(Color.WHITE);

        String titulo = "Sala de Espera - HOST";
        int anchoTitulo = g2.getFontMetrics().stringWidth(titulo);
        g2.drawString(titulo, (pantallaAncho - anchoTitulo) / 2, pantallaAlto / 2 - 130);

        g2.setFont(fuenteNormal);
        int y = pantallaAlto / 2 - 70;
        
        g2.setColor(Color.YELLOW);
        String labelID = "ID del Servidor:";
        int anchoLabel = g2.getFontMetrics().stringWidth(labelID);
        g2.drawString(labelID, (pantallaAncho - anchoLabel) / 2, y);
        
        y += 30;
        g2.setFont(new Font("Monospaced", Font.BOLD, 24));
        g2.setColor(Color.CYAN);
        int anchoID = g2.getFontMetrics().stringWidth(serverID);
        g2.drawString(serverID, (pantallaAncho - anchoID) / 2, y);
        
        y += 10;
        g2.setFont(fuenteNormal);
        g2.setColor(Color.ORANGE);
        String botonCopiar = "[C] Copiar ID";
        int anchoCopiar = g2.getFontMetrics().stringWidth(botonCopiar);
        g2.drawString(botonCopiar, (pantallaAncho - anchoCopiar) / 2, y);
        
        if (mostrarMensajeCopiado) {
            y += 25;
            g2.setColor(Color.GREEN);
            g2.setFont(fuentePequena);
            String copiado = "✓ ID copiado al portapapeles";
            int anchoCopiado = g2.getFontMetrics().stringWidth(copiado);
            g2.drawString(copiado, (pantallaAncho - anchoCopiado) / 2, y);
        }
        
        y += 40;
        g2.setFont(fuenteNormal);
        g2.setColor(Color.CYAN);
        String usuarios = "Usuarios conectados: " + usuariosConectados;
        int anchoUsuarios = g2.getFontMetrics().stringWidth(usuarios);
        g2.drawString(usuarios, (pantallaAncho - anchoUsuarios) / 2, y);
        
        y += 50;
        g2.setColor(Color.GREEN);
        String botonPlay = "[ENTER] Iniciar Partida";
        int anchoPlay = g2.getFontMetrics().stringWidth(botonPlay);
        g2.drawString(botonPlay, (pantallaAncho - anchoPlay) / 2, y);
        
        y += 30;
        g2.setColor(Color.RED);
        String botonCancelar = "[ESC] Cancelar";
        int anchoCancelar = g2.getFontMetrics().stringWidth(botonCancelar);
        g2.drawString(botonCancelar, (pantallaAncho - anchoCancelar) / 2, y);
    }

    public void renderUnirseServidor(Graphics2D g2, int pantallaAncho, int pantallaAlto, String serverID) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, pantallaAncho, pantallaAlto);

        g2.setFont(fuenteTitulo);
        g2.setColor(Color.WHITE);

        String titulo = "Unirse a Servidor";
        int anchoTitulo = g2.getFontMetrics().stringWidth(titulo);
        g2.drawString(titulo, (pantallaAncho - anchoTitulo) / 2, pantallaAlto / 2 - 100);

        g2.setFont(fuenteNormal);
        int y = pantallaAlto / 2 - 30;
        
        g2.setColor(Color.YELLOW);
        String label = "Ingresa el ID del Servidor:";
        int anchoLabel = g2.getFontMetrics().stringWidth(label);
        g2.drawString(label, (pantallaAncho - anchoLabel) / 2, y);
        
        y += 40;
        int anchoInput = 280;
        int xInput = (pantallaAncho - anchoInput) / 2;
        g2.setColor(Color.WHITE);
        g2.fillRect(xInput, y - 25, anchoInput, 35);
        g2.setColor(new Color(100, 100, 255));
        g2.drawRect(xInput, y - 25, anchoInput, 35);
        
        g2.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2.setColor(Color.BLACK);
        g2.drawString(serverID + "|", xInput + 10, y);
        
        y += 50;
        g2.setFont(fuentePequena);
        g2.setColor(Color.LIGHT_GRAY);
        String info = "Formato: XXXX-XXXX (8 caracteres)";
        int anchoInfo = g2.getFontMetrics().stringWidth(info);
        g2.drawString(info, (pantallaAncho - anchoInfo) / 2, y);
        
        y += 50;
        g2.setFont(fuenteNormal);
        if (serverID.length() >= 8) {
            g2.setColor(Color.GREEN);
            String boton = "[ENTER] Conectarse";
            int anchoBoton = g2.getFontMetrics().stringWidth(boton);
            g2.drawString(boton, (pantallaAncho - anchoBoton) / 2, y);
        } else {
            g2.setColor(Color.GRAY);
            String boton = "[ENTER] Conectarse (ingresa un ID válido)";
            int anchoBoton = g2.getFontMetrics().stringWidth(boton);
            g2.drawString(boton, (pantallaAncho - anchoBoton) / 2, y);
        }
        
        y += 30;
        g2.setColor(Color.LIGHT_GRAY);
        g2.setFont(fuentePequena);
        String instruccion = "ESC para volver al menú";
        int anchoInst = g2.getFontMetrics().stringWidth(instruccion);
        g2.drawString(instruccion, (pantallaAncho - anchoInst) / 2, y);
    }

    public void renderSalaEsperaCliente(Graphics2D g2, int pantallaAncho, int pantallaAlto, int usuariosConectados) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, pantallaAncho, pantallaAlto);

        g2.setFont(fuenteTitulo);
        g2.setColor(Color.WHITE);

        String titulo = "Esperando al Host...";
        int anchoTitulo = g2.getFontMetrics().stringWidth(titulo);
        g2.drawString(titulo, (pantallaAncho - anchoTitulo) / 2, pantallaAlto / 2 - 100);

        g2.setFont(fuenteNormal);
        int y = pantallaAlto / 2 - 30;
        
        g2.setColor(Color.CYAN);
        String usuarios = "Usuarios conectados: " + usuariosConectados;
        int anchoUsuarios = g2.getFontMetrics().stringWidth(usuarios);
        g2.drawString(usuarios, (pantallaAncho - anchoUsuarios) / 2, y);
        
        y += 50;
        g2.setColor(Color.YELLOW);
        g2.setFont(fuentePequena);
        String mensaje = "Esperando a que el host inicie la partida...";
        int anchoMensaje = g2.getFontMetrics().stringWidth(mensaje);
        g2.drawString(mensaje, (pantallaAncho - anchoMensaje) / 2, y);
        
        y += 50;
        g2.setColor(Color.RED);
        g2.setFont(fuenteNormal);
        String botonCancelar = "[ESC] Desconectar";
        int anchoCancelar = g2.getFontMetrics().stringWidth(botonCancelar);
        g2.drawString(botonCancelar, (pantallaAncho - anchoCancelar) / 2, y);
    }
    
    public void renderJuegoTerminado(Graphics2D g2, int pantallaAncho, int pantallaAlto, JugadorSystem jugadorSystem) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, pantallaAncho, pantallaAlto);

        g2.setFont(new Font("Arial", Font.BOLD, 48));
        g2.setColor(Color.RED);
        String titulo = "¡TIEMPO TERMINADO!";
        int anchoTitulo = g2.getFontMetrics().stringWidth(titulo);
        g2.drawString(titulo, (pantallaAncho - anchoTitulo) / 2, pantallaAlto / 2 - 150);

        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(Color.YELLOW);
        String subtitulo = "Estadísticas Finales";
        int anchoSubtitulo = g2.getFontMetrics().stringWidth(subtitulo);
        g2.drawString(subtitulo, (pantallaAncho - anchoSubtitulo) / 2, pantallaAlto / 2 - 80);

        g2.setFont(fuenteNormal);
        g2.setColor(Color.WHITE);
        int y = pantallaAlto / 2 - 30;
        int lineHeight = 35;

        String vida = "Vida final: " + (int)jugadorSystem.getJugador().getVida() + " HP";
        int anchoVida = g2.getFontMetrics().stringWidth(vida);
        g2.drawString(vida, (pantallaAncho - anchoVida) / 2, y);
        
        y += lineHeight;
        g2.setColor(Color.CYAN);
        String pociones = "Pociones en arsenal: " + jugadorSystem.getPocionesEnArsenal();
        int anchoPociones = g2.getFontMetrics().stringWidth(pociones);
        g2.drawString(pociones, (pantallaAncho - anchoPociones) / 2, y);
        
        y += lineHeight;
        g2.setColor(Color.GREEN);
        String acertijos = "Acertijos resueltos: " + jugadorSystem.getAcertijosResueltos();
        int anchoAcertijos = g2.getFontMetrics().stringWidth(acertijos);
        g2.drawString(acertijos, (pantallaAncho - anchoAcertijos) / 2, y);

        y += lineHeight + 30;
        g2.setFont(fuenteNormal);
        g2.setColor(Color.YELLOW);
        String reiniciar = "[ENTER] Jugar de nuevo";
        int anchoReiniciar = g2.getFontMetrics().stringWidth(reiniciar);
        g2.drawString(reiniciar, (pantallaAncho - anchoReiniciar) / 2, y);
        
        y += 30;
        g2.setColor(Color.RED);
        String menu = "[ESC] Menú Principal";
        int anchoMenu = g2.getFontMetrics().stringWidth(menu);
        g2.drawString(menu, (pantallaAncho - anchoMenu) / 2, y);
    }
}
