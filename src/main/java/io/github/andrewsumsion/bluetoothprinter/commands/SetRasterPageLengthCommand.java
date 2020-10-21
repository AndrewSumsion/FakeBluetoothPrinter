package io.github.andrewsumsion.bluetoothprinter.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SetRasterPageLengthCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command) {
        return command[0] == 27 &&
                command[1] == 42 &&
                command[2] == 114 &&
                command[3] == 80;
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out) throws IOException {

    }

    @Override
    public String getDescription() {
        return "Set Raster Page Length";
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
