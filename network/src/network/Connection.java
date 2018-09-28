package network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Class for connection
 */
public class Connection {

    private final Socket socket;
    private final Thread thread;
    private final ConnectionListener connectionListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    /**
     * Сокет будем создавать мы (внутри)
     * @param connectionListener - слушатель событий
     * @param ip - ip address
     * @param port - port
     * @throws IOException
     */
    public Connection(ConnectionListener connectionListener, String ip, int port) throws IOException {
        this(connectionListener, new Socket(ip, port));
    }

    /**
     * Кто-то снаружи будет делать соединение (создаст сокет), т.е мы принимаем уже готовый сокет(сделанный кем-то)
     *
     * @param socket сам сокет для соединения
     *  thread поток - слушает входящие сообщения, если сообщение пришло - make event
     *  in - поток чтения сообщения
     *  out -потом записи сообщения
     *  connectionListener - слушатель событий
     */

    public Connection(ConnectionListener connectionListener, Socket socket) throws IOException {
        this.connectionListener = connectionListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connectionListener.onConnectionReady(Connection.this);

                    //пока потом не прерван

                    while (!thread.isInterrupted()) {
                        String message = in.readLine();
                        connectionListener.onReceiveMessage(Connection.this, message); // передаем туда сообщение

                    }

                } catch (IOException e) {
                    Connection.this.connectionListener.onException(Connection.this, e);
                } finally {
                    connectionListener.onDisconnection(Connection.this);
                }
            }
        });

        thread.start();

    }

    /**
     * Отправить сообщение
     * @param message само сообщение
     */
    public synchronized void sendMessage(String message) {
        try {
            out.write(message + "\r\n");
            out.flush();

        } catch (IOException e) {
            connectionListener.onException(Connection.this, e);
            disConnect();
        }
    }

    /**
     * Прервать соединение (убить поток)
     */
    public synchronized void disConnect() {
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            connectionListener.onException(Connection.this, e);
        }
    }

    @Override
    public String toString() {
        return "Connection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
