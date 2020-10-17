package io.github.andrewsumsion.bluetoothprinter.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CharacterSpacingCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command) {
        return command[0] == 27 &&
                command[1] == 32;
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out) throws IOException {

    }

    @Override
    public String getDescription() {
        return "Set character spacing";
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
