package io.github.andrewsumsion.bluetoothprinter;

public abstract class PrintingJob {
    protected byte[] rawData;

    protected PrintingJob(byte[] rawData) {
        this.rawData = rawData;
    }

    public byte[] getRawData() {
        return rawData;
    }
    public abstract Object getData();
}
