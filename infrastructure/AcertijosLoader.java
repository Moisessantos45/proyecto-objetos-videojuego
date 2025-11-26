package infrastructure;

import model.Acertijo;
import model.Respuesta;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AcertijosLoader {
    private List<Acertijo> acertijos;
    private Random random;

    public AcertijosLoader(String rutaArchivo) {
        this.acertijos = new ArrayList<>();
        this.random = new Random();
        cargarAcertijos(rutaArchivo);
    }

    private void cargarAcertijos(String rutaArchivo) {
        try {
            String contenido = new String(Files.readAllBytes(Paths.get(rutaArchivo)));
            JSONArray jsonArray = new JSONArray(contenido);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                
                int id = obj.getInt("id");
                String dificultad = obj.getString("dificultad");
                String pregunta = obj.getString("pregunta");
                String respuestaCorrecta = obj.getString("respuesta_correcta");

                List<Respuesta> respuestas = new ArrayList<>();
                JSONArray respuestasArray = obj.getJSONArray("respuestas");
                for (int j = 0; j < respuestasArray.length(); j++) {
                    JSONObject respObj = respuestasArray.getJSONObject(j);
                    respuestas.add(new Respuesta(
                        respObj.getString("inciso"),
                        respObj.getString("texto")
                    ));
                }

                acertijos.add(new Acertijo(id, dificultad, pregunta, respuestas, respuestaCorrecta));
            }

            System.out.println("Acertijos cargados correctamente: " + acertijos.size());
        } catch (IOException e) {
            System.err.println("Error al cargar acertijos: " + e.getMessage());
        }
    }

    public Acertijo obtenerAcertijoAleatorio() {
        if (acertijos.isEmpty()) {
            return null;
        }
        return acertijos.get(random.nextInt(acertijos.size()));
    }

    public List<Acertijo> getAcertijos() {
        return acertijos;
    }
}
