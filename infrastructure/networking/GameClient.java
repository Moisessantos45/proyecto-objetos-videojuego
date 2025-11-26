package infrastructure.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String serverAddress;
    private int port;
    private boolean connected = false;

    public GameClient(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    public boolean connect() {
        try {
            System.out.println("Intentando conectar a " + serverAddress + ":" + port + "...");
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;
            System.out.println("Conexión exitosa a " + serverAddress);
            
            new Thread(this::listen).start();
            return true;
        } catch (IOException e) {
            System.out.println("Error al conectar a " + serverAddress + ":" + port + " -> " + e.getMessage());
            return false;
        }
    }

    private void listen() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                // Aquí procesaríamos los mensajes del servidor (actualizaciones de juego, etc.)
                System.out.println("Mensaje del servidor: " + message);
            }
        } catch (IOException e) {
            System.out.println("Desconectado del servidor");
        } finally {
            connected = false;
        }
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
