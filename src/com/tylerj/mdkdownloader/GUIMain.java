package com.tylerj.mdkdownloader;

import java.awt.*;
import javax.swing.*;

public class GUIMain {
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("MDKDownloader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel title = new JLabel("MDKDownloader v1.0");

        title.setPreferredSize(new Dimension(600, 800));

        frame.getContentPane().add(title, BorderLayout.CENTER);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}