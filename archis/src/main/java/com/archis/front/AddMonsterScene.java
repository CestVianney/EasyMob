package com.archis.front;

import com.archis.bdd.BddCrud;
import com.archis.front.itfc.MonstresUpdateListener;
import com.archis.front.itfc.ScreenCaptureListener;
import com.archis.model.Monstre;
import com.archis.ocr.ScreenCapture;
import com.archis.utils.ImageRenderer;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

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

import static com.archis.utils.SceneUtils.setCloseButtonPanel;
import static com.archis.utils.SceneUtils.setPanelMouseMovable;

public class AddMonsterScene implements ScreenCaptureListener {
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
    private Map<String, Monstre> monstreMap = new HashMap<>();


    public JPanel AddMonsterScene() throws AWTException {
        monsterMap = new HashMap<>();
        setCloseButtonPanel(pnlMain, xButton);
        setPanelMouseMovable(pnlMain);
        setTextSuggestions();
        setAddMonsterButtonProperties();
        setHistoriquePanel();
        setList();
        setScreenButton();

        return pnlMain;
    }

    private void setScreenButton() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            public void nativeKeyPressed(NativeKeyEvent e) {
                if (e.getKeyCode() == NativeKeyEvent.VC_F1) {
                    screenButton.doClick();
                }
            }

            public void nativeKeyReleased(NativeKeyEvent e) {
                // Nothing here
            }

            public void nativeKeyTyped(NativeKeyEvent e) {
                // Nothing here
            }
        });

        screenButton.addActionListener(e -> {
            try {
                ScreenCapture screenCapture = new ScreenCapture();
                screenCapture.setScreenCaptureListener(this);
                screenCapture.captureAndExtractMonstres();
            } catch (AWTException ex) {
                throw new RuntimeException(ex);
            }
        });
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
                        listModel.addElement(monster.getNom());
                        monsterMap.put(monster.getNom(), monster);
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

    void setHistoriquePanel() {
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
        table1.getColumnModel().getColumn(2).setCellEditor(new ButtonEditorAddMonster(new JCheckBox(), this, monstresUpdateListener, table1));

        StripedRowRenderer stripedRowRenderer = new StripedRowRenderer();
        for (int i = 0; i < table1.getColumnCount(); i++) {
            table1.getColumnModel().getColumn(i).setCellRenderer(stripedRowRenderer);
        }

        List<Monstre> historiqueMonstres = BddCrud.getHistorique();
        historiqueMonstres.forEach(monstre -> {
            String monstreName = monstre.getNom();
            String imgUrl = monstre.getImage();
            monstreMap.put(monstreName, monstre);
            model.addRow(new Object[]{imgUrl, monstreName, "Supprimer"});
        });
        table1.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());
    }

    public Map<String, Monstre> getMonstreMap() {
        return monstreMap;
    }

    void setMonstresUpdateListener(MonstresUpdateListener monstresUpdateListener) {
        this.monstresUpdateListener = monstresUpdateListener;
    }

    @Override
    public void onCaptureCompleted(List<String> monstres) {
        new PopupCapture(monstres);
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

class ButtonEditorAddMonster extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;
    private Monstre monstre;
    private AddMonsterScene addMonsterScene;
    private MonstresUpdateListener monstresUpdateListener;
    private JTable table;

    public ButtonEditorAddMonster(JCheckBox checkBox, AddMonsterScene addMonsterScene, MonstresUpdateListener monstresUpdateListener, JTable table) {
        super(checkBox);
        this.addMonsterScene = addMonsterScene;
        this.monstresUpdateListener = monstresUpdateListener;
        this.table = table;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> {
            if (table.getSelectedRow() != -1) {
                fireEditingStopped();
            }
        });
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

        // Récupérer le monstre à partir de la Map
        String monstreName = table.getModel().getValueAt(row, 1).toString();
        this.monstre = addMonsterScene.getMonstreMap().get(monstreName);

        return button;
    }

    public Object getCellEditorValue() {
        if (isPushed) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1 && selectedRow < table.getModel().getRowCount()) { // Check if the row exists in the table model
                if (table.getCellEditor() != null) {
                    table.getCellEditor().stopCellEditing();
                }
                BddCrud.removeOneMonster(monstre);
                addMonsterScene.setHistoriquePanel();
                monstresUpdateListener.onMonstresUpdated();
            }
        }
        isPushed = false;
        return new String(label);
    }
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    protected void fireEditingStopped() {
        if (table.getSelectedRow() != -1) {
            super.fireEditingStopped();
        }
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