package io.github.andrewsumsion.bluetoothprinter.commands;

import io.github.andrewsumsion.bluetoothprinter.FakeBluetoothPrinter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RetrieveStatusCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command) {
        return command[0] == 27
                && command[1] == 6
                && command[2] == 1;
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out) throws IOException {
        byte[] data = FakeBluetoothPrinter.status.toRawData();
        out.write(data);
    }

    @Override
    public String getDescription() {
        return "Status Requested";
    }

    @Override
    public int getLength() {
        return 3;
    }

    @Override
    public int getMatchingLength() {
        return 3;
    }
}
