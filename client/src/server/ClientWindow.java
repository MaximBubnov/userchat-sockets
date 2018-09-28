package server;

import network.Connection;
import network.ConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, ConnectionListener{

    private static final String IP_ADDRESS  = "192.168.1.3";
    private static final int PORT  = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField nameUser = new JTextField("unknown");
    private final JTextField messageText = new JTextField();

    private Connection connection;

    private ClientWindow() {

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEditable(false); //запрет редактировния
        log.setLineWrap(true); //перенос слов
        add(log, BorderLayout.CENTER);

        messageText.addActionListener(this);
        add(messageText, BorderLayout.SOUTH);

        add(nameUser, BorderLayout.NORTH);

        setVisible(true);

        try {
            connection = new Connection(this, IP_ADDRESS, PORT);
        } catch (IOException e) {
            printMessage("Connection exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = messageText.getText();

        if (message.equals("")) return;

        messageText.setText(null);
        connection.sendMessage(nameUser.getText() + ": " + message);
    }


    @Override
    public void onConnectionReady(Connection connection) {
        printMessage("Connection successfully ready!");
    }

    @Override
    public void onReceiveMessage(Connection connection, String message) {
        printMessage(message);
    }

    @Override
    public void onDisconnection(Connection connection) {
        printMessage("Connection closed!");
    }

    @Override
    public void onException(Connection connection, Exception e) {

        printMessage("Connection exception: " + e);
    }

    private synchronized void printMessage(String message) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(message + "\n");
                log.setCaretPosition(log.getDocument().getLength()); //автоскролл (текст поднимаем)
            }
        });
    }
}
