package com.archis.ocr;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TesseractScan {
    Tesseract tesseract = new Tesseract();

    public static List<String> extractMonstres() {
        TesseractScan tesseractScan = new TesseractScan();
        String result = tesseractScan.scanImage();
        List<String> resultLines = List.of(result.split("\n"));
        resultLines = retirerNiveaux(resultLines);
        resultLines.forEach(System.out::println);
        return resultLines;
    }

    private static List<String> retirerNiveaux(List<String> lines) {
        return lines.stream()
                .map(line -> line.split(" "))
                .map(words -> Arrays.copyOfRange(words, 0, words.length - 1))
                .map(words -> String.join(" ", words))
                .collect(Collectors.toList());
    }

    private String scanImage() {
        File imageFile = null;
        imageFile = setupAndGetImageFile(imageFile);
        return doOcrAndExtractResult(imageFile);
    }

    private String doOcrAndExtractResult(File imageFile) {
        String result = "";
        try {
            result = tesseract.doOCR(imageFile);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return result;
    }

    private File setupAndGetImageFile(File imageFile) {
        File preprocessedImage = null;
        try {
            setupTesseractAndOpenCV();
            imageFile = getImageFile(imageFile);
            Mat image = Imgcodecs.imread(imageFile.getAbsolutePath());
            Mat grayImage = new Mat();
            Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

            Mat thresholdImage = new Mat();
            Imgproc.threshold(grayImage, thresholdImage, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

            URL url = getClass().getResource("/img/");
            Path imagePath = Paths.get(url.toURI());
            Path newFilePath = imagePath.resolve("processedcapture.png");
            preprocessedImage = newFilePath.toFile();
            Imgcodecs.imwrite(preprocessedImage.getAbsolutePath(), thresholdImage);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return preprocessedImage;
    }

    private File getImageFile(File imageFile) {
        try {
            Path imagePath = Paths.get(getClass().getResource("/img/capture.png").toURI());
            return imagePath.toFile();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URI", e);
        }
    }

    private void setupTesseractAndOpenCV() throws URISyntaxException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        URL url = getClass().getResource("/tessdata");
        Path path = Paths.get(url.toURI());
        tesseract.setDatapath(path.toString());
        tesseract.setLanguage("fra");
    }


    public static void main(String[] args) {

    }
}
