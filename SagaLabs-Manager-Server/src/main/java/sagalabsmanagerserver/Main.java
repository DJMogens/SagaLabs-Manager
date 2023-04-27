package sagalabsmanagerserver;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static sagalabsmanagerserver.Database.syncLabs;

public class Main {
    public static void main(String[] args) throws SQLException {

        AzureLogin.startLogin();

        startSyncLabs();
        startSyncVM();
    }

    public static void startSyncLabs() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            try {
                Database.syncLabs();
                System.out.println("Synced Labs table");
            } catch (Exception e) {
                System.out.println("Error in syncLabs: " + e.getMessage());
            }
        }, 0, 20, TimeUnit.SECONDS);
    }

    public static void startSyncVM() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            try {
                Database.syncVM();
                System.out.println("Synced vm table");
            } catch (Exception e) {
                System.out.println("Error in syncVM: " + e.getMessage());
            }
        }, 0, 20, TimeUnit.SECONDS);
    }
}