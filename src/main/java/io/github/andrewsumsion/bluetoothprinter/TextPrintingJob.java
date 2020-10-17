package io.github.andrewsumsion.bluetoothprinter;

import java.nio.charset.StandardCharsets;

public class TextPrintingJob extends PrintingJob {

    public TextPrintingJob(byte[] rawData) {
        super(rawData);
    }

    @Override
    public String getData() {
        return new String(rawData, StandardCharsets.UTF_8);
    }
}
