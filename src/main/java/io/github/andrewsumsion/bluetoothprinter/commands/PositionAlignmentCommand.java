package io.github.andrewsumsion.bluetoothprinter.commands;

import io.github.andrewsumsion.bluetoothprinter.PrinterData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PositionAlignmentCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command, PrinterData printerData) {
        return command[0] == 27 &&
                command[1] == 29 &&
                command[2] == 97;
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out, PrinterData printerData) throws IOException {

    }

    @Override
    public String getDescription() {
        return "Set Position Alignment";
    }

    @Override
    public int getLength() {
        return 4;
    }

    @Override
    public int getMatchingLength() {
        return 3;
    }
}
