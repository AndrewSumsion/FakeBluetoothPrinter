package io.github.andrewsumsion.bluetoothprinter.commands;

import io.github.andrewsumsion.bluetoothprinter.FakeBluetoothPrinter;
import io.github.andrewsumsion.bluetoothprinter.PrinterData;
import io.github.andrewsumsion.bluetoothprinter.PrintingMode;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RasterDataTransferCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command, PrinterData printerData) {
        if(printerData.getPrintingMode() != PrintingMode.RASTER) {
            return false;
        }
        return command[0] == 98 ||
                command[0] == 107;
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out, PrinterData printerData) throws IOException {
        int n1 = command[1];
        int n2 = command[2];
        int k = n1 + n2 * 256;

        byte[] data = new byte[k];
        for(int i = 0; i < k; i++) {
            data[i] = (byte) in.read();
        }
        printerData.getRasterData().put(printerData.getRasterPointer(), dataToImage(data));
        printerData.setRasterPointer(printerData.getRasterPointer() + 1);
    }

    private BufferedImage dataToImage(byte[] data) {
        int finalWidth = data.length * 8;
        BufferedImage finalImage = new BufferedImage(finalWidth, 1, BufferedImage.TYPE_INT_ARGB);
        int x = 0;
        for(int i = 0; i < data.length; i++) {
            Color transparent = new Color(0, 0, 0, 0);
            finalImage.setRGB(x + 0, 0, (((data[i] & 128) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 1, 0, (((data[i] &  64) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 2, 0, (((data[i] &  32) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 3, 0, (((data[i] &  16) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 4, 0, (((data[i] &   8) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 5, 0, (((data[i] &   4) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 6, 0, (((data[i] &   2) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 7, 0, (((data[i] &   1) != 0) ? Color.BLACK : transparent).getRGB());

            x += 8;
        }
        return finalImage;
    }

    @Override
    public String getDescription() {
        return "Raster Data Transfer";
    }

    @Override
    public int getLength() {
        return 3;
    }

    @Override
    public int getMatchingLength() {
        return 1;
    }
}
