package sagalabsmanagerclient.controllers;

import javafx.fxml.FXML;
import sagalabsmanagerclient.View;
import sagalabsmanagerclient.ViewSwitcher;

public class VPNController extends MenuController {
    @FXML
    public static void changeScene() {
        ViewSwitcher.switchView(View.VPN);
    }
}
    