package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final int PORT = 55555;
    private static final List<Socket> clients = new ArrayList<>();
    private static final List<String> nicknames = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is listening...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected with " + clientSocket.getInetAddress());

                clients.add(clientSocket);

                Thread thread = new Thread(() -> {
                    try {
                        handleClient(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) throws IOException {
        InputStream input = clientSocket.getInputStream();
        OutputStream output = clientSocket.getOutputStream();

        output.write("NICK".getBytes());

        byte[] buffer = new byte[1024];
        int bytesRead;

        bytesRead = input.read(buffer);
        String nickname = new String(buffer, 0, bytesRead);
        nicknames.add(nickname);

        broadcast(nickname + " joined!");

        output.write("Connected to server!".getBytes());

        while (true) {
            bytesRead = input.read(buffer);
            if (bytesRead == -1) {
                break;
            }
            String message = new String(buffer, 0, bytesRead);
            broadcast(message);
        }

        int index = clients.indexOf(clientSocket);
        clients.remove(index);
        clientSocket.close();
        String leftMessage = nicknames.get(index) + " left!";
        nicknames.remove(index);
        broadcast(leftMessage);
    }

    private static void broadcast(String message) {
        for (Socket client : clients) {
            try {
                OutputStream clientOutput = client.getOutputStream();
                clientOutput.write(message.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
