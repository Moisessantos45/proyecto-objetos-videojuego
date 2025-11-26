package infrastructure.networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameServer {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private int port;
    private boolean running = false;
    private String serverId;

    public GameServer(int port) {
        this.port = port;
        this.serverId = generateServerId();
    }

    public void start() {
        new Thread(() -> {
            try {
                // Forzar la escucha en todas las interfaces de red (0.0.0.0)
                serverSocket = new ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"));
                running = true;
                System.out.println("Servidor iniciado en puerto " + port + " con ID: " + serverId);
                System.out.println("Escuchando en IP: " + InetAddress.getLocalHost().getHostAddress());
                
                while (running) {
                    Socket socket = serverSocket.accept();
                    ClientHandler client = new ClientHandler(socket, this);
                    synchronized (clients) {
                        clients.add(client);
                    }
                    new Thread(client).start();
                    System.out.println("Nuevo cliente conectado. Total: " + clients.size());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(String message, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                // Opcional: no enviar al remitente si no es necesario
                // if (client != sender) 
                client.sendMessage(message);
            }
        }
    }

    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
        System.out.println("Cliente desconectado. Total: " + clients.size());
    }

    public String getServerId() {
        return serverId;
    }
    
    public int getConnectedPlayers() {
        return clients.size();
    }

    private String generateServerId() {
        // Genera un ID simple tipo XXXX-XXXX
        String uuid = UUID.randomUUID().toString().toUpperCase();
        return uuid.substring(0, 4) + "-" + uuid.substring(4, 8);
    }
}
