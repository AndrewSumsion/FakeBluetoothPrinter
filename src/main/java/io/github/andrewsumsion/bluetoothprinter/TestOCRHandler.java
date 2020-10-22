package io.github.andrewsumsion.bluetoothprinter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestOCRHandler implements OCRJobHandler {
    @Override
    public void handle(PrintingJob job) {
        OCRPrintingJob ocrPrintingJob = (OCRPrintingJob) job;
        System.out.println("Received OCR Print Job:");
        String data = ocrPrintingJob.getData();
        System.out.println(data);
        try {
            FileOutputStream out = new FileOutputStream(System.getProperty("user.home") + "/Desktop/ocr.txt");
            out.write(data.getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
