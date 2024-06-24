package com.archis.front;

import com.archis.bdd.BddCrud;
import com.archis.front.itfc.MonstresUpdateListener;
import com.archis.model.Monstre;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.archis.utils.SceneUtils.setCloseButtonPanel;
import static com.archis.utils.SceneUtils.setPanelMouseMovable;

public class AddMonsterScene {
    private JPanel pnlMain;
    private JPanel pnlCenter;
    private JPanel pnlInnerNorth;
    private JPanel pnlInnerEast;
    private JPanel pnlInnerWest;
    private JButton xButton;
    private JButton screenButton;
    private JTextField nomMonstreText;
    private JList suggestionsList;
    private JButton addMonsterButton;
    private DefaultListModel<String> listModel;

    private Monstre selectedMonster;
    private Map<String, Monstre> monsterMap;
    private MonstresUpdateListener monstresUpdateListener;



    public JPanel AddMonsterScene() {
        listModel = new DefaultListModel<>();
        monsterMap = new HashMap<>();
        setCloseButtonPanel(pnlMain, xButton);
        setPanelMouseMovable(pnlMain);
        setTextSuggestions();
        setAddMonsterButtonProperties();

        return pnlMain;
    }

    private void setTextSuggestions() {

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
                    suggestionsList.setModel(listModel);
                    suggestionsList.setVisibleRowCount(5);
                    suggestionsList.setSelectedIndex(0);
                }
            }
        });

    }

    private void setAddMonsterButtonProperties() {
        addMonsterButton.addActionListener(e -> {
            String selectedMonsterName = (String) suggestionsList.getSelectedValue();
            if(selectedMonsterName != null) {
                Monstre selectedMonster = monsterMap.get(selectedMonsterName);
                if(selectedMonster != null) {
                    BddCrud.addMonster(selectedMonster);
                    if(monstresUpdateListener != null) {
                        monstresUpdateListener.onMonstresUpdated();
                    }
                }
            }
        });
    }

    void setMonstresUpdateListener(MonstresUpdateListener monstresUpdateListener) {
        this.monstresUpdateListener = monstresUpdateListener;
    }
}
