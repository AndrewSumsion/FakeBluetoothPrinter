package io.github.andrewsumsion.bluetoothprinter.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class KanjiSpacingCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command) {
        return command[0] == 27 &&
                (command[1] == 115 || command[1] == 116);
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out) throws IOException {

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
