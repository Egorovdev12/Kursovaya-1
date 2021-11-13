import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatServer implements TCPConnectionListener {

    public static void main(String[] args) {
        new ChatServer();
    }

    private final List<TCPConnection> listOfConnections = new ArrayList<>();
    private final String PATH_TO_SETTINGS_FILE = new File("").getAbsolutePath() + "/Server/settings.json";
    private final String JSON_KEY_FOR_PORT = "port";
    private final ServerLogger serverLogger = new ServerLogger();
    private int port;


    // Конструктор сервера
    public ChatServer() {
        port = getPortNumberFromFile(PATH_TO_SETTINGS_FILE);
        System.out.println("Server is running...");
        // Класс ServerSocket слушает определенный порт и принимает входящее соединение
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    // Метод accept класса ServerSocket ждет нового соединения и возвращает объект сокета, который связан с этим соединением
                    new TCPConnection(this, serverSocket.accept());
                }
                catch (IOException ex) {
                    System.out.println("TCPConnection exception: " + ex);
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public synchronized void onReadyConnection(TCPConnection tcpConnection) {
        listOfConnections.add(tcpConnection);
        sendMessageToChat("New client connected " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendMessageToChat(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        listOfConnections.remove(tcpConnection);
        sendMessageToChat("Client " + tcpConnection + " disconnected");
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception exception) {
        System.out.println("TCPConnection exception: " + exception);
    }

    // Метод реализующий отправку сообщения всем текущим пользователям чата
    private void sendMessageToChat(String value) {
        System.out.println(value);
        final int listSize = listOfConnections.size();
        for (int i = 0; i < listSize; i++) {
            listOfConnections.get(i).sendMessage(value);
        }
        serverLogger.saveLog(value + "; TIME: " + new Date());
    }

    // Метод, получающий номер порта из файла настроек
    public synchronized int getPortNumberFromFile(String path)  {
        JSONObject jo = null;
        try {
            Object object = new JSONParser().parse(new FileReader(path));
            jo = (JSONObject) object;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(jo.get(JSON_KEY_FOR_PORT).toString());
    }
}