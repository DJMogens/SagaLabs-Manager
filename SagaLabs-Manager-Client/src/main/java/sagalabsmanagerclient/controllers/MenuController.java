package sagalabsmanagerclient.controllers;

import sagalabsmanagerclient.*;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.SQLException;

public abstract class MenuController implements Refreshable {

    public boolean pageIsSelected = false;
    public void logout(ActionEvent event) throws IOException, SQLException {
        AzureLogin.setLoginStatus(false);
        AzureLogin.setAzure(null);
        AzureLogin.setTokenCredentialKeyVault(null);
        Database.conn.close();
        ViewSwitcher.switchView(View.LOGIN);
    }
    public void home(ActionEvent event) throws IOException {
        pageIsSelected = false;
        ViewSwitcher.switchView(View.HOME);
    }
    public void switchToMachine(ActionEvent event) throws IOException {
        pageIsSelected = false;
        ViewSwitcher.switchView(View.MACHINES);
    }
    public void switchToSQL(ActionEvent event) throws IOException {
        pageIsSelected = false;
        ViewSwitcher.switchView(View.SQLSCENE);
    }
    public void switchToVPN(ActionEvent event) throws IOException {
        pageIsSelected = false;
        ViewSwitcher.switchView(View.VPN);
    }
}
