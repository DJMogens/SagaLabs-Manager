package sagalabsmanagerclient;

public enum View {
    //ENUM types for opening fxml files with the ViewSwitcher function
    LOGIN("Login.fxml"),
    HOME("Home.fxml"),
    VPN("Vpn.fxml"),
    MACHINES("Machines.fxml"),
    CAPTURE_LAB_WINDOW("CaptureLab.fxml");
    final private String fileName;

    View(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
