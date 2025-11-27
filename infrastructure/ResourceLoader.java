package infrastructure;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class ResourceLoader {
    private static ResourceLoader instance;
    private Map<String, BufferedImage> imageCache;

    private ResourceLoader() {
        this.imageCache = new HashMap<>();
    }

    public static ResourceLoader getInstance() {
        if (instance == null) {
            instance = new ResourceLoader();
        }
        return instance;
    }

    public BufferedImage cargarImagen(String ruta) {
        if (imageCache.containsKey(ruta)) {
            return imageCache.get(ruta);
        }

        try {
            BufferedImage imagen = null;

            // Intentar cargar desde classpath primero (para JAR)
            try {
                var resourceStream = getClass().getClassLoader().getResourceAsStream(ruta);
                if (resourceStream != null) {
                    imagen = ImageIO.read(resourceStream);
                    resourceStream.close();
                }
            } catch (Exception e) {
                // Si falla, intentar desde File (para desarrollo)
            }

            // Si no se cargó desde classpath, intentar desde File
            if (imagen == null) {
                File file = new File(ruta);
                if (file.exists()) {
                    imagen = ImageIO.read(file);
                }
            }

            if (imagen != null) {
                imageCache.put(ruta, imagen);
                return imagen;
            } else {
                System.err.println("Error: No se pudo cargar la imagen: " + ruta);
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error al cargar imagen: " + ruta);
            e.printStackTrace();
            return null;
        }
    }

    public BufferedImage[] cargarSpritesJugador() {
        BufferedImage[] sprites = new BufferedImage[8];
        sprites[0] = cargarImagen("spritesjugador/moverArriba1.png");
        sprites[1] = cargarImagen("spritesjugador/moverArriba2.png");
        sprites[2] = cargarImagen("spritesjugador/moverAbajo1.png");
        sprites[3] = cargarImagen("spritesjugador/moverAbajo2.png");
        sprites[4] = cargarImagen("spritesjugador/moverIzquierda1.png");
        sprites[5] = cargarImagen("spritesjugador/moverIzquierda2.png");
        sprites[6] = cargarImagen("spritesjugador/moverDerecha1.png");
        sprites[7] = cargarImagen("spritesjugador/moverDerecha2.png");
        return sprites;
    }

    public BufferedImage[] cargarTiles() {
        BufferedImage[] tiles = new BufferedImage[10];
        tiles[0] = cargarImagen("tiles/pasto.png");
        tiles[1] = cargarImagen("tiles/pared.png");
        tiles[2] = cargarImagen("tiles/agua.png");
        tiles[3] = cargarImagen("tiles/tierra.png");
        tiles[4] = cargarImagen("tiles/arena.png");
        tiles[5] = cargarImagen("tiles/piedra.png");
        tiles[6] = cargarImagen("tiles/nube.png");
        tiles[7] = cargarImagen("tiles/volcan.png");
        return tiles;
    }

    public BufferedImage[] cargarAnimacionEnemigo(String tipoEnemigo, String animacion) {
        String rutaBase = "spritesenemigos/" + tipoEnemigo + "/" + animacion + "/";
        System.out.println("DEBUG: Intentando cargar animación de enemigo desde: " + rutaBase);

        try {
            // Intentar cargar desde File primero (desarrollo)
            File carpeta = new File(rutaBase);
            System.out.println("DEBUG: Carpeta existe: " + carpeta.exists());
            System.out.println("DEBUG: Carpeta es directorio: " + carpeta.isDirectory());
            if (carpeta.exists() && carpeta.isDirectory()) {
                File[] archivos = carpeta.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

                System.out.println("DEBUG: Archivos encontrados: " + (archivos != null ? archivos.length : 0));
                if (archivos != null && archivos.length > 0) {
                    java.util.Arrays.sort(archivos);

                    BufferedImage[] frames = new BufferedImage[archivos.length];
                    for (int i = 0; i < archivos.length; i++) {
                        String rutaCompleta = archivos[i].getPath();
                        frames[i] = cargarImagen(rutaCompleta);
                    }

                    return frames;
                }
            }

            // Si no se encontró en File, intentar cargar desde classpath (JAR)
            // Nota: Cargar directorios desde JAR es complejo, se recomienda listar archivos
            // manualmente
            System.out.println("DEBUG: No se encontraron archivos en File system, intentando desde classpath...");
            // Por ahora, retornar array vacío si no se encuentra en File
            return new BufferedImage[0];
        } catch (Exception e) {
            System.err.println("DEBUG: Excepción al cargar animación: " + e.getMessage());
            return new BufferedImage[0];
        }
    }

    public BufferedImage cargarItemSprite(String nombreItem) {
        return cargarImagen("tiles/" + nombreItem + ".png");
    }

    public void limpiarCache() {
        imageCache.clear();
    }
}