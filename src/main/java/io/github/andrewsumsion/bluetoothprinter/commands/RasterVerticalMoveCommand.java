package io.github.andrewsumsion.bluetoothprinter.commands;

import io.github.andrewsumsion.bluetoothprinter.FakeBluetoothPrinter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RasterVerticalMoveCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command) {
        //27 42 114 89
        return command[0] == 27 &&
                command[1] == 42 &&
                command[2] == 114 &&
                command[3] == 89;
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out) throws IOException {
        FakeBluetoothPrinter.data.setRasterPointer(FakeBluetoothPrinter.data.getRasterPointer() + command[4]);
    }

    @Override
    public String getDescription() {
        return "Raster Vertical Move";
    }

    @Override
    public int getLength() {
        return 6;
    }

    @Override
    public int getMatchingLength() {
        return 4;
    }
}
