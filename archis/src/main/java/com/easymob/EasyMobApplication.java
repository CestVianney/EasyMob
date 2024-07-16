package com.easymob;

import com.easymob.bdd.BddFirstInit;
import com.easymob.front.MainScene;
import javax.swing.*;
import java.io.IOException;

public class EasyMobApplication {
	public static void main(String[] args) throws UnsupportedLookAndFeelException, IOException {
		BddFirstInit.firstInit();
		MainScene.main(args);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("Nettoyage avant la fermeture de l'application.");
			System.exit(0);
		}));
	}
}
