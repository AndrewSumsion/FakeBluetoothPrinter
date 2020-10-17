package io.github.andrewsumsion.bluetoothprinter;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

public class OCRPrintingJob extends PrintingJob {
    private BufferedImage image;
    private String dataFolder;

    protected OCRPrintingJob(BufferedImage image, String dataFolder) {
        super(new byte[0]);
        this.image = image;
        this.dataFolder = dataFolder;
    }

    @Override
    public String getData() {
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath(dataFolder);
        //tesseract.setLanguage("eng");
        try {
            return tesseract.doOCR(image);
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] getRawData() {
        throw new UnsupportedOperationException();
    }
}
