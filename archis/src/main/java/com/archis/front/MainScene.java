package com.archis.front;

import com.archis.bdd.BddCrud;
import com.archis.front.itfc.SettingsUpdateListener;
import com.archis.model.Settings;
import com.archis.utils.TypeMonstreEnum;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.archis.utils.SceneUtils.*;

public class MainScene implements SettingsUpdateListener {
    private JPanel mainPanel;
    private JProgressBar progressionCaptures;
    private JComboBox choixTypeMonstreBarreProgression;
    private JButton optiMap;
    private JButton addMonster;
    private JButton data;
    private JButton settings;
    private JButton closeButton;
    private TypeMonstreEnum actualTypeMonstre = TypeMonstreEnum.ARCHIMONSTRE;
    private JFrame optiMapWindow = null;
    private JFrame addMonsterWindow = null;
    private JFrame dataWindow = null;
    private JFrame settingsWindow = null;
    private int nombrePersonnages;
    private float opacite;
    private String couleurPrincipale;
    private String couleurBackground;

    @Override
    public void onSettingsUpdated() {
        getValuesFromSettings();
        setNewValues();
        mainPanel.revalidate();
        mainPanel.repaint();
        Window window = SwingUtilities.getWindowAncestor(mainPanel);
        if (window != null) {
            window.setOpacity(opacite); // Set the new opacity
        }
    }

    private void setNewValues() {
        setProgressionCaptures();
        setData();
        setAddMonster();
        setOptiMap();
        if (optiMapWindow != null) {
            optiMapWindow.setOpacity(opacite);
        }
        if (addMonsterWindow != null) {
            addMonsterWindow.setOpacity(opacite);
        }
        if (dataWindow != null) {
            dataWindow.setOpacity(opacite);
        }
    }

    private void getValuesFromSettings() {
        List<Settings> settings = BddCrud.getSettings();
        for (Settings setting : settings) {
            switch (setting.getNom()) {
                case "opacite":
                    opacite = Integer.parseInt(setting.getValeur())/100.0f;
                    break;
                case "nombrepersonnages":
                    nombrePersonnages = Integer.parseInt(setting.getValeur());
                    break;
                case "maincouleur":
                    couleurPrincipale = setting.getValeur();
                    break;
                case "couleurfond":
                    couleurBackground = setting.getValeur();
                    break;
            }
        }
    }

    public MainScene() {
        mainPanel = new JPanel(new MigLayout());
        setPanelMouseMovable(mainPanel);
        getValuesFromSettings();
        progressionCaptures = new JProgressBar();
        setProgressionCaptures();
        choixTypeMonstreBarreProgression = new JComboBox();
        setChoixTypeMonstreBarreProgression();
        optiMap = new JButton(couleurBackground);
        setOptiMap();
        addMonster = new JButton("Ajouter un monstre");
        setAddMonster();
        data = new JButton("Données");
        setData();
        settings = new JButton("Paramètres");
        setSettings();
        closeButton = new JButton("X");
        setCloseButtonPanel(mainPanel, closeButton);

        mainPanel.add(closeButton, "span, align right, wrap");
        mainPanel.add(progressionCaptures, "pushx, growx");
        mainPanel.add(choixTypeMonstreBarreProgression, "wrap");
        mainPanel.add(optiMap, "span, growx, wrap");
        mainPanel.add(addMonster, "span, growx, wrap");
        mainPanel.add(data, "span, growx, wrap");
        mainPanel.add(settings, "span, growx, wrap");
    }

    private void setProgressionCaptures() {
        int totalMonstres;
        int countMonstres;
        if(actualTypeMonstre == TypeMonstreEnum.TOUS) {
            totalMonstres = BddCrud.selectAllArchimonstres(nombrePersonnages);
            countMonstres = BddCrud.selectAllArchimonstresWithNombre(nombrePersonnages);
        } else {
            totalMonstres = BddCrud.selectAllArchimonstresByType(actualTypeMonstre.getTypeBdd(), nombrePersonnages);
            countMonstres = BddCrud.selectAllArchimonstresByTypeWithNombre(actualTypeMonstre.getTypeBdd(), nombrePersonnages);
        }
        System.out.println(totalMonstres);
        progressionCaptures.setMaximum(totalMonstres);
        progressionCaptures.setValue(countMonstres);
        progressionCaptures.setString(countMonstres + "/" + totalMonstres);
        progressionCaptures.setStringPainted(true);
    }

