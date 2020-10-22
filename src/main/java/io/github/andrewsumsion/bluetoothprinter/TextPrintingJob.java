package io.github.andrewsumsion.bluetoothprinter;

import java.nio.charset.StandardCharsets;

public class TextPrintingJob extends PrintingJob {
    private String data;

    public TextPrintingJob(byte[] rawData) {
        super(rawData);
    }

    @Override
    public String getData() {
        if(data == null) {
            data = new String(rawData, StandardCharsets.UTF_8).replaceAll("\\p{C}", "");
        }
        return data;
    }
}
