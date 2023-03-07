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
                    }
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        thread.start();
    }
    public static void startSyncVM() throws SQLException {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while(true) {
                    try {
                        Database.syncVM();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        thread.start();
    }
}