package io.github.andrewsumsion.bluetoothprinter;

public class TestOCRHandler implements OCRJobHandler {
    @Override
    public void handle(PrintingJob job) {
        OCRPrintingJob ocrPrintingJob = (OCRPrintingJob) job;
        System.out.println("Received OCR Print Job:");
        System.out.println(ocrPrintingJob.getData());
    }
}
