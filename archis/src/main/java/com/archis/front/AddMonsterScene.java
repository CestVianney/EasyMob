package com.archis.front;

import com.archis.bdd.BddCrud;
import com.archis.front.itfc.MonstresUpdateListener;
import com.archis.model.Monstre;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static com.archis.utils.SceneUtils.setCloseButtonPanel;
import static com.archis.utils.SceneUtils.setPanelMouseMovable;

public class AddMonsterScene {
    private JPanel pnlMain;
    private JPanel pnlCenter;
    private JPanel pnlInnerNorth;
    private JPanel pnlInnerCentral;
    private JButton xButton;
    private JButton screenButton;
    private JTextField nomMonstreText;
    private JList suggestionsList;
    private JButton addMonsterButton;
    private JPanel pnlInnerSouth;
    private JTable table1;
    private DefaultListModel<String> listModel;

    private Monstre selectedMonster;
    private Map<String, Monstre> monsterMap;
    private MonstresUpdateListener monstresUpdateListener;


    public JPanel AddMonsterScene() {
        monsterMap = new HashMap<>();
        setCloseButtonPanel(pnlMain, xButton);
        setPanelMouseMovable(pnlMain);
        setTextSuggestions();
        setAddMonsterButtonProperties();
        setHistoriquePanel();
        setList();
        return pnlMain;
    }

    private void setList() {
        listModel = new DefaultListModel<>();
        listModel.addElement(" ");
        listModel.addElement(" ");
        listModel.addElement(" ");
        listModel.addElement(" ");
        listModel.addElement(" ");
        listModel.addElement(" ");
        listModel.addElement(" ");
        listModel.addElement(" ");
        listModel.addElement(" ");
        listModel.addElement(" ");
        suggestionsList.setModel(listModel);
    }
    private void setTextSuggestions() {

        nomMonstreText.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
        nomMonstreText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            private void updateSuggestions() {
                String text = nomMonstreText.getText();
                if (text.length() >= 3) {
                    List<Monstre> monsters = BddCrud.getMonstersStartingWith(text);
                    listModel.clear();
                    monsterMap.clear();
                    for (Monstre monster : monsters) {
                        String monsterName;
                        if (monster.getNomArchimonstre().isEmpty()) {
                            monsterName = monster.getNomMonstre();
                        } else {
                            monsterName = monster.getNomArchimonstre() + " (" + monster.getNomMonstre() + ")";
                        }
                        listModel.addElement(monsterName);
                        monsterMap.put(monsterName, monster);
                    }
                    int difference = 10 - monsters.size();
                    for (int i = 0; i < difference; i++) {
                        listModel.addElement(" ");
                    }
                    suggestionsList.setModel(listModel);
                    suggestionsList.setVisibleRowCount(5);
                    suggestionsList.setSelectedIndex(0);
                }
            }
        });

        suggestionsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedMonsterName = (String) suggestionsList.getSelectedValue();
                    if (selectedMonsterName != null) {
                        Monstre selectedMonster = monsterMap.get(selectedMonsterName);
                        if (selectedMonster != null) {
                            BddCrud.addMonster(selectedMonster);
                            setHistoriquePanel();
                            table1.repaint();
                            if (monstresUpdateListener != null) {
                                monstresUpdateListener.onMonstresUpdated();
                            }

                        }
                    }
                }
            }
        });

    }

    private void setAddMonsterButtonProperties() {
        addMonsterButton.addActionListener(e -> {
            String selectedMonsterName = (String) suggestionsList.getSelectedValue();
            if (selectedMonsterName != null) {
                Monstre selectedMonster = monsterMap.get(selectedMonsterName);
                if (selectedMonster != null) {
                    BddCrud.addMonster(selectedMonster);
                    if (monstresUpdateListener != null) {
                        monstresUpdateListener.onMonstresUpdated();
                    }
                }
            }
        });
    }

    private void setHistoriquePanel() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("img");
        model.addColumn("nom");
        model.addColumn("Action");

        table1.setModel(model);
        table1.setRowHeight(38);
        table1.setSize(300, 304);
        table1.setMaximumSize(new Dimension(300, 304));
        table1.setPreferredScrollableViewportSize(new Dimension(300, 304));
        table1.setFillsViewportHeight(true);

        TableColumn column0 = table1.getColumnModel().getColumn(0); // Colonne "img"
        column0.setPreferredWidth(38);
        column0.setMaxWidth(38);
        TableColumn column1 = table1.getColumnModel().getColumn(1); // Colonne "nom"
        column1.setPreferredWidth(200);
        TableColumn column2 = table1.getColumnModel().getColumn(2); // Colonne "Action"
        column2.setPreferredWidth(40);

        table1.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        table1.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox()));

        StripedRowRenderer stripedRowRenderer = new StripedRowRenderer();
        for (int i = 0; i < table1.getColumnCount(); i++) {
            table1.getColumnModel().getColumn(i).setCellRenderer(stripedRowRenderer);
        }

        List<Monstre> historiqueMonstres = BddCrud.getHistorique();
        historiqueMonstres.forEach(monstre ->
            model.addRow(new Object[]{"", monstre.getNomArchimonstre().isEmpty() ? monstre.getNomMonstre() : monstre.getNomArchimonstre(), "Supprimer"})
        );
    }


    void setMonstresUpdateListener(MonstresUpdateListener monstresUpdateListener) {
        this.monstresUpdateListener = monstresUpdateListener;
    }

}

class ButtonRenderer extends JButton implements TableCellRenderer {

    public ButtonRenderer() {
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(UIManager.getColor("Button.background"));
        }
        setText((value == null) ? "" : value.toString());
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;

    public ButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped());
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
        }
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

    public Object getCellEditorValue() {
        if (isPushed) {
            // Vous pouvez afficher un message d'information ici
            // ou effectuer une action en base de données
            System.out.println(label + ": Button pressed");
        }
        isPushed = false;
        return new String(label);
    }

    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}

class StripedRowRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
            c.setBackground(row % 2 == 0 ? Color.DARK_GRAY : table.getBackground());
        }
        return c;
    }
}