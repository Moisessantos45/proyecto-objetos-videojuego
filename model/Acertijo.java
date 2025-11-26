package model;

import java.util.List;

public class Acertijo {
    private int id;
    private String dificultad;
    private String pregunta;
    private List<Respuesta> respuestas;
    private String respuestaCorrecta;
    private int intentosRestantes;
    private static final int MAX_INTENTOS = 2;

    public Acertijo(int id, String dificultad, String pregunta, List<Respuesta> respuestas, String respuestaCorrecta) {
        this.id = id;
        this.dificultad = dificultad;
        this.pregunta = pregunta;
        this.respuestas = respuestas;
        this.respuestaCorrecta = respuestaCorrecta;
        this.intentosRestantes = MAX_INTENTOS;
    }

    public int getId() {
        return id;
    }

    public String getDificultad() {
        return dificultad;
    }

    public String getPregunta() {
        return pregunta;
    }

    public List<Respuesta> getRespuestas() {
        return respuestas;
    }

    public String getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public int getIntentosRestantes() {
        return intentosRestantes;
    }

    public void decrementarIntentos() {
        if (intentosRestantes > 0) {
            intentosRestantes--;
        }
    }

    public boolean tieneIntentosDisponibles() {
        return intentosRestantes > 0;
    }
}
