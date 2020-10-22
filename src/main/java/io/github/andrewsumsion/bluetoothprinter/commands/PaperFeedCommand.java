package io.github.andrewsumsion.bluetoothprinter.commands;

import io.github.andrewsumsion.bluetoothprinter.PrinterData;
import io.github.andrewsumsion.bluetoothprinter.PrintingMode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PaperFeedCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command, PrinterData printerData) {
        return command[0] == 27 &&
                command[1] == 73;
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out, PrinterData printerData) throws IOException {
        if(printerData.getPrintingMode() == PrintingMode.HYBRID) {
//            BufferedImage image = new BufferedImage(1, command[2], BufferedImage.TYPE_INT_ARGB);
//            Graphics2D g2 = image.createGraphics();
//            g2.setPaint(new Color(255, 255, 255));
//            g2.fillRect(0, 0, image.getWidth(), image.getHeight());
//            printerData.getRasterData().add(image);
            printerData.setRasterPointer(printerData.getRasterPointer() + command[2]);
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
