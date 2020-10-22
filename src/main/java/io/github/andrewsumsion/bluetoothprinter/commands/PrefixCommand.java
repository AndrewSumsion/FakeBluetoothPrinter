package io.github.andrewsumsion.bluetoothprinter.commands;

import io.github.andrewsumsion.bluetoothprinter.PrinterData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PrefixCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command, PrinterData printerData) {
        return command[0] == 27 &&
                command[1] == 64;
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out, PrinterData printerData) throws IOException {

    }

    @Override
    public String getDescription() {
        return "Command Prefix";
    }

    @Override
    public int getLength() {
        return 2;
    }

    @Override
    public int getMatchingLength() {
        return 2;
    }
}
