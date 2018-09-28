package server;

import network.Connection;
import network.ConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * Сервер слушает входящие соединения. на каждое новое соединение он создает новый Connection
 */
public class Server implements ConnectionListener{

    public static void main(String[] args) {
        new Server();
    }

    private final ArrayList<Connection> connections = new ArrayList<>(); //количество соединений

    private Server() {
        System.out.println("Server is running");
        try (ServerSocket serverSocket = new ServerSocket(8189)) { //serverSocket слушает порт 8189

            while (true) {
                try {

                    new Connection(this, serverSocket.accept()); //передаем 2-ой конструктор | accept - возвращет сокет, связанный с соединением

                } catch (IOException e) {
                    System.out.println("Connection exception: " + e);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(Connection connection) {
        connections.add(connection);
        sendInfoMessageToAllconnections("Client connected: " + connection);
    }

    @Override
    public synchronized void onReceiveMessage(Connection connection, String message) {
        sendInfoMessageToAllconnections(message);
    }

    @Override
    public synchronized void onDisconnection(Connection connection) {
        connections.remove(connection);
        sendInfoMessageToAllconnections("Client disconnected: " + connection);
    }

    @Override
    public synchronized void onException(Connection connection, Exception e) {
        System.out.println("Connection exception : " + e);
    }

    /**
     * Логи
     * @param message что произошло
     */
    private void sendInfoMessageToAllconnections(String message) {
        System.out.println(message);

        for (int i = 0; i < connections.size(); i++) {
            connections.get(i).sendMessage(message);
        }
    }
}
