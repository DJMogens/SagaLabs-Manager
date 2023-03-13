package sagalabsmanagerclient.controllers;

import sagalabsmanagerclient.*;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.SQLException;

public class MenuController {
    public void logout(ActionEvent event) throws IOException {
        AzureLogin.loginStatus = false;
        AzureLogin.azure = null;
        AzureLogin.tokenCredentialKeyVault = null;
        ViewSwitcher.switchView(View.LOGIN);
    }
    public void home(ActionEvent event) throws IOException {
        ViewSwitcher.switchView(View.HOME);
    }
    public void switchToMachine(ActionEvent event) throws IOException {
        ViewSwitcher.switchView(View.MACHINES);
    }
    public void switchToSQL(ActionEvent event) throws IOException, SQLException {
        ViewSwitcher.switchView(View.SQLSCENE);
        Database.conn.close();
    }
    public void switchToVPN(ActionEvent event) throws IOException, SQLException {
        ViewSwitcher.switchView(View.VPN);
        VPNAdmin.listUsers();
        Database.conn.close();
    }
}
