package com.example.sagalabsmanager.controllers;

import com.example.sagalabsmanager.View;
import com.example.sagalabsmanager.ViewSwitcher;
import javafx.event.ActionEvent;

import java.io.IOException;

public class MenuController {
    public void logout(ActionEvent event) throws IOException {
        ViewSwitcher.switchView(View.LOGIN);
    }
    public void home(ActionEvent event) throws IOException {
        ViewSwitcher.switchView(View.LABS);
    }
    public void switchToMachine(ActionEvent event) throws IOException {
        ViewSwitcher.switchView(View.MACHINES);
    }
    public void switchToSQL(ActionEvent event) throws IOException {
        ViewSwitcher.switchView(View.SQLSCENE);
    }
}
