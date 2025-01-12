import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client1 {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public static void main(String[] args) {
        new Client1().start();
    }

    public void start() {
        try {
            socket = new Socket("localhost", 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Отправляем имя клиента на сервер
            out.println("Client 1");

            // Создаем поток для получения сообщений
            new Thread(new IncomingMessageHandler()).start();

            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            String userInput;
            while ((userInput = consoleInput.readLine()) != null) {
                out.println(userInput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class IncomingMessageHandler implements Runnable {
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    System.out.println("Получено сообщение: " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}