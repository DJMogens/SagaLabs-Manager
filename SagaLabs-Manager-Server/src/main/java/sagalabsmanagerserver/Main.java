package sagalabsmanagerserver;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static sagalabsmanagerserver.Database.syncLabs;

public class Main {
    public static void main(String[] args) throws SQLException {

        AzureLogin.startLogin();

        int numberOfThreads = 10;

        for (int i = 0; i < numberOfThreads; i++) {
            int delay = 20 * i;
            startSyncLabs(delay);
            startSyncVM(delay);
        }
    }

    public static void startSyncLabs(int delay) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            try {
                Database.syncLabs();
                System.out.println("Synced Labs table");
            } catch (Exception e) {
                System.out.println("Error in syncLabs: " + e.getMessage());
            }
        }, delay, 5, TimeUnit.SECONDS);
    }

    public static void startSyncVM(int delay) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            try {
                Database.syncVM();
                System.out.println("Synced vm table");
            } catch (Exception e) {
                System.out.println("Error in syncVM: " + e.getMessage());
            }
        }, delay, 5, TimeUnit.SECONDS);
    }
}
