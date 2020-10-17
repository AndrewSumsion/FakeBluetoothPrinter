package io.github.andrewsumsion.bluetoothprinter.commands;

import io.github.andrewsumsion.bluetoothprinter.CharacterSet;
import io.github.andrewsumsion.bluetoothprinter.FakeBluetoothPrinter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CharsetCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command) {
        return command[0] == 27 &&
                command[1] == 82;
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out) throws IOException {
        if(command[2] == 64) {
            FakeBluetoothPrinter.data.setCharset(CharacterSet.LEGAL);
        } else {
            FakeBluetoothPrinter.data.setCharset(CharacterSet.values()[command[2]]);
        }
    }

    @Override
    public String getDescription() {
        return "Set charset";
    }

    @Override
    public int getLength() {
        return 3;
    }

    @Override
    public int getMatchingLength() {
        return 2;
    }
}
