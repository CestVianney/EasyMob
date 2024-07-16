package com.easymob.front;

import com.easymob.bdd.BddCrud;
import com.easymob.front.itfc.SettingsUpdateListener;
import com.easymob.model.Settings;
import com.easymob.utils.SettingsSingleton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static com.easymob.utils.SceneUtils.*;

public class SettingsScene {
    private JButton xButton;
    private JPanel pnlMain;
    private JPanel pnlCenterMain;
    private JPanel pnlInnerNorth;
    private JPanel pnlInnerCenter;
    private JSlider sliderOpacite;
    private JPanel pnlInnerBottom;
    private JTextField apiKeyTextField;
    private JTextField userKeyTextField;
    private JButton validerButton;
    private JTextField nomMetamob;
    private JCheckBox activerMetamobCheckBox;
    private JTextField raccourciCaptureTextField;
    private SettingsUpdateListener settingsUpdateListener;

    private List<Settings> settingsList;

    public JPanel SettingsScene() {
        getSettingValues();
        setCloseButtonPanel(pnlMain, xButton);
        setPanelMouseMovable(pnlMain);
        setSliderOpaciteProperties();
        setTextAreasValues();
        setValiderButton();
        setRaccourciCaptureProperties();
        return pnlMain;
    }

    private void setTextAreasValues() {
        for (Settings setting : settingsList) {
            switch (setting.getNom()) {
                case "apiKey":
                    apiKeyTextField.setText(setting.getValeur());
                    break;
                case "userKey":
                    userKeyTextField.setText(setting.getValeur());
                    break;
                case "nomPersonnage":
                    nomMetamob.setText(setting.getValeur());
                    break;
                case "toucheCapture":
                    raccourciCaptureTextField.setText(setting.getValeur());
                    break;
            }
        }
    }

    private void setValiderButton() {
        validerButton.addActionListener(e -> {
            updateSettings("apiKey", apiKeyTextField.getText());
            updateSettings("userKey", userKeyTextField.getText());
            updateSettings("nomPersonnage", nomMetamob.getText());
            updateSettings("toucheCapture", raccourciCaptureTextField.getText());
            if (settingsUpdateListener != null) {
                settingsUpdateListener.onSettingsUpdated();
            }
            Window window = SwingUtilities.getWindowAncestor(pnlMain);
            if (window != null) {
                window.dispose();
            }
        });
    }



    private void setRaccourciCaptureProperties() {
        raccourciCaptureTextField.addActionListener(e -> {
            updateSettings("toucheCapture", raccourciCaptureTextField.getText().toUpperCase());
            if (settingsUpdateListener != null) {
                settingsUpdateListener.onSettingsUpdated();
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

    private void updateSettings(String param, String value) {
        BddCrud.updateSettings(param, value);
        setSettingValues();
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
