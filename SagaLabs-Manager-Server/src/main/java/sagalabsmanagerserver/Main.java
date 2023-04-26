package sagalabsmanagerserver;

import java.sql.SQLException;
import java.util.concurrent.*;

import static sagalabsmanagerserver.Database.syncLabs;

public class Main {
    public static void main(String[] args) throws SQLException {

        AzureLogin.startLogin();

        startSyncLabs();
        startSyncVM();
    }

    public static void startSyncLabs() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable syncLabsTask = () -> {
            try {
                Database.syncLabs();
                System.out.println("Synced Labs table");
            } catch (Exception e) {
                System.out.println("Error in syncLabs: " + e.getMessage());
            }
        };

        executor.scheduleWithFixedDelay(() -> {
            Future<?> future = executor.submit(syncLabsTask);
            try {
                future.get(60, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                future.cancel(true);
                System.out.println("SyncLabs task timed out. Restarting...");
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public static void startSyncVM() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable syncVMTask = () -> {
            try {
                Database.syncVM();
                System.out.println("Synced vm table");
            } catch (Exception e) {
                System.out.println("Error in syncVM: " + e.getMessage());
            }
        };

        executor.scheduleWithFixedDelay(() -> {
            Future<?> future = executor.submit(syncVMTask);
            try {
                future.get(60, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                future.cancel(true);
                System.out.println("SyncVM task timed out. Restarting...");
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}
