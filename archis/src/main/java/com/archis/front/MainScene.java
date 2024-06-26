package com.archis.front;

import com.archis.bdd.BddCrud;
import com.archis.front.itfc.MonstresUpdateListener;
import com.archis.front.itfc.SettingsUpdateListener;
import com.archis.model.Settings;
import com.archis.utils.SettingsSingleton;
import com.archis.utils.TypeMonstreEnum;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import static com.archis.utils.SceneUtils.*;

public class MainScene implements SettingsUpdateListener, MonstresUpdateListener {


    private JPanel pnlMain;
    private JPanel pnlCenterMain;
    private JPanel pnlInnerNorth;
    private JPanel pnlInnerCenter;
    private JProgressBar progressBar1;
    private JComboBox typeMonstreBox;
    private JButton optiMapButton;
    private JButton addMonsterButton;
    private JButton dataButton;
    private JButton settingsButton;
    private JButton xButton;
    private JFrame settingsFrame;
    private JFrame dataFrame;
    private JFrame optiMapFrame;
    private JFrame addMonsterFrame;

    private List<Settings> settingsList;
    private int nombrePersonnages;
    private float opacite;
    private TypeMonstreEnum actualTypeMonstre = TypeMonstreEnum.ARCHIMONSTRE;

    public MainScene() {
        setPanelMouseMovable(pnlMain);
        setSettingValues();
        setCloseButtonPanel(pnlMain, xButton);
        setProgressionCaptures();
        setChoixTypeMonstreBarreProgression();
        setButtonProperties();
    }

    private void setSettingValues() {
        getSettingValues();
        for (Settings setting : settingsList) {
            switch (setting.getNom()) {
                case "opacite":
                    opacite = Integer.parseInt(setting.getValeur())/100.0f;
                    break;
                case "nombrepersonnages":
                    nombrePersonnages = Integer.parseInt(setting.getValeur());
                    break;
            }
        }
    }

    private void getSettingValues() {
        SettingsSingleton settingsSingleton = SettingsSingleton.getInstance();
        settingsList = settingsSingleton.getSettings();
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
        progressBar1.setMaximum(totalMonstres);
        progressBar1.setValue(countMonstres);
        progressBar1.setString(countMonstres + "/" + totalMonstres);
        progressBar1.setStringPainted(true);
    }

    private void setChoixTypeMonstreBarreProgression() {
        for (TypeMonstreEnum typeMonstreEnum : TypeMonstreEnum.values()) {
            typeMonstreBox.addItem(typeMonstreEnum.getDisplay());
        }
        typeMonstreBox.addActionListener(e -> {
            String selectedType = (String) typeMonstreBox.getSelectedItem();
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

    private void setButtonProperties() {
        optiMapButton.addActionListener(e -> {
            if(optiMapFrame != null) {
                optiMapFrame.toFront();
                optiMapFrame.repaint();
            } else {
                optiMapFrame = new JFrame("Optimisation de la map");
//                optiMapFrame.setContentPane(new OptiMapScene().OptiMapScene());
                setWindowProperties(optiMapFrame);
            }
        });

        addMonsterButton.addActionListener(e -> {
            if(addMonsterFrame != null) {
                addMonsterFrame.toFront();
                addMonsterFrame.repaint();
            } else {
                addMonsterFrame = new JFrame("Ajouter un monstre");
                AddMonsterScene addMonsterScene = new AddMonsterScene();
                addMonsterScene.setMonstresUpdateListener(this);
                addMonsterFrame.setContentPane(addMonsterScene.AddMonsterScene());
                addMonsterFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        addMonsterFrame = null;
                    }
                });
                addMonsterFrame.setSize(500,500);
                setWindowProperties(addMonsterFrame);
            }
        });

        dataButton.addActionListener(e -> {
            if(dataFrame != null) {
                dataFrame.toFront();
                dataFrame.repaint();
            } else {
                dataFrame = new JFrame("Données");
                dataFrame.setContentPane(new DataScene().DataScene());
                dataFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        dataFrame = null;
                    }
                });
                setWindowProperties(dataFrame);
            }
        });

        settingsButton.addActionListener(e -> {
            if(settingsFrame != null) {
                settingsFrame.toFront();
                settingsFrame.repaint();
            } else {
                SettingsScene settingsScene = new SettingsScene();
                settingsScene.setSettingsUpdateListener(this);
                settingsFrame = new JFrame("Paramètres");
                settingsFrame.setContentPane(settingsScene.SettingsScene());
                settingsFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        settingsFrame = null;
                    }
                });
                setWindowProperties(settingsFrame);
            }
        });
    }

    private static void setWindowProperties(JFrame frame) {
        frame.setUndecorated(true);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new FlatMacDarkLaf());
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Aux petits oignons");
        frame.setUndecorated(true);
        MainScene mainScene = new MainScene();
        frame.setContentPane(mainScene.pnlMain);
        frame.setOpacity(mainScene.opacite);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void onSettingsUpdated() {
        setSettingValues();
        setProgressionCaptures();
        pnlMain.revalidate();
        pnlMain.repaint();
        Window window = SwingUtilities.getWindowAncestor(pnlMain);
        if (window != null) {
            window.setOpacity(opacite);
        }
        System.out.println("post sauvegarde = " + nombrePersonnages);
    }

    @Override
    public void onMonstresUpdated() {
        setProgressionCaptures();
        pnlMain.revalidate();
        pnlMain.repaint();
    }
}
