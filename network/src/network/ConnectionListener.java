package network;

public interface ConnectionListener {

    void onConnectionReady(Connection connection); // соединение установленно

    void onReceiveMessage(Connection connection, String message); //приняли строчку (сообщение)

    void onDisconnection(Connection connection); //получили дисконект

    void onException(Connection connection, Exception e); // словили exception
}
