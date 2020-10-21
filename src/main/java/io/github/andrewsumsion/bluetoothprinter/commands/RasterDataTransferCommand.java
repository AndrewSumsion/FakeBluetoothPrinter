package io.github.andrewsumsion.bluetoothprinter.commands;

import io.github.andrewsumsion.bluetoothprinter.FakeBluetoothPrinter;
import io.github.andrewsumsion.bluetoothprinter.PrintingMode;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RasterDataTransferCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command) {
        if(FakeBluetoothPrinter.data.getPrintingMode() != PrintingMode.RASTER) {
            return false;
        }
        return command[0] == 98 ||
                command[0] == 107;
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out) throws IOException {
        int n1 = command[1];
        int n2 = command[2];
        int k = n1 + n2 * 256;

        byte[] data = new byte[k];
        for(int i = 0; i < k; i++) {
            data[i] = (byte) in.read();
        }
//        FakeBluetoothPrinter.data.addRasterData(dataToImage(data));
        FakeBluetoothPrinter.data.getRasterData().put(FakeBluetoothPrinter.data.getRasterPointer(), dataToImage(data));
    }

    private BufferedImage dataToImage(byte[] data) {
        int finalWidth = data.length * 8;
        int finalHeight = 1;
        System.out.println("Width:  " + finalWidth);
        System.out.println("Height: " + finalHeight);
        BufferedImage finalImage = new BufferedImage(finalWidth, 48, BufferedImage.TYPE_INT_ARGB);
        int x = 0;
        int y = 0;
        for(int i = 0; i < data.length; i++) {
            Color transparent = new Color(0, 0, 0, 0);
            finalImage.setRGB(x + 0, y, (((data[i] & 128) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 1, y, (((data[i] &  64) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 2, y, (((data[i] &  32) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 3, y, (((data[i] &  16) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 4, y, (((data[i] &   8) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 5, y, (((data[i] &   4) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 6, y, (((data[i] &   2) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 7, y, (((data[i] &   1) != 0) ? Color.BLACK : transparent).getRGB());

            x += 8;
            if(x >= finalWidth) {
                x = 0;
                y += 1;
            }
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
