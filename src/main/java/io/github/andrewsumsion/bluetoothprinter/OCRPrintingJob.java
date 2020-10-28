package io.github.andrewsumsion.bluetoothprinter;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;

public class OCRPrintingJob extends PrintingJob {
    private BufferedImage image;
    private Preprocessor preprocessor;
    private String dataFolder;
    private String result;

    protected OCRPrintingJob(BufferedImage image, String dataFolder) {
        super(new byte[0]);
        this.image = image;
        this.dataFolder = dataFolder;
        this.preprocessor = new Preprocessor() {
            @Override
            public BufferedImage preprocess(BufferedImage image) {
                return image;
            }
        };
    }

    @Override
    public String getData() {
        if(result != null) {
            return result;
        }
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath(dataFolder);
        tesseract.setTessVariable("user_defined_dpi", "70");
        BufferedImage processedImage = preprocessor.preprocess(image);
        try {
            result = tesseract.doOCR(processedImage);
            result = result.replaceAll("[\\u2018\\u2019]", "'")
                    .replaceAll("[\\u201C\\u201D]", "\"");
            return result;
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public OCRPrintingJob clone() {
        OCRPrintingJob newJob = new OCRPrintingJob(image, dataFolder);
        newJob.setPreprocessor(preprocessor);
        return newJob;
    }

    public void setPreprocessor(Preprocessor preprocessor) {
        result = null;
        this.preprocessor = preprocessor;
    }

    @Override
    public byte[] getRawData() {
        throw new UnsupportedOperationException();
    }
}
