package sagalabsmanagerclient;

public enum View {
    //ENUM types for opening fxml files with the ViewSwitcher function
    LOGIN("Login.fxml"),
    HOME("Home.fxml"),
    SQLSCENE("SQL.fxml"),
    MACHINES("VMs.fxml");


    private String fileName;

    View(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
