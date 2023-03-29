package sagalabsmanagerclient.controllers;

import sagalabsmanagerclient.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.sql.SQLException;

public class DatabaseController extends MenuController{

    @FXML
    private Label sqlstatus;

    public void ConnectToDatabase(ActionEvent event) throws IOException, SQLException {
        if (Database.login()){
            sqlstatus.setText("SQL connection: Working");
            sqlstatus.setTextFill(Color.GREEN);
        }
        else {
            sqlstatus.setText("SQL connection: Failed");
            sqlstatus.setTextFill(Color.RED);
        }
        //Database.getMachines();
    }
    public void refresh() {

    }
}
