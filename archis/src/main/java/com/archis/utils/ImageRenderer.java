package com.archis.utils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageRenderer extends DefaultTableCellRenderer {
    JLabel lbl = new JLabel();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        lbl.setText(null);
        lbl.setIcon(null);
        if (value != null) {
            String imageUrl = value.toString();
            try {
                URL url = new URL(imageUrl);
                ImageIcon imageIcon = new ImageIcon(url);
                Image image = imageIcon.getImage();
                Image scaledImage = image.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                lbl.setIcon(new ImageIcon(scaledImage));
            } catch (MalformedURLException e) {
                System.out.println("URL de l'image incorrecte : " + e.getMessage());
            }
        }
        return lbl;
    }
}