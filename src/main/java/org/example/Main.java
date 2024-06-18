package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        if (!GraphicsEnvironment.isHeadless()) {
            SwingUtilities.invokeLater(TableApp::new);
        } else {
            logger.error("Cannot start GUI in a headless environment");
        }
    }
}