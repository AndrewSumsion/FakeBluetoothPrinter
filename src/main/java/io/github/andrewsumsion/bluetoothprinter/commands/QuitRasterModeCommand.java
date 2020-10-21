package io.github.andrewsumsion.bluetoothprinter.commands;

import io.github.andrewsumsion.bluetoothprinter.FakeBluetoothPrinter;
import io.github.andrewsumsion.bluetoothprinter.PrintingMode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class QuitRasterModeCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command) {
        return command[0] == 27 &&
                command[1] == 42 &&
                command[2] == 114 &&
                command[3] == 66;
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out) throws IOException {
        if(FakeBluetoothPrinter.data.getPrintingMode() != PrintingMode.RASTER) {
            FakeBluetoothPrinter.data.setPrintingMode(PrintingMode.PAGE);
        }
    }

    @Override
    public String getDescription() {
        return "Quit Raster Mode";
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