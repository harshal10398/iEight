package com.iEight;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
public class Application {
    private static final Logger logger=Logger.getLogger(Application.class.getName());
    public static void main(String[] args) {
        if(SystemTray.isSupported()){
            SystemTray systemTray=SystemTray.getSystemTray();
            TrayIcon trayIcon = new TrayIcon();

            trayIcon.setImage();
            systemTray.add();
        }
        SpringApplication.run(Application.class, args);
    }
}
