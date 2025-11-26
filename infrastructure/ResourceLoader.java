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
            BufferedImage imagen = ImageIO.read(new File(ruta));
            imageCache.put(ruta, imagen);
            return imagen;
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
        try {
            tiles[0] = ImageIO.read(new File("tiles/pasto.png"));
            tiles[1] = ImageIO.read(new File("tiles/pared.png"));
            tiles[2] = ImageIO.read(new File("tiles/agua.png"));
            tiles[3] = ImageIO.read(new File("tiles/tierra.png"));
            tiles[4] = ImageIO.read(new File("tiles/arena.png"));
            tiles[5] = ImageIO.read(new File("tiles/piedra.png"));
            tiles[6] = ImageIO.read(new File("tiles/nube.png"));
            tiles[7] = ImageIO.read(new File("tiles/volcan.png"));
        } catch (IOException e) {
            System.err.println("Error al cargar tiles");
            e.printStackTrace();
        }
        return tiles;
    }

    public BufferedImage[] cargarAnimacionEnemigo(String tipoEnemigo, String animacion) {
        String rutaBase = "spritesenemigos/" + tipoEnemigo + "/" + animacion + "/";
        System.out.println("DEBUG: Intentando cargar animación de enemigo desde: " + rutaBase);
        
        try {
            File carpeta = new File(rutaBase);
            System.out.println("DEBUG: Carpeta existe: " + carpeta.exists());
            System.out.println("DEBUG: Carpeta es directorio: " + carpeta.isDirectory());
            if (!carpeta.exists() || !carpeta.isDirectory()) {
                System.out.println("DEBUG: Carpeta no existe o no es un directorio válido.");
                return new BufferedImage[0];
            }
            
            File[] archivos = carpeta.listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".png")
            );
            
            System.out.println("DEBUG: Archivos encontrados: " + (archivos != null ? archivos.length : 0));
            if (archivos == null || archivos.length == 0) {
                System.out.println("DEBUG: No se encontraron archivos PNG en la carpeta.");
                return new BufferedImage[0];
            }
            
            java.util.Arrays.sort(archivos);
            
            BufferedImage[] frames = new BufferedImage[archivos.length];
            for (int i = 0; i < archivos.length; i++) {
                String rutaCompleta = archivos[i].getPath();
                frames[i] = cargarImagen(rutaCompleta);
            }
            
            return frames;
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
