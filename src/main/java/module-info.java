module com.example.sagalabsmanager {
    requires javafx.controls;
    requires javafx.fxml;
        requires javafx.web;
            
        requires org.controlsfx.controls;
            requires com.dlsc.formsfx;
            requires net.synedra.validatorfx;
            requires org.kordamp.ikonli.javafx;
                requires eu.hansolo.tilesfx;
        
    opens com.example.sagalabsmanager to javafx.fxml;
    exports com.example.sagalabsmanager;
}