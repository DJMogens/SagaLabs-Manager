package com.example.sagalabsmanager;

public enum View {
    LOGIN("SagaLogin-view.fxml"),
    LABS("labs.fxml"),
    VMS("VMs.fxml");

    private String fileName;

    View(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
