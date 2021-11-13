import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ClientLogger {

    public synchronized void saveLog(String message) {
        try (FileWriter writer = new FileWriter(new File("").getAbsolutePath() + "/Client/clientLogs.txt", true)){
            writer.write(message);
            writer.append('\n');
            writer.flush();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}