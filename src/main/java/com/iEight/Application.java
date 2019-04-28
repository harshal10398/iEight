package com.iEight;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
public class Application {
    private static final Logger logger = Logger.getLogger(Application.class.getName());
    private static ConfigurableApplicationContext context;

    public static void main(String[] args) throws IOException, AWTException, URISyntaxException {

        if(SystemTray.isSupported()){

            SystemTray systemTray=SystemTray.getSystemTray();
            try {

                final Image icon=ImageIO.read(
                        Application.class.getResourceAsStream(
                                "/public/ic_launcher/res/mipmap-hdpi/ic_launcher.png"));

                final PopupMenu popupMenu = new PopupMenu("iEight server running!");
                MenuItem exitItem=new MenuItem("Exit");
                exitItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        logger.log(Level.SEVERE,actionEvent.getActionCommand()+"|"+actionEvent.paramString()+"|"+actionEvent.getSource().getClass().getName());
                        SpringApplication.exit(context);
                        System.exit(0);
                    }
                });
                popupMenu.add(exitItem);

                final TrayIcon trayIcon = new TrayIcon(icon,"iEight server running!",popupMenu);
                trayIcon.setImageAutoSize(true);
                trayIcon.setPopupMenu(popupMenu);

                systemTray.add(trayIcon);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        context=SpringApplication.run(Application.class, args);
        if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)){
            Desktop.getDesktop().browse(new URI("http://localhost:8080"));
        }
    }
}
