import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ServerLogger {

    public synchronized void saveLog(String message) {
        try (FileWriter writer = new FileWriter(new File("").getAbsolutePath() + "/Server/logs.txt", true)){
            writer.write(message);
            writer.append('\n');
            writer.flush();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}