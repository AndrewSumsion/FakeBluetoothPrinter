package io.github.andrewsumsion.bluetoothprinter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrinterData {
    private PrintingMode printingMode;
    private CharacterSet charset;
    private int codePage;
    private int rasterWidth = 1000;
    private int rasterPointer = 0;
    private Map<Integer, BufferedImage> rasterData = new HashMap<>();

    public PrintingMode getPrintingMode() {
        return printingMode;
    }

    public CharacterSet getCharset() {
        return charset;
    }

    public int getCodePage() {
        return codePage;
    }

    public void setRasterWidth(int rasterWidth) {
        this.rasterWidth = rasterWidth;
    }

    public Map<Integer, BufferedImage> getRasterData() {
        return rasterData;
    }

    public void setPrintingMode(PrintingMode printingMode) {
        this.printingMode = printingMode;
    }

    public void setCharset(CharacterSet charset) {
        this.charset = charset;
    }

    public void setCodePage(int codePage) {
        this.codePage = codePage;
    }

    public void setRasterPointer(int rasterPointer) {
        this.rasterPointer = rasterPointer;
    }

    public int getRasterWidth() {
        return rasterWidth;
    }

    public void setRasterData(Map<Integer, BufferedImage> rasterData) {
        this.rasterData = rasterData;
    }

    public int getRasterPointer() {
        return rasterPointer;
    }

    public void addRasterData(BufferedImage image) {
        rasterData.put(rasterPointer, image);
    }
}
