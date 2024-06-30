package com.archis.utils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class MonstreTableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Obtenez la valeur de la colonne 'Existe dans la BDD' pour la ligne actuelle
        Object cellValue = table.getValueAt(row, 1);
        boolean existsInBdd = false;
        if (cellValue instanceof Boolean) {
            existsInBdd = (Boolean) cellValue;
        }

        // Si le monstre existe dans la BDD, coloriez la ligne en vert, sinon en rouge
        c.setBackground(existsInBdd ? new Color(0,0,0,0) : Color.RED);

        return c;
    }
}