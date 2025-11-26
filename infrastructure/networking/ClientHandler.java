package infrastructure.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private GameServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String clientId;

    public ClientHandler(Socket socket, GameServer server, String clientId) {
        this.socket = socket;
        this.server = server;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Enviar ID al cliente al conectar
            sendMessage("WELCOME:" + clientId);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Si es un mensaje de posición, retransmitir a TODOS (incluyendo al remitente)
                if (inputLine.startsWith("POS:")) {
                    System.out.println("[SERVIDOR] Recibido POS de " + clientId + ": " + inputLine);
                    server.broadcastToAll(inputLine);
                } else {
                    // Para otros mensajes (START_GAME, MAP_SEED), excluir al remitente
                    server.broadcast(inputLine, this);
                }
            }
        } catch (IOException e) {
            System.out.println("Error en ClientHandler: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            server.removeClient(this);
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}
