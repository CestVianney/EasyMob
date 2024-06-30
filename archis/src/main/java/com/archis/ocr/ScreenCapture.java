package com.archis.ocr;

import com.archis.front.itfc.ScreenCaptureListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;

import static com.archis.ocr.TesseractScan.extractMonstres;

public class ScreenCapture {
    private Rectangle captureRect;
    private Point point1;
    private ScreenCaptureListener listener;
    private Rectangle lastSelectedRect= null;


    public ScreenCapture() throws AWTException {
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
                if (lastSelectedRect == null) {
                    Point point2 = e.getPoint();
                    captureRect = new Rectangle(point1, new Dimension(point2.x-point1.x, point2.y-point1.y));
                } else {
                    captureRect = lastSelectedRect;
                }
                if (controlRectangleSize(frame)) return;
                savePortionOfScreen(screenCapture);
                listeMonstres.set(extractMonstres());
                frame.dispose();
                if (listener != null) {
                    listener.onCaptureCompleted(listeMonstres.get());
                }
                lastSelectedRect = captureRect;
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

    private void savePortionOfScreen(BufferedImage screenCapture) {
        try {
            Path imagePath = Paths.get(getClass().getResource("/img/capture.png").toURI());
            BufferedImage capture = screenCapture.getSubimage(captureRect.x, captureRect.y, captureRect.width, captureRect.height);
            ImageIO.write(capture, "png", imagePath.toFile());
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