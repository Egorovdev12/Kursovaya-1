public interface TCPConnectionListener {
    // Интерфейс, содержащий в себе различные возможные события
    void onReadyConnection(TCPConnection tcpConnection);
    void onReceiveString(TCPConnection tcpConnection, String value);
    void onDisconnect(TCPConnection tcpConnection);
    void onException(TCPConnection tcpConnection, Exception exception);

}
