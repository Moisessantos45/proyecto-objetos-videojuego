package model;

/**
 * Encapsula estadísticas de un jugador (local o remoto)
 */
public class PlayerStats {
    private String playerId;
    private String alias;
    private int vida;
    private int vidaMaxima;
    private int acertijosResueltos;
    private int posicionX;
    private int posicionY;
    private boolean esLocal;
    private long ultimaActualizacion;

    public PlayerStats(String playerId, String alias, int vidaMaxima) {
        this.playerId = playerId;
        this.alias = alias != null ? alias : "Jugador_" + playerId.substring(0, 8);
        this.vida = vidaMaxima;
        this.vidaMaxima = vidaMaxima;
        this.acertijosResueltos = 0;
        this.posicionX = 0;
        this.posicionY = 0;
        this.esLocal = false;
        this.ultimaActualizacion = System.currentTimeMillis();
    }

    // Getters y Setters
    public String getPlayerId() {
        return playerId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = Math.max(0, Math.min(vida, vidaMaxima));
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public void setVidaMaxima(int vidaMaxima) {
        this.vidaMaxima = vidaMaxima;
    }

    public int getAcertijosResueltos() {
        return acertijosResueltos;
    }

    public void setAcertijosResueltos(int acertijosResueltos) {
        this.acertijosResueltos = acertijosResueltos;
    }

    public void incrementarAcertijosResueltos() {
        this.acertijosResueltos++;
    }

    public int getPosicionX() {
        return posicionX;
    }

    public void setPosicionX(int x) {
        this.posicionX = x;
    }

    public int getPosicionY() {
        return posicionY;
    }

    public void setPosicionY(int y) {
        this.posicionY = y;
    }

    public boolean esLocal() {
        return esLocal;
    }

    public void setEsLocal(boolean esLocal) {
        this.esLocal = esLocal;
    }

    public long getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void actualizarTimestamp() {
        this.ultimaActualizacion = System.currentTimeMillis();
    }

    public float getPorcentajeVida() {
        return (float) vida / vidaMaxima;
    }

    @Override
    public String toString() {
        return String.format("%s [%d/%d] Acertijos: %d", 
            alias, vida, vidaMaxima, acertijosResueltos);
    }
}
