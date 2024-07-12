package com.archis.front;

import com.archis.bdd.BddCrud;
import com.archis.bdd.MetamobCrud;
import com.archis.front.itfc.ScreenCaptureListener;
import com.archis.front.itfc.SettingsUpdateListener;
import com.archis.model.Settings;
import com.archis.ocr.ScreenCapture;
import com.archis.utils.SettingsSingleton;
import com.archis.utils.TypeMonstreEnum;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.archis.utils.SceneUtils.*;

public class MainScene implements SettingsUpdateListener, ScreenCaptureListener {


    private JPanel pnlMain;
    private JPanel pnlCenterMain;
    private JPanel pnlInnerNorth;
    private JPanel pnlInnerCenter;
    private JButton optiMapButton;
    private JButton screenButton;
    private JButton settingsButton;
    private JButton xButton;
    private JCheckBox useDernierRectangleCheckBox;
    private JButton resetRectangleButton;
    private JButton importData;
    private JButton helpButton;
    private JFrame settingsFrame;
    private JFrame dataFrame;
    private JFrame optiMapFrame;
    private JFrame addMonsterFrame;
    private JFrame readMeFrame;

    private List<Settings> settingsList;
    private float opacite;
    private String apiKey;
    private String userKey;
    private String toucheCapture;
    private TypeMonstreEnum actualTypeMonstre = TypeMonstreEnum.ARCHIMONSTRE;

    public MainScene() {
        setPanelMouseMovable(pnlMain);
        setSettingValues();
        setCloseButtonPanel(pnlMain, xButton);
        setButtonProperties();
        setRectangleSelection();
        setImportDataButton();
        setScreenButton();
        setReadmeButton();
    }

    private void setReadmeButton() {
        helpButton.addActionListener(e -> {
            if(readMeFrame != null) {
                readMeFrame.toFront();
                readMeFrame.repaint();
            } else {
                ReadmeScene readmeScene = new ReadmeScene();
                readMeFrame = new JFrame("ReadMe");
                readMeFrame.setContentPane(readmeScene.ReadmeScene());
                readMeFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        readMeFrame = null;
                    }
                });
                setWindowProperties(readMeFrame);
            }
        });

    }

    private void setImportDataButton() {
        boolean areMonstersSet = BddCrud.isDatabaseSetWithData();
        if (areMonstersSet) {
            importData.setBackground(Color.GREEN);
        } else {
            importData.setBackground(Color.RED);
        }
        importData.addActionListener(e -> {
            MetamobCrud metamobCrud = new MetamobCrud();
            boolean areMonstresSet = metamobCrud.getAllMonstres();
            if (areMonstresSet) {
                JOptionPane.showMessageDialog(null, "Les monstres ont bien été importés");
                importData.setBackground(Color.GREEN);
            } else {
                JOptionPane.showMessageDialog(null, "Une erreur est survenue lors de l'importation des monstres", "Erreur", JOptionPane.ERROR_MESSAGE);
                importData.setBackground(Color.RED);
            }
        });
    }

    private void setRectangleSelection() {
        useDernierRectangleCheckBox.addActionListener(e -> {
            if (useDernierRectangleCheckBox.isSelected()) {
                BddCrud.updateSettings("useLastRectangle", "true");
            } else {
                BddCrud.updateSettings("useLastRectangle", "false");
            }
        });
        resetRectangleButton.addActionListener(e -> {
            BddCrud.updateSettings("rectangle", "");
        });
    }

    private void setSettingValues() {
        getSettingValues();
        for (Settings setting : settingsList) {
            switch (setting.getNom()) {
                case "opacite":
                    opacite = Integer.parseInt(setting.getValeur())/100.0f;
                    break;
                case "apikey":
                    apiKey = setting.getValeur();
                    break;
                case "userKey":
                    userKey = setting.getValeur();
                    break;
                case "toucheCapture":
                    toucheCapture = setting.getValeur();
                    break;
                case "useLastRectangle":
                    useDernierRectangleCheckBox.setSelected(Boolean.parseBoolean(setting.getValeur()));
                    break;
                default:
                    break;
            }
        }
    }

    private void getSettingValues() {
        SettingsSingleton settingsSingleton = SettingsSingleton.getInstance();
        settingsList = settingsSingleton.getSettings();
    }

    private void setButtonProperties() {
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

    private void setScreenButton() {
        try {
            screenButton.setVisible(false);
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            public void nativeKeyPressed(NativeKeyEvent e) {
                if (NativeKeyEvent.getKeyText(e.getKeyCode()).equals(toucheCapture)) {
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
                ScreenCapture screenCapture = new ScreenCapture(useDernierRectangleCheckBox.isSelected());
                screenCapture.setScreenCaptureListener(this);
                screenCapture.captureAndExtractMonstres();
            } catch (AWTException ex) {
                throw new RuntimeException(ex);
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
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("EasyMob");
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
        pnlMain.revalidate();
        pnlMain.repaint();
        Window window = SwingUtilities.getWindowAncestor(pnlMain);
        if (window != null) {
            window.setOpacity(opacite);
        }
    }

    @Override
    public void onCaptureCompleted(List<String> monstres) {
        new PopupCapture(monstres);
    }
}
