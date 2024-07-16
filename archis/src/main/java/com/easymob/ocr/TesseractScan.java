package com.easymob.ocr;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TesseractScan {
    Tesseract tesseract = new Tesseract();

    public static List<String> extractMonstres() {
        try {
            TesseractScan tesseractScan = new TesseractScan();
            String result = tesseractScan.scanImage();
            List<String> resultLines = List.of(result.split("\n"));
            resultLines = applicationRegex(resultLines);
            resultLines = retirerNiveaux(resultLines);
            resultLines.removeIf(String::isEmpty);
            return resultLines;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<String> applicationRegex(List<String> resultLines) {
        String regex = ".*\\b[\\p{L}\\d\\s!]+\\b\\s\\(\\d+\\)$";
        return resultLines.stream()
                .map(line -> line.replace("!", "l"))
                .filter(line -> line.matches(regex))
                .collect(Collectors.toList());
    }

    private static List<String> retirerNiveaux(List<String> lines) {
        return lines.stream()
                .map(line -> line.split(" "))
                .map(words -> Arrays.copyOfRange(words, 0, words.length - 1))
                .map(words -> String.join(" ", words))
                .collect(Collectors.toList());
    }

    private String scanImage() {
        File imageFile = setupAndGetImageFile();
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

    private File setupAndGetImageFile() {
        File preprocessedImage = null;
        try {
            setupTesseractAndOpenCV();
            String jarPath = new File(TesseractScan.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();

            Path imagePath = Paths.get(jarPath, "img");
            File imageFile = imagePath.resolve("capture.png").toFile();

            if (!imageFile.exists()) {
                throw new RuntimeException("Image file does not exist: " + imageFile.getAbsolutePath());
            }

            Path newFilePath = imagePath.resolve("capture.png");
            preprocessedImage = newFilePath.toFile();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return preprocessedImage;
    }

    private void setupTesseractAndOpenCV() {
        try {
            URL url = getClass().getResource("/tessdata");
            if (url == null) {
                throw new FileNotFoundException("Resource not found: /tessdata");
            }
            Path tempDir = Files.createTempDirectory("tessdata");
            copyResourceToDirectory(url, tempDir);

            tesseract.setDatapath(tempDir.toString());
            tesseract.setLanguage("fra");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyResourceToDirectory(URL resourceURL, Path targetDir) throws IOException {
        try (FileSystem fileSystem = FileSystems.newFileSystem(resourceURL.toURI(), Collections.emptyMap())) {
            Path pathInsideJar = fileSystem.getPath("/tessdata");
            Files.walk(pathInsideJar).forEach(path -> {
                Path destPath = targetDir.resolve(pathInsideJar.relativize(path).toString());
                try {
                    if (Files.isDirectory(path)) {
                        if (!Files.exists(destPath)) {
                            Files.createDirectories(destPath);
                        }
                    } else {
                        Files.copy(path, destPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
//TODO : TEST EN LOCAL
//    private void setupTesseractAndOpenCV() throws URISyntaxException {
////        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        URL url = getClass().getResource("/tessdata");
//        Path path = Paths.get(url.toURI());
//        tesseract.setDatapath(path.toString());
//        tesseract.setLanguage("fra");
//    }
}
