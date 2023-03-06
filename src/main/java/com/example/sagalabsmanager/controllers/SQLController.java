package com.example.sagalabsmanager.controllers;

import com.example.sagalabsmanager.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.sql.SQLException;

public class SQLController extends MenuController{

    @FXML
    public Label sqlstatus;

    public void ConnectToSQL(ActionEvent event) throws IOException, SQLException {
        if (Database.sqlLogin()){
            sqlstatus.setText("SQL connection: Working");
            sqlstatus.setTextFill(Color.GREEN);
        }
        else {
            sqlstatus.setText("SQL connection: Failed");
            sqlstatus.setTextFill(Color.RED);
        }
    }

}
