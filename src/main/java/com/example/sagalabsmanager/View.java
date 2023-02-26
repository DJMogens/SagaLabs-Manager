package com.example.sagalabsmanager;

public enum View {
    //ENUM types for opening fxml files with the ViewSwitcher function
    LOGIN("SagaLogin-view.fxml"),
    LABS("labs.fxml"),
    SQLSCENE("SceneSQL.fxml"),
    MACHINES("SceneVM.fxml");


    private String fileName;

    View(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
