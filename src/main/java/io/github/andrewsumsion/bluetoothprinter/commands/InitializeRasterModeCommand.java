package io.github.andrewsumsion.bluetoothprinter.commands;

import io.github.andrewsumsion.bluetoothprinter.FakeBluetoothPrinter;
import io.github.andrewsumsion.bluetoothprinter.PrintingMode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class InitializeRasterModeCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command) {
        return command[0] == 27 &&
                command[1] == 42 &&
                command[2] == 114 &&
                command[3] == 82;
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out) throws IOException {
        FakeBluetoothPrinter.data.setPrintingMode(PrintingMode.RASTER);
    }

    @Override
    public String getDescription() {
        return "Initialize Raster Mode";
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
