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
    private String result;

    protected OCRPrintingJob(BufferedImage image, String dataFolder) {
        super(new byte[0]);
        this.image = image;
        this.dataFolder = dataFolder;
    }

    @Override
    public String getData() {
        if(result != null) {
            return result;
        }
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath(dataFolder);
        //tesseract.setLanguage("eng");
        try {
            result = tesseract.doOCR(image);
            return result;
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] getRawData() {
        throw new UnsupportedOperationException();
    }
}
