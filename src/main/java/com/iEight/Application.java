package com.iEight;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
public class Application {
    private static final Logger logger=Logger.getLogger(Application.class.getName());
    public static void main(String[] args) throws IOException, AWTException {
        if(SystemTray.isSupported()){

            SystemTray systemTray=SystemTray.getSystemTray();

            try {


                Image icon=ImageIO.read(Application.class.getResourceAsStream("/public/ic_launcher.png"));

                TrayIcon trayIcon = new TrayIcon(icon,"iEight server running!");

                trayIcon.setImageAutoSize(true);
                systemTray.add(trayIcon);

            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        SpringApplication.run(Application.class, args);
    }
}
