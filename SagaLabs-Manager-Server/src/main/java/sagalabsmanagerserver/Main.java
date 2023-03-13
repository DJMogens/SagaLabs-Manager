package sagalabsmanagerserver;

import java.sql.SQLException;

import static sagalabsmanagerserver.Database.syncLabs;

public class Main {
    public static void main(String[] args) throws SQLException {

        AzureLogin.startLogin();

        startSyncLabs();
        startSyncVM();
    }

    public static void startSyncLabs() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while(true) {
                    try {
                        Database.syncLabs();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        System.out.println("Error in syncLabs: " + e.getMessage());
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        continue; // restart the loop
                    }
                    System.out.println("Synced Labs table");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        thread.start();
    }

    public static void startSyncVM() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while(true) {
                    try {
                        Database.syncVM();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        System.out.println("Error in syncVM: " + e.getMessage());
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        continue; // restart the loop
                    }
                    System.out.println("Synced vm table");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        thread.start();
    }

}