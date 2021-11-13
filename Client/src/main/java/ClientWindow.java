import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 1213;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    private final String DEFAULT_NICKNAME = "user";
    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField(DEFAULT_NICKNAME);
    private final JTextField fieldInput = new JTextField();

    private static TCPConnection tcpConnection;
    private ClientLogger clientLogger = new ClientLogger();


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private ClientWindow() {
        // Нажатие на крестик - закрывает окошко
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // Установка размера окна
        setSize(WIDTH, HEIGHT);
        // Сделать окно видимым
        setVisible(true);
        // Окно при запуске появляется поверх других
        setAlwaysOnTop(true);
        // Окно при запуске появляется по центру экрана
        setLocationRelativeTo(null);
        // Запрещаем редактирование истории сообщений
        log.setEditable(false);
        // Автоматический перенос слов
        log.setLineWrap(true);
        // Добавим экшн листнер
        fieldInput.addActionListener(this);
        // Распределяем элементы на форме
        add(log, BorderLayout.CENTER);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickname, BorderLayout.NORTH);

        // Сделать окно видимым
        setVisible(true);

        try {
            tcpConnection = new TCPConnection(this, IP_ADDRESS, PORT);
        } catch (IOException e) {
            printMsg("Connection exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if (msg.equals("")) {
            return;
        }
        fieldInput.setText(null);
        tcpConnection.sendMessage(fieldNickname.getText() + ": " + msg);
        clientLogger.saveLog(fieldNickname.getText() + ": " + msg + "; TIME: " + new Date());
    }

    @Override
    public void onReadyConnection(TCPConnection tcpConnection) {
        printMsg("Connection ready");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMsg("Connection close");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception exception) {
        printMsg("Connection exception: " + exception);
    }

    private synchronized void printMsg(String msg) {
        // синхоризирующий метод swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}