package com.archis.front;

import com.archis.bdd.BddCrud;
import com.archis.front.itfc.SettingsUpdateListener;
import com.archis.model.Settings;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.List;

import static com.archis.utils.SceneUtils.setCloseButtonPanel;

public class SettingsScene {
    private SettingsUpdateListener settingsUpdateListener;

    private JPanel mainPanel;
    private JSlider sliderOpacity;
    private JFormattedTextField valeurNombrePersonnages;
    private JTextField valeurCouleurPrincipale;
    private JTextField valeurCouleurBackground;
    private JButton majCouleurPp;
    private JButton majCouleurBg;
    private JButton closeButton;
    private JSlider sliderNbPersonnages;

    public JPanel getMainPanel() {
        mainPanel = new JPanel(new MigLayout("fillx")); // Ajoutez "fillx" pour remplir l'espace horizontal
        setValuesFromDatabaseSettings();

        closeButton = new JButton("X");
        setCloseButtonPanel(mainPanel, closeButton);
        mainPanel.add(closeButton, "span, align right, wrap");

        JLabel opacite = new JLabel("Opacite");
        mainPanel.add(opacite, "split 2, align center");
        sliderOpacity.setPreferredSize(new Dimension(200, sliderOpacity.getPreferredSize().height));
        mainPanel.add(sliderOpacity, "wrap, pushx, growx, align center"); // Ajoutez "growx" pour que le JSlider grandisse horizontalement

        JLabel nombrePersonnages = new JLabel("Nombre de personnages");
        mainPanel.add(nombrePersonnages, "split 2, align center");
        sliderNbPersonnages.setPreferredSize(new Dimension(200, sliderOpacity.getPreferredSize().height));
        mainPanel.add(sliderNbPersonnages, "wrap, pushx, growx, align center");

        JLabel couleurPrincipale = new JLabel("Couleur principale");
        mainPanel.add(couleurPrincipale);
        mainPanel.add(valeurCouleurPrincipale);
        mainPanel.add(majCouleurPp, "wrap, pushx, growx, align center");

        JLabel couleurBackground = new JLabel("Couleur de fond");
        mainPanel.add(couleurBackground);
        mainPanel.add(valeurCouleurBackground);
        mainPanel.add(majCouleurBg, "wrap, pushx, growx, align center");

        return mainPanel;
    }

    private void setValuesFromDatabaseSettings() {
        List<Settings> settings = BddCrud.getSettings();
        for (Settings setting : settings) {
            switch (setting.getNom()) {
                case "opacite":
                    setOpacite();
                    sliderOpacity.setValue(Integer.parseInt(setting.getValeur()));
                    break;
                case "nombrepersonnages":
                    setNombrePersonnages();
                    sliderNbPersonnages.setValue(Integer.parseInt(setting.getValeur()));
                    break;
                case "maincouleur":
                    setCouleurPrincipale();
                    valeurCouleurPrincipale.setText(setting.getValeur());
                    break;
                case "couleurfond":
                    setCouleurBackground();
                    valeurCouleurBackground.setText(setting.getValeur());
                    break;
            }
        }
    }

    private void setOpacite() {
        sliderOpacity = new JSlider(JSlider.HORIZONTAL, 0, 100, sliderOpacity.getValue());// Min: 0, Max: 100, Initial: 50
        sliderOpacity.setPaintTicks(true); // Afficher les marques

        sliderOpacity.setSnapToTicks(true);
        sliderOpacity.setMajorTickSpacing(10); // Espacement des marques

        sliderOpacity.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // Mettre à jour la base de données lorsque le bouton de la souris est relâché
                BddCrud.updateSettings("opacite", String.valueOf(sliderOpacity.getValue()));
                if (settingsUpdateListener != null) {
                    settingsUpdateListener.onSettingsUpdated();
                }
            }
        });
    }

    private void setNombrePersonnages() {
        sliderNbPersonnages = new JSlider(JSlider.HORIZONTAL, 0, 100, sliderOpacity.getValue());// Min: 0, Max: 100, Initial: 50
        sliderNbPersonnages.setPaintTicks(true); // Afficher les marques

        sliderNbPersonnages.setSnapToTicks(true);
        sliderNbPersonnages.setMajorTickSpacing(10); // Espacement des marques

        sliderNbPersonnages.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // Mettre à jour la base de données lorsque le bouton de la souris est relâché
                BddCrud.updateSettings("opacite", String.valueOf(sliderOpacity.getValue()));
                if (settingsUpdateListener != null) {
                    settingsUpdateListener.onSettingsUpdated();
                }
            }
        });
    }

    private void setCouleurPrincipale() {
        valeurCouleurPrincipale = new JTextField();
        valeurCouleurPrincipale.setMinimumSize(new Dimension(100, 30));
        valeurCouleurPrincipale.setColumns(7);
        valeurCouleurPrincipale.setHorizontalAlignment(JTextField.CENTER);

        majCouleurPp = new JButton("V");
        majCouleurPp.setMaximumSize(new Dimension(30, 30));
        majCouleurPp.addActionListener(e -> {
            BddCrud.updateSettings("maincouleur", valeurCouleurPrincipale.getText());
            if (settingsUpdateListener != null) {
                settingsUpdateListener.onSettingsUpdated();
            }
        });
    }

    private void setCouleurBackground() {
        valeurCouleurBackground = new JTextField();
        valeurCouleurBackground.setMinimumSize(new Dimension(100, 30));
        valeurCouleurBackground.setColumns(7);
        valeurCouleurBackground.setHorizontalAlignment(JTextField.CENTER);

        majCouleurBg = new JButton("V");
        majCouleurBg.setMaximumSize(new Dimension(30, 30));
        majCouleurBg.addActionListener(e -> {
            BddCrud.updateSettings("couleurfond", valeurCouleurBackground.getText());
            if (settingsUpdateListener != null) {
                settingsUpdateListener.onSettingsUpdated();
            }
        });
    }

    void setSettingsUpdateListener(SettingsUpdateListener settingsUpdateListener) {
        this.settingsUpdateListener = settingsUpdateListener;
    }
}
