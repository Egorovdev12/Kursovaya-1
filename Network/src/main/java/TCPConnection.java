import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {

    // Сокет устанавливает соедение
    private final Socket socket;
    // Один поток на каждом из клиентов - будет постоянно читать поток ввода
    private final Thread listeningThread;
    // Потоки ввода-вывода
    private final BufferedReader buffReader;
    private final BufferedWriter buffWriter;
    // Слушатель событий
    private final TCPConnectionListener eventListener;

    private final String encodingName = "UTF-8";

    // Конструктор, внутри которог осоздается сокет
    public TCPConnection(TCPConnectionListener eventListener, String ip, int port) throws IOException {
        this(eventListener, new Socket(ip, port));
    }


    // Конструктор на случай, если кто-то снаружи передаст нам сокет
    public TCPConnection (final TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        // необходимо получить у данного сокета входящий и исходящий поток, чтобы принимать и отправлять данные
        buffReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName(encodingName)));
        buffWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName(encodingName)));

        listeningThread = new Thread(new Runnable() {
            public void run() {
                try {
                    eventListener.onReadyConnection(TCPConnection.this);
                    while (!listeningThread.isInterrupted()){
                        String message = buffReader.readLine();
                        eventListener.onReceiveString(TCPConnection.this, message);
                    }
                }
                catch (IOException ex) {
                    eventListener.onException(TCPConnection.this, ex);
                }
                finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        listeningThread.start();
    }

    public synchronized void sendMessage(String value) {
        try {
            buffWriter.write(value + "\r\n");
            buffWriter.flush();
        } catch (IOException e) {
            eventListener.onException(this, e);
            disconnect();
        }
    }

    // Метод, прерывающий соединение
    public synchronized void disconnect() {
        listeningThread.interrupt();
        try {
            socket.close();
        } catch (IOException ex) {
            eventListener.onException(this, ex);
        }
    }

    @Override
    public String toString() {
        return "Connection: " + socket.getInetAddress() + ": " + socket.getPort();
    }

}