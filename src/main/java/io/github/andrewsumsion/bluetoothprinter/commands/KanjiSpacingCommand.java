package io.github.andrewsumsion.bluetoothprinter.commands;

import io.github.andrewsumsion.bluetoothprinter.PrinterData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class KanjiSpacingCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command, PrinterData printerData) {
        return command[0] == 27 &&
                (command[1] == 115 || command[1] == 116);
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out, PrinterData printerData) throws IOException {

    }

    @Override
    public String getDescription() {
        return "Set Kanji Spacing";
    }

    @Override
    public int getLength() {
        return 4;
    }

    @Override
    public int getMatchingLength() {
        return 2;
    }
}
