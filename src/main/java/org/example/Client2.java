package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client2 {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 55555;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Choose your nickname: ");
        String nickname = scanner.nextLine();

        try {
            Socket clientSocket = new Socket(SERVER_ADDRESS, PORT);
            System.out.println("Connected to server");

            Thread receiveThread = new Thread(new ReceiveMessages(clientSocket));
            receiveThread.start();

            OutputStream output = clientSocket.getOutputStream();
            output.write("NICK".getBytes());
            output.write(nickname.getBytes());

            while (true) {
                String message = scanner.nextLine();
                message = nickname + ": " + message;
                output.write(message.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ReceiveMessages implements Runnable {
        private final Socket clientSocket;

        ReceiveMessages(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                InputStream input = clientSocket.getInputStream();
                byte[] buffer = new byte[1024];

                while (true) {
                    int bytesRead = input.read(buffer);
                    if (bytesRead == -1) {
                        break;
                    }

                    String message = new String(buffer, 0, bytesRead);
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}