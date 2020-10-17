package io.github.andrewsumsion.bluetoothprinter.commands;

import io.github.andrewsumsion.bluetoothprinter.FakeBluetoothPrinter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SelectCodePageCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command) {
        return command[0] == 27 &&
                command[1] == 29 &&
                command[2] == 116;
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out) throws IOException {
        FakeBluetoothPrinter.data.setCodePage(command[3]);
    }

    @Override
    public String getDescription() {
        return "Select Code Page";
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
