package infrastructure.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String serverAddress;
    private int port;
    private boolean connected = false;
    private Queue<String> messageQueue = new ConcurrentLinkedQueue<>();
    private String myId;

    public GameClient(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    public boolean connect() {
        try {
            System.out.println("------------------------------------------------");
            System.out.println("INICIANDO CONEXIÓN");
            System.out.println("Destino: '" + serverAddress + "'");
            System.out.println("Puerto: " + port);
            
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;
            System.out.println("¡CONEXIÓN ESTABLECIDA EXITOSAMENTE!");
            System.out.println("------------------------------------------------");
            
            new Thread(this::listen).start();
            return true;
        } catch (IOException e) {
            System.err.println("FALLO DE CONEXIÓN:");
            System.err.println("Error: " + e.getClass().getSimpleName());
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("------------------------------------------------");
            return false;
        }
    }

    private void listen() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("WELCOME:")) {
                    myId = message.split(":")[1];
                    System.out.println("Mi ID asignado es: " + myId);
                } else {
                    // Solo imprimir mensajes que no sean de posición para no saturar la consola
                    if (!message.startsWith("POS:")) {
                        System.out.println("Mensaje del servidor: " + message);
                    }
                    messageQueue.offer(message);
                }
            }
        } catch (IOException e) {
            System.out.println("Desconectado del servidor");
        } finally {
            connected = false;
        }
    }

    public void sendPosition(int x, int y, String direction) {
        if (myId != null) {
            sendMessage("POS:" + myId + ":" + x + ":" + y + ":" + direction);
        }
    }

    public String getMyId() {
        return myId;
    }

    public String getNextMessage() {
        return messageQueue.poll();
    }

    public void sendMessage(String message) {
        if (out != null && connected) {
            out.println(message);
        }
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
