package com.archis.front;

import com.archis.bdd.BddCrud;
import com.archis.bdd.MetamobCrud;
import com.archis.model.Monstre;
import com.archis.utils.MonstreTableCellRenderer;
import com.archis.utils.TypeAjoutEnum;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
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
        setValiderButton();
        table1.setRowHeight(40);
        table1.setPreferredScrollableViewportSize(new Dimension(table1.getPreferredSize().width, 320));
        TableColumn column = table1.getColumnModel().getColumn(1);
        column.setMinWidth(100);
        column.setMaxWidth(100);
        column.setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(table1);
        pnlInnerCenter.add(scrollPane);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.toFront();
    }

    private void setTableValues(JTable table1, List<String> monstres) {
        for (String monstre : monstres) {
            boolean existsInBdd = BddCrud.checkMonstreExists(monstre);
            model.addRow(new Object[]{monstre, existsInBdd});
        }
        table1.setModel(model);

        table1.setDefaultRenderer(Object.class, new MonstreTableCellRenderer());

        model.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                if (column == 0) {
                    String newMonstreName = (String) model.getValueAt(row, column);
                    boolean existsInBdd = BddCrud.checkMonstreExists(newMonstreName);
                    model.setValueAt(existsInBdd, row, 1);
                    table1.repaint();
                }
            }
        });
    }

    private void setValiderButton() {
        validerButton.addActionListener(e -> {
            addToMetamobAndBdd();
        });
    }

    private void addToMetamobAndBdd() {
        List<Integer> ids = new ArrayList<>();
        List<Monstre> monstres = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            String nomMonstre = (String) model.getValueAt(i, 0);
            boolean existsInBdd = (boolean) model.getValueAt(i, 1);
            if(existsInBdd) {
                Monstre monstre = BddCrud.getMonstreByName(nomMonstre);
                ids.add(monstre.getId());
                monstres.add(monstre);
            }
        }
        if (!ids.isEmpty()) {
            try {
                MetamobCrud metamobCrud = new MetamobCrud();
                boolean hasBeenAdded = metamobCrud.addMonstres(ids, TypeAjoutEnum.QUANTITE, "+1");
                if (hasBeenAdded) {
                    Window window = SwingUtilities.getWindowAncestor(pnlMain);
                    if (window != null) {
                        window.dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Erreur lors de l'ajout du monstre sur Metamob");
                }
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}


