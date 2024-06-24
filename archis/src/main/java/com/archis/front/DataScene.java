package com.archis.front;

import javax.swing.*;

import static com.archis.utils.SceneUtils.setCloseButtonPanel;
import static com.archis.utils.SceneUtils.setPanelMouseMovable;

public class DataScene {

    private JPanel pnlMain;
    private JPanel pnlCenter;
    private JPanel pnlInnerNorth;
    private JButton xButton;

    public JPanel DataScene() {
        setCloseButtonPanel(pnlMain, xButton);
        setPanelMouseMovable(pnlMain);
        return pnlMain;
    }

}
