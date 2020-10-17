package io.github.andrewsumsion.bluetoothprinter.commands;

import io.github.andrewsumsion.bluetoothprinter.FakeBluetoothPrinter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class InitializeETBCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command) {
        return command[0] == 27 &&
                command[1] == 30 &&
                command[2] == 69;
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out) throws IOException {
        FakeBluetoothPrinter.status.etbCounter = 0;
    }

    @Override
    public String getDescription() {
        return "Reset ETB counter to zero";
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
