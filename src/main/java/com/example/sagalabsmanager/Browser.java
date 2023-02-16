package com.example.sagalabsmanager;

import java.awt.Desktop;
import java.net.URI;

public class Browser {
    public static void openBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
