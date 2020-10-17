package io.github.andrewsumsion.bluetoothprinter.commands;

import io.github.andrewsumsion.bluetoothprinter.FakeBluetoothPrinter;
import io.github.andrewsumsion.bluetoothprinter.PrinterData;
import io.github.andrewsumsion.bluetoothprinter.PrintingMode;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FineRasterImageCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command) {
        return command[0] == 27 &&
                command[1] == 88;
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out) throws IOException {
        FakeBluetoothPrinter.data.setPrintingMode(PrintingMode.RASTER);
        int n1 = command[2] & 0xFF;
        int n2 = command[3] & 0xFF;
        int k = (n2 * 256 + n1) * 3;
        byte[] rawData = new byte[k];
        for(int i = 0; i < k; i++) {
            rawData[i] = (byte) in.read();
        }
        FakeBluetoothPrinter.data.addRasterData(getData(rawData, (n2 * 256 + n1)));
    }

    private BufferedImage getData(byte[] rawData, int width) {
        int height = 24;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for(int x = 0; x < width; x++) {
            int b1 = rawData[x * 3];
            int b2 = rawData[x * 3 + 1];
            int b3 = rawData[x * 3 + 2];
            drawByte(image, x, 0, b1);
            drawByte(image, x, 8, b2);
            drawByte(image, x, 16, b3);
        }

        return image;
    }
    private void drawByte(BufferedImage image, int x, int y, int bite) {
        Color transparent = new Color(0, 0, 0, 0);

        image.setRGB(x, y + 0, (((bite & 128) != 0) ? Color.BLACK : transparent).getRGB());
        image.setRGB(x, y + 1, (((bite &  64) != 0) ? Color.BLACK : transparent).getRGB());
        image.setRGB(x, y + 2, (((bite &  32) != 0) ? Color.BLACK : transparent).getRGB());
        image.setRGB(x, y + 3, (((bite &  16) != 0) ? Color.BLACK : transparent).getRGB());
        image.setRGB(x, y + 4, (((bite &   8) != 0) ? Color.BLACK : transparent).getRGB());
        image.setRGB(x, y + 5, (((bite &   4) != 0) ? Color.BLACK : transparent).getRGB());
        image.setRGB(x, y + 6, (((bite &   2) != 0) ? Color.BLACK : transparent).getRGB());
        image.setRGB(x, y + 7, (((bite &   1) != 0) ? Color.BLACK : transparent).getRGB());

    }

    @Override
    public String getDescription() {
        return "Fine Raster Image Data";
    }

    @Override
    public int getLength() {
        return 2;
    }

    @Override
    public int getMatchingLength() {
        return 4;
    }
}
