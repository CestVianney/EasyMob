package com.easymob.ocr;

import com.easymob.bdd.BddCrud;
import com.easymob.front.itfc.ScreenCaptureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;

import static com.easymob.bdd.BddCrud.getDimensionRectangle;
import static com.easymob.ocr.TesseractScan.extractMonstres;

public class ScreenCapture {
    private Rectangle captureRect;
    private Point point1;
    private ScreenCaptureListener listener;
    private Rectangle lastSelectedRect = null;
    boolean isLastRectangleCoche = false;
    private static final Logger logger = LoggerFactory.getLogger(ScreenCapture.class);



    public ScreenCapture(boolean isLastRectangleCoche) throws AWTException {
        this.isLastRectangleCoche = isLastRectangleCoche;
    }

    public void setScreenCaptureListener(ScreenCaptureListener listener) {
        this.listener = listener;
    }

    public List<String> captureAndExtractMonstres() throws AWTException {
        final Robot robot = new Robot();
        final JFrame frame = new JFrame();
        final AtomicReference<List<String>> listeMonstres = new AtomicReference<>();

        BufferedImage screenCapture = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

        JLabel label = new JLabel(new ImageIcon(screenCapture));
        frame.add(label);
        frame.setAlwaysOnTop(true);
        JPanel selectionPanel = new JPanel();
        selectionPanel.setBackground(new Color(0, 0, 0, 0));
        label.add(selectionPanel);
        String rectString = getDimensionRectangle();
        boolean isRectangleSetInBdd = rectString != null && !rectString.isEmpty();
        if(isLastRectangleCoche && isRectangleSetInBdd) {
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    autoCaptureAndClose(frame, screenCapture, rectString);
                }
            });
        }
        label.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (lastSelectedRect == null) {
                    point1 = e.getPoint();
                    selectionPanel.setBounds(point1.x, point1.y, 1, 1);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point point2 = e.getPoint();
                captureRect = new Rectangle(point1, new Dimension(point2.x - point1.x, point2.y - point1.y));
                if (controlRectangleSize(frame)) return;
                String rectString = captureRect.x + "," + captureRect.y + "," + captureRect.width + "," + captureRect.height;
                BddCrud.updateSettings("rectangle", rectString);
                savePortionOfScreen(screenCapture);
                try {
                    listeMonstres.set(extractMonstres());
                } catch (Exception err) {
                    System.out.println("--------------Erreur lors de l'extraction des monstres : " + err.getMessage());
                    err.printStackTrace();
                }
                frame.dispose();
                if (listener != null) {
                    listener.onCaptureCompleted(listeMonstres.get());
                }
            }
        });

        label.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastSelectedRect == null) {
                    Point point2 = e.getPoint();
                    selectionPanel.setBounds(Math.min(point1.x, point2.x), Math.min(point1.y, point2.y),
                            Math.abs(point1.x - point2.x), Math.abs(point1.y - point2.y));
                    selectionPanel.setBackground(new Color(0, 0, 255, 50));
                    label.repaint();
                }
            }
        });

        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return listeMonstres.get();
    }

    private void autoCaptureAndClose(JFrame frame, BufferedImage screenCapture, String rectString) {
        String[] rectValues = rectString.split(",");
        if (rectValues.length != 4) {
            return;
        }
        try {
            captureRect = new Rectangle(Integer.parseInt(rectValues[0]), Integer.parseInt(rectValues[1]), Integer.parseInt(rectValues[2]), Integer.parseInt(rectValues[3]));
        } catch (NumberFormatException e) {
            return;
        }
        if (captureRect.width <= 0 || captureRect.height <= 0) {
            return;
        }
        savePortionOfScreen(screenCapture);
        frame.dispose();
        if (listener != null) {
            listener.onCaptureCompleted(extractMonstres());
        }
    }

    private void savePortionOfScreen(BufferedImage screenCapture) {
        try {
            String jarPath = new File(ScreenCapture.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            Path imagePath = Paths.get(jarPath, "img");
            if (!Files.exists(imagePath)) {
                Files.createDirectories(imagePath);
            }
            Path imageFile = imagePath.resolve("capture.png");
            BufferedImage capture = screenCapture.getSubimage(captureRect.x, captureRect.y, captureRect.width, captureRect.height);
            ImageIO.write(capture, "png", imageFile.toFile());
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    private boolean controlRectangleSize(JFrame frame) {
        if (captureRect.width == 0 || captureRect.height == 0) {
            frame.dispose();
            return true;
        }
        return false;
    }
}