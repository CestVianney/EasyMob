package com.archis.front;

import com.archis.bdd.BddCrud;
import com.archis.utils.MonstreTableCellRenderer;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

import static com.archis.utils.SceneUtils.*;

public class PopupCapture {
    private JTable table1;
    private JPanel pnlInnerCenter;
    private JButton validerButton;
    private JButton xButton;
    private JPanel pnlMain;
    private JPanel pnlInnerNorth;
    private JPanel pnlCenter;
    private DefaultTableModel model;

    public PopupCapture(List<String> monstres) {
        JFrame frame = new JFrame("PopupCapture");
        setCloseButtonPanel(pnlMain, xButton);
        setPanelMouseMovable(pnlMain);
        frame.setContentPane(pnlMain);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        model = new DefaultTableModel();

        // Ajoutez des colonnes à votre modèle
        model.addColumn("Nom du Monstre");
        model.addColumn("Existe dans la BDD");

        setTableValues(table1, monstres);
        table1.setRowHeight(40);
        table1.setPreferredScrollableViewportSize(new Dimension(table1.getPreferredSize().width, 320));
        TableColumn column = table1.getColumnModel().getColumn(1);
        column.setMinWidth(0);
        column.setMaxWidth(0);
        column.setPreferredWidth(0);
        // Créez un JScrollPane contenant votre JTable
        JScrollPane scrollPane = new JScrollPane(table1);

        // Ajoutez le JScrollPane à votre JPanel
        pnlInnerCenter.add(scrollPane);

        frame.pack();
        frame.setVisible(true);
    }

    private void setTableValues(JTable table1, List<String> monstres) {
        for (String monstre : monstres) {
            boolean existsInBdd = BddCrud.checkMonstreExists(monstre);
            model.addRow(new Object[]{monstre, existsInBdd});
        }
        table1.setModel(model);

        // Utilisez le TableCellRenderer personnalisé pour votre table
        table1.setDefaultRenderer(Object.class, new MonstreTableCellRenderer());

        model.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                // Si la colonne 'Nom du Monstre' est modifiée
                if (column == 0) {
                    // Obtenez le nouveau nom du monstre
                    String newMonstreName = (String) model.getValueAt(row, column);

                    // Vérifiez si le nouveau monstre existe dans la BDD
                    boolean existsInBdd = BddCrud.checkMonstreExists(newMonstreName);

                    // Mettez à jour la colonne 'Existe dans la BDD' avec le nouveau résultat
                    model.setValueAt(existsInBdd, row, 1);

                    // Redessinez la table pour mettre à jour les couleurs des lignes
                    table1.repaint();
                }
            }
        });
    }
}

