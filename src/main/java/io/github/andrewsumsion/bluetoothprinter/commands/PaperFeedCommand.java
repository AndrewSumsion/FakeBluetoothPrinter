package io.github.andrewsumsion.bluetoothprinter.commands;

import io.github.andrewsumsion.bluetoothprinter.FakeBluetoothPrinter;
import io.github.andrewsumsion.bluetoothprinter.PrintingMode;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PaperFeedCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command) {
        return command[0] == 27 &&
                command[1] == 73;
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out) throws IOException {
        if(FakeBluetoothPrinter.data.getPrintingMode() == PrintingMode.RASTER) {
//            BufferedImage image = new BufferedImage(1, command[2], BufferedImage.TYPE_INT_ARGB);
//            Graphics2D g2 = image.createGraphics();
//            g2.setPaint(new Color(255, 255, 255));
//            g2.fillRect(0, 0, image.getWidth(), image.getHeight());
//            FakeBluetoothPrinter.data.getRasterData().add(image);
            FakeBluetoothPrinter.data.setRasterPointer(FakeBluetoothPrinter.data.getRasterPointer() + command[2]);
        }
    }

    @Override
    public String getDescription() {
        return "Paper Feed";
    }

    @Override
    public int getLength() {
        return 3;
    }

    @Override
    public int getMatchingLength() {
        return 2;
    }
}
