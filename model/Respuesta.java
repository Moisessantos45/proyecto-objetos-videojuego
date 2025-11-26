package model;

public class Respuesta {
    private String inciso;
    private String texto;

    public Respuesta(String inciso, String texto) {
        this.inciso = inciso;
        this.texto = texto;
    }

    public String getInciso() {
        return inciso;
    }

    public String getTexto() {
        return texto;
    }
}
