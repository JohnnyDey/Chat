package server;

import Util.Connection;
import Util.ConsoleHelper;
import Util.Message;
import Util.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        ConsoleHelper.writeMessage("Введите порт сервера");
        int port = ConsoleHelper.readInt();
        try(ServerSocket ss = new ServerSocket(port)) {
            ConsoleHelper.writeMessage("Сервер запущен. " + ss.getLocalSocketAddress());
            while (true){
                Socket socket = ss.accept();
                Handler handler = new Handler(socket);
                handler.start();
            }
        }
        catch (Exception e) {
            ConsoleHelper.writeMessage("Error!");
        }
    }
    public static void sendBroadcastMessage(Message message) {
        for (Map.Entry<String, Connection> pair : connectionMap.entrySet()) {
            try {
                pair.getValue().send(message);
            }
            catch (IOException e) {
                System.out.println("Сообщение не было отправлено");
            }
        }
    }

    private static class Handler extends Thread {
        private Socket socket;
        public Handler (Socket socket) {
            this.socket = socket;
        }
        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message message = connection.receive();
                if (message.getType() == MessageType.USER_NAME) {
                    if (message.getData() != null && !message.getData().equals("")) {
                        if (connectionMap.get(message.getData()) == null) {
                            connectionMap.put(message.getData(), connection);
                            connection.send(new Message(MessageType.NAME_ACCEPTED));
                            return message.getData();
                        }
                    }
                }
            }
        }
        private void sendListOfUsers(Connection connection, String userName) throws IOException {
            for (Map.Entry<String, Connection> pair : connectionMap.entrySet()) {
                if (!pair.getKey().equals(userName)) {
                    connection.send(new Message(MessageType.USER_ADDED, pair.getKey()));
                }
            }
        }
        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    String text = userName + ": " + message.getData();
                    sendBroadcastMessage(new Message(MessageType.TEXT, text));
                }
                else ConsoleHelper.writeMessage("Ошибка!");
            }
        }
        @Override
        public void run() {
            String newClientName = null;
            SocketAddress address = socket.getRemoteSocketAddress();
            ConsoleHelper.writeMessage("Установлено новое соединение с удаленным адресом: " + address);
            try (Connection connection = new Connection(socket)) {
                 newClientName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, newClientName));
                sendListOfUsers(connection, newClientName);
                serverMainLoop(connection, newClientName);
            }
            catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Произошла ошибка при обмене данными с удаленным адресом: " + address);
            }
            if (newClientName != null) {
                connectionMap.remove(newClientName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, newClientName));
            }
            ConsoleHelper.writeMessage("Закрыто соединение с удаленным адресом: " + address);
        }
    }
}