    private void setChoixTypeMonstreBarreProgression() {
        choixTypeMonstreBarreProgression.addItem(TypeMonstreEnum.ARCHIMONSTRE.getDisplay());
        choixTypeMonstreBarreProgression.addItem(TypeMonstreEnum.MONSTRE.getDisplay());
        choixTypeMonstreBarreProgression.addItem(TypeMonstreEnum.BOSS.getDisplay());
        choixTypeMonstreBarreProgression.addItem(TypeMonstreEnum.TOUS.getDisplay());
        choixTypeMonstreBarreProgression.addActionListener(e -> {
            String selectedType = (String) choixTypeMonstreBarreProgression.getSelectedItem();
            switch (selectedType) {
                case "Archimonstre":
                    actualTypeMonstre = TypeMonstreEnum.ARCHIMONSTRE;
                    break;
                case "Monstre":
                    actualTypeMonstre = TypeMonstreEnum.MONSTRE;
                    break;
                case "Boss":
                    actualTypeMonstre = TypeMonstreEnum.BOSS;
                    break;
                case "Tous":
                    actualTypeMonstre = TypeMonstreEnum.TOUS;
                    break;
                default:
                    actualTypeMonstre = null;
                    break;
            }
            setProgressionCaptures();
        });
    }

    private void setOptiMap() {
        optiMap.addActionListener(e -> {
            if (optiMapWindow == null || !optiMapWindow.isVisible()) {
                optiMapWindow = new JFrame("OptiMap");
                optiMapWindow.setUndecorated(true);
                optiMapWindow.setSize(300, 200);
                optiMapWindow.setOpacity(opacite);
                optiMapWindow.setLocationRelativeTo(null);
                optiMapWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                setFrameMouseMovable(optiMapWindow);
                optiMapWindow.setVisible(true);
            } else {
                optiMapWindow.toFront();
                optiMapWindow.repaint();
            }
        });
    }

    private void setAddMonster() {
        addMonster.addActionListener(e -> {
            // Vérifiez si la fenêtre OptiMap est déjà ouverte
            if (addMonsterWindow == null || !addMonsterWindow.isVisible()) {
                addMonsterWindow = new JFrame("Ajouter un monstre");
                addMonsterWindow.setUndecorated(true);
                addMonsterWindow.setOpacity(opacite);
                addMonsterWindow.setSize(300, 200);
                addMonsterWindow.setLocationRelativeTo(null);
                addMonsterWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                addMonsterWindow.setVisible(true);
                setFrameMouseMovable(addMonsterWindow);
            } else {
                addMonsterWindow.toFront();
                addMonsterWindow.repaint();
            }
        });
    }

    private void setData() {
        data.addActionListener(e -> {
            if (dataWindow == null || !dataWindow.isVisible()) {
                // Créer une nouvelle fenêtre
                dataWindow = new JFrame("Data");
                dataWindow.setSize(300, 200);
                dataWindow.setUndecorated(true);
                dataWindow.setOpacity(opacite);
                dataWindow.setLocationRelativeTo(null);
                dataWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                dataWindow.setVisible(true);
                setFrameMouseMovable(dataWindow);
            } else {
                dataWindow.toFront();
                dataWindow.repaint();
            }
        });
    }

    private void setSettings(){
        settings.addActionListener(e -> {
            if (settingsWindow == null || !settingsWindow.isVisible()) {
                SettingsScene settingsScene = new SettingsScene();
                settingsScene.setSettingsUpdateListener(this);
                settingsWindow = new JFrame("Data");
                settingsWindow.setUndecorated(true);
                settingsWindow.setContentPane(settingsScene.getMainPanel());
                settingsWindow.setSize(300, 200);
                settingsWindow.setLocationRelativeTo(null);
                settingsWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                settingsWindow.setVisible(true);
                setFrameMouseMovable(settingsWindow);
            } else {
                settingsWindow.toFront();
                settingsWindow.repaint();
            }
        });
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new FlatMacDarkLaf());
        JFrame.setDefaultLookAndFeelDecorated(true);

        JFrame frame = new JFrame("Archutility");
        frame.setUndecorated(true);
        frame.setContentPane(new MainScene().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {

    }

}
