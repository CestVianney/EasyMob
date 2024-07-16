package com.easymob.front;

import javax.swing.*;

import static com.easymob.utils.SceneUtils.setCloseButtonPanel;
import static com.easymob.utils.SceneUtils.setPanelMouseMovable;

public class ReadmeScene {
    private JPanel pnlMain;
    private JPanel pnlCenterMain;
    private JPanel pnlInnerNorth;
    private JButton xButton;
    private JTextPane readMeTextPanel;

    public JPanel ReadmeScene() {
        setCloseButtonPanel(pnlMain, xButton);
        setPanelMouseMovable(pnlMain);
        setReadMeText();
        return pnlMain;
    }

    private void setReadMeText() {
        readMeTextPanel.setContentType("text/html");
        readMeTextPanel.setText(
                "<html>" +
                        "<b>Première utilisation - Renseignez via les paramètres les <span style='color:#87CEEB;'>informations requises</span> (nom metamob, clé API, clé utilisateur et touche de capture).</b><br>" +
                        "<b>Cliquez sur le <span style='color:#87CEEB;'>bouton d'import</span> (qui doit être <span style='color:red;'>rouge</span> au premier lancement).</b>" +
                        " <br>Le bouton deviendra <span style='color:green;'>vert</span> et il n'y aura plus besoin d'y toucher par la suite (sauf si Ankama change des noms...).<br>" +
                        " Note : <b> <span style='color:#87CEEB;'>Ce n'est à faire qu'une seule fois !</span></b> Ces valeurs seront stockées en local sur votre machine.<br><br>" +
                        "<b>1a - Afin d'effectuer une capture, appuyez sur la touche que vous avez configurée (par exemple : F1).</b>" +
                        " Une fenêtre représentant une capture d'écran s'ouvrira, et il vous suffira de <b>sélectionner une large zone</b> (par exemple, tout le cadre de la pierre d'âme).<br>" +
                        "<b>1b - Une popup s'ouvrira avec le nom des monstres que l'outil aura reconnu.</b> Si la ligne est rouge, il faudra soit corriger le nom du monstre," +
                        " ou bien laisser tel quel car il s'agit d'un monstre non concerné par la quête.<br>" +
                        "<b>1c - Cliquez sur valider, cela ajoutera directement la liste sur metamob :)</b><br><br>" +
                        "<b>2 - En cochant <span style='color:#87CEEB;'>\"Utiliser le dernier rectangle\"</span>, le rectangle de la dernière capture sera automatiquement utilisé.</b>" +
                        " Je le recommande si vous ajoutez vos PDAs à la chaîne ! Le fonctionnement de l'outil est tel que vous capturez une zone de votre écran : " +
                        "Si la visualisation de la pierre se déplace, alors vous ne capturerez plus correctement la liste des noms. <br><b>Il faudra alors cliquer sur \"Reset rectangle\"</b> et définir une nouvelle zone." +
                        "</html>"
        );
    }
}
