package com.example.sagalabsmanager.controllers;

import com.example.sagalabsmanager.SagaDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.sql.SQLException;

public class SQLController extends MenuController{

    @FXML
    public Label sqlstatus;
    public TextField sqlUsername, sqlURL;
    public PasswordField sqlPassword;

    public void ConnectToSQL(ActionEvent event) throws IOException, SQLException {
        if (SagaDB.sqlLoginCheck()){
            sqlstatus.setText("SQL connection: Working");
            sqlstatus.setTextFill(Color.GREEN);
        }
        else {
            sqlstatus.setText("SQL connection: Failed");
            sqlstatus.setTextFill(Color.RED);
        }
    }

}
