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
                    Platform.runLater(() -> {
                        try {
                            refresh();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (InterruptedException ignored) {}
            }
        });
        refreshing.start();
    }

    public void logout() throws SQLException {
        AzureLogin.setLoginStatus(false);
        AzureLogin.setAzure(null);
        AzureLogin.setTokenCredentialKeyVault(null);
        Database.conn.close();
        setView(View.LOGIN);
    }
    public void switchToHome() {
        setView(View.HOME);
    }
    public void switchToMachine() {
        setView(View.MACHINES);
    }
    public void switchToVPN() {
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
