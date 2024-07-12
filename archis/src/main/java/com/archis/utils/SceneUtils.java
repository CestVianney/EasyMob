package com.archis.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class SceneUtils {
    private static Point initialClick;

    public static void setCloseButtonPanel(JPanel panel, JButton closeButton) {
        closeButton.setBackground(Color.RED);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);

        closeButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                closeButton.setBackground(Color.ORANGE);
            }
            public void mouseExited(MouseEvent e) {
                closeButton.setBackground(Color.RED);
            }
        });
        closeButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(panel);
            if (window != null) {
                window.dispose();
            }
        });
    }

    public static void setPanelMouseMovable(JPanel panel) {
        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Window window = SwingUtilities.getWindowAncestor(panel);

                if (window != null) {
                    int thisX = window.getLocation().x;
                    int thisY = window.getLocation().y;

                    int xMoved = e.getX() - initialClick.x;
                    int yMoved = e.getY() - initialClick.y;

                    int X = thisX + xMoved;
                    int Y = thisY + yMoved;
                    window.setLocation(X, Y);
                }
            }
        });
    }
}
