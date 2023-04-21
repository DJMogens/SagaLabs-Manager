package sagalabsmanagerclient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Output {
    private File file;
    private PrintStream stream;

    public Output() {
        this.file = new File(".\\log.txt");
        try {
            this.stream = new PrintStream(file);
        } catch (FileNotFoundException e) {
            try {
                file.createNewFile();
                this.stream = new PrintStream(file);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public void sendOutputToLog() {
        System.setOut(stream);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        System.out.println("Program started at " + dtf.format(now));
    }
}
