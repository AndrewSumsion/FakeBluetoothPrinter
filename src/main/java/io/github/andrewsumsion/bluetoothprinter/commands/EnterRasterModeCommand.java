package io.github.andrewsumsion.bluetoothprinter.commands;

import io.github.andrewsumsion.bluetoothprinter.PrinterData;
import io.github.andrewsumsion.bluetoothprinter.PrintingMode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EnterRasterModeCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command, PrinterData printerData) {
        return command[0] == 27 &&
                command[1] == 42 &&
                command[2] == 114 &&
                command[3] == 65;
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out, PrinterData printerData) throws IOException {
        printerData.setPrintingMode(PrintingMode.RASTER);
    }

    @Override
    public String getDescription() {
        return "Enter Raster Mode";
    }

    @Override
    public int getLength() {
        return 4;
    }

    @Override
    public int getMatchingLength() {
        return 4;
    }
}
