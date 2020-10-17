package io.github.andrewsumsion.bluetoothprinter.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface InboundCommand {
    boolean matches(byte[] command);
    void execute(byte[] command, DataInputStream in, DataOutputStream out) throws IOException;
    String getDescription();
    int getLength();
    int getMatchingLength();
}
