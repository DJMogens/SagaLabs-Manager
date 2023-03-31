package sagalabsmanagerclient.controllers;

import javafx.application.Platform;
import sagalabsmanagerclient.*;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class MenuController extends Controller implements Refreshable {
    private Thread refreshing;
    private boolean pageIsSelected = false;

    public void initialize() throws SQLException {
        pageIsSelected = true;
        addRefreshThread();
    }
    public void addRefreshThread() {
        refreshing = new Thread(() -> {
            while(pageIsSelected) {
                try {
                    Thread.sleep(milliSecondsBetweenRefresh);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    System.out.println("REFRESHING " + this + " at " + dtf.format(now));
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                refresh();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                } catch (InterruptedException ignored) {}
            }
        });
        refreshing.start();
    }

    public View logout(ActionEvent event) throws IOException, SQLException {
        AzureLogin.setLoginStatus(false);
        AzureLogin.setAzure(null);
        AzureLogin.setTokenCredentialKeyVault(null);
        Database.conn.close();
        return View.LOGIN;
    }
    public void switchToHome(ActionEvent event) throws IOException {
        setView(View.HOME);
    }
    public void switchToMachine(ActionEvent event) throws IOException {
        setView(View.MACHINES);
    }
    public void switchToSQL(ActionEvent event) throws IOException {
        setView(View.SQLSCENE);
    }
    public void switchToVPN(ActionEvent event) throws IOException {
        setView(View.VPN);
    }
    public void stopRefreshing() {
        pageIsSelected = false;
        try {
            refreshing.interrupt();
        }
        catch(NullPointerException ignored) {}
    }
}
