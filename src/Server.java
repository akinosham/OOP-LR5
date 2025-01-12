import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static Map<PrintWriter, String> clientWriters = new HashMap<>(); // Используем Map для хранения писателей и имени клиента

    public static void main(String[] args) {
        System.out.println("Сервер запущен...");
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String clientName; // Имя клиента

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);


                // Получаем имя клиента при подключении
                clientName = in.readLine();


                synchronized (clientWriters) {
                    clientWriters.put(out, clientName);
                }

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Получено сообщение от " + clientName + ": " + message);
                    sendMessageToClients(message, clientName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
            }
        }

        private void sendMessageToClients(String message, String senderName) {
            synchronized (clientWriters) {
                for (Map.Entry<PrintWriter,String> entry : clientWriters.entrySet()) {
                    PrintWriter writer = entry.getKey();
                    String name = entry.getValue();
                    if (!name.equals(senderName)) { // Проверяем, чтобы не отправлять сообщение обратно отправителю
                        writer.println(senderName + ": " + message);
                    }
                }
            }
        }
    }
}