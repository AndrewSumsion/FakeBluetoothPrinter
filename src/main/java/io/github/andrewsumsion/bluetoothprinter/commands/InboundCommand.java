package io.github.andrewsumsion.bluetoothprinter.commands;

import io.github.andrewsumsion.bluetoothprinter.PrinterData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface InboundCommand {
    boolean matches(byte[] command, PrinterData printerData);
    void execute(byte[] command, DataInputStream in, DataOutputStream out, PrinterData printerData) throws IOException;
    String getDescription();
    int getLength();
    int getMatchingLength();
}
