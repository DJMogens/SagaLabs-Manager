package sagalabsmanagerclient.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sagalabsmanagerclient.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HomeController extends MenuController {

    @FXML
    private AnchorPane anchorHome;
    private final AzureMethods azureMethods = new AzureMethods();

    public void initialize() throws SQLException {
        Platform.runLater(() -> {
            try {
                VBox vbox = createLabsVBox();
                anchorHome.getChildren().add(vbox);
            } catch (SQLException e) {
                // Handle the SQLException
                e.printStackTrace();
            }
        });
        Database.login();
        Machines.InitMachines();

        super.initialize();
    }

    public void refresh() throws SQLException {
        anchorHome.getChildren().clear();
        anchorHome.getChildren().add(createLabsVBox());
    }

    private VBox createLabsVBox() throws SQLException {
        // Execute a SQL query to retrieve information about labs
        ResultSet labs = Database.executeSql("SELECT LabName, vpnRunning, VmCount, LabVPN, (SELECT COUNT(*) FROM vm WHERE resource_group = LabName AND powerstate LIKE '%running%') AS RunningVMs FROM Labs");
        // Create a VBox to hold all the lab information boxes
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        // Loop through the labs ResultSet
        while (labs.next()) {
            // Create an HBox for each lab containing lab information and controls
            HBox hbox = createLabHBox(labs);
            // Add the created HBox to the VBox
            vbox.getChildren().add(hbox);
        }
        // Return the created VBox containing lab information boxes
        return vbox;
    }


    private HBox createLabHBox(ResultSet labs) throws SQLException {
        // Retrieve lab information from the ResultSet
        String labName = labs.getString("LabName");
        String vpnIp = labs.getString("LabVPN");
        boolean vpnRunning = labs.getBoolean("vpnRunning");
        int vmCount = labs.getInt("VmCount");
        int runningVMs = labs.getInt("RunningVMs");
        // Create an HBox to hold all the lab information elements
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: gray; -fx-border-color: #333333; -fx-border-width: 2px; -fx-border-style: solid;");
        // Create a colored Rectangle to represent the VPN status
        Rectangle box = createVpnStatusRectangle(vpnRunning);
        // Create a VBox to hold all the lab labels
        VBox labelBox = createLabelsVBox(labName, vpnIp, vpnRunning, vmCount, runningVMs);
        // Create buttons to turn on/off all VMs for this lab
        Button turnOnButton = createTurnOnButton(labName);
        Button turnOffButton = createTurnOffButton(labName);

        // If runningVMs is 0, make the turnOffButton faded and unclickable
        if (runningVMs == 0) {
            turnOffButton.setDisable(true);
            turnOffButton.setOpacity(0.5);
        }

        // If runningVMs is equal to vmCount, make sure the turnOnButton is faded and unclickable
        if (runningVMs == vmCount) {
            turnOnButton.setDisable(true);
            turnOnButton.setOpacity(0.5);
        }

        Button captureLabButton = new Button("Capture Lab");
        captureLabButton.setOnAction(event -> openCaptureLabWindow(labName));


        // Add the captureLabButton to the HBox
        hbox.getChildren().addAll(box, labelBox, turnOnButton, turnOffButton, captureLabButton);


        // Return the created HBox containing lab information and controls
        return hbox;
    }


    private Rectangle createVpnStatusRectangle(boolean vpnRunning) {
        return new Rectangle(100, 100, vpnRunning ? Color.GREEN : Color.RED);
    }

    private VBox createLabelsVBox(String labName, String vpnIp, boolean vpnRunning, int vmCount, int runningVMs) {
        // Create a VBox to hold all the lab labels
        VBox labelBox = new VBox();
        labelBox.setSpacing(5);
        // Create a label for the lab name and set its style
        Label nameLabel = new Label(labName);
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        // Create a label for the lab name and set its style
        Label vpnIpLabel = new Label("IP: " + vpnIp);
        nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        // Create a label for the number of VMs and set its style
        Label vmLabel = new Label("VM's: " + vmCount);
        vmLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        // Create a label for the VPN status and set its color
        Label vpnStatusLabel = new Label(vpnRunning ? "VPN: Running" : "VPN: Down");
        vpnStatusLabel.setTextFill(vpnRunning ? Color.GREEN : Color.RED);
        // Create a label for the number of running VMs and set its style
        Label runningVMLabel = new Label("Running VM's: " + runningVMs + "/" + vmCount);
        runningVMLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        // Add all the labels to the VBox
        labelBox.getChildren().addAll(nameLabel,vpnIpLabel, vpnStatusLabel, runningVMLabel);
        // Return the created VBox containing lab labels
        return labelBox;
    }


    private Button createTurnOnButton(String labName) {
        // Create a button to turn on all machines in the lab
        Button turnOnButton = new Button("Turn On all machines");
        // Set up the event handler for the button
        turnOnButton.setOnAction(event -> {
            // Create a Runnable task to turn on all VMs in the specified lab
            Runnable task = () -> azureMethods.turnOnInLab(labName);
            // Start a new thread to execute the task
            Thread thread = new Thread(task);
            thread.start();
        });
        // Return the created button with the event handler
        return turnOnButton;
    }

    private Button createTurnOffButton(String labName) {
        // Create a button to turn off all machines in the lab
        Button turnOffButton = new Button("Turn Off all machines");
        // Set up the event handler for the button
        turnOffButton.setOnAction(event -> {
            // Create a Runnable task to turn off all VMs in the specified lab
            Runnable task = () -> azureMethods.turnOffVMsInLab(labName);
            // Start a new thread to execute the task
            Thread thread = new Thread(task);
            thread.start();
        });
        // Return the created button with the event handler
        return turnOffButton;
    }


    public void openCaptureLabWindow(String labName) {
        ViewSwitcher.openCaptureLabWindow(labName);
    }

}
