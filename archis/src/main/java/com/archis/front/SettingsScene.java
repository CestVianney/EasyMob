package com.archis.front;

import com.archis.bdd.BddCrud;
import com.archis.front.itfc.SettingsUpdateListener;
import com.archis.model.Settings;
import com.archis.utils.SettingsSingleton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static com.archis.utils.SceneUtils.*;

public class SettingsScene {
    private JButton xButton;
    private JPanel pnlMain;
    private JPanel pnlCenterMain;
    private JPanel pnlInnerNorth;
    private JPanel pnlInnerCenter;
    private JSlider sliderOpacite;
    private JSlider sliderNbPersonnages;
    private JButton colorButton;
    private JPanel pnlInnerBottom;
    private Color chosenColor;
    private SettingsUpdateListener settingsUpdateListener;

    private List<Settings> settingsList;

    public JPanel SettingsScene() {
        getSettingValues();
        setColorChooserButtonProperties();
        setCloseButtonPanel(pnlMain, xButton);
        setPanelMouseMovable(pnlMain);
        setSliderOpaciteProperties();
        setSliderNbPersonnagesProperties();
        return pnlMain;
    }

    private void setColorChooserButtonProperties() {
        colorButton.addActionListener(e -> {
            chosenColor = JColorChooser.showDialog(null, "Choisissez une couleur", Color.RED);
            if (chosenColor != null) {
                BddCrud.updateSettings("maincouleur", chosenColor.toString());
                colorButton.setBackground(chosenColor);
                //TODO changer couleur principale
            }
        });
    }

    private void setSliderOpaciteProperties() {
        for (Settings setting : settingsList) {
            switch (setting.getNom()) {
                case "opacite":
                    sliderOpacite.setValue(Integer.parseInt(setting.getValeur()));
                    break;
            }
        }
        sliderOpacite.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent evt) {
                updateSettings("opacite", String.valueOf(sliderOpacite.getValue()));
                if (settingsUpdateListener != null) {
                    settingsUpdateListener.onSettingsUpdated();
                }
            }
        });
    }

    private void setSliderNbPersonnagesProperties() {
        sliderNbPersonnages.setPaintTrack(true);
        sliderNbPersonnages.setPaintTicks(true);
        sliderNbPersonnages.setMajorTickSpacing(1);
        sliderNbPersonnages.setSnapToTicks(true);
        sliderNbPersonnages.setPaintLabels(true);

        for (Settings setting : settingsList) {
            switch (setting.getNom()) {
                case "nombrepersonnages":
                    sliderNbPersonnages.setValue(Integer.parseInt(setting.getValeur()));
                    break;
            }
        }
        sliderNbPersonnages.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent evt) {
                updateSettings("nombrepersonnages", String.valueOf(sliderNbPersonnages.getValue()));
                if (settingsUpdateListener != null) {
                    settingsUpdateListener.onSettingsUpdated();
                }
            }
        });
    }

    private void updateSettings(String param, String value) {
        BddCrud.updateSettings(param, value);
        setSettingValues();
    }

    private void createUIComponents() {
    }

    void setSettingsUpdateListener(SettingsUpdateListener settingsUpdateListener) {
        this.settingsUpdateListener = settingsUpdateListener;
    }

    private void getSettingValues() {
        SettingsSingleton settingsSingleton = SettingsSingleton.getInstance();
        settingsList = settingsSingleton.getSettings();
    }
    private void setSettingValues() {
        SettingsSingleton settingsSingleton = SettingsSingleton.getInstance();
        settingsSingleton.updateSettings();
    }
}
