package io.github.andrewsumsion.bluetoothprinter.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RequestModelInfoCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command) {
        return command[0] == 27 &&
                command[1] == 35 &&
                command[2] == 42;
    }

    // a1 = [27, 35, 42, 44]
    // a2 = [10, 0]
    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out) throws IOException {
        out.write(new byte[] {27, 35, 42, 44, 10, 0});
    }

    @Override
    public String getDescription() {
        return "No idea what this is";
    }

    @Override
    public int getLength() {
        return 5;
    }

    @Override
    public int getMatchingLength() {
        return 3;
    }

    public static void main(String[] args) {
        byte[] array1 = new byte[] {27, 35, 42, 44};
        byte[] array2 = new byte[] {10, 0};
    }

    static byte[] a(byte[] readBytes, byte[] array1, byte[] array2) { // TODO this is important
        byte[] array1copy = new byte[array1.length]; // 4
        byte[] array2copy = new byte[array2.length]; // 2
        int i = 0;
        while (true) {
            if (i < readBytes.length - array1.length + 1) {
                System.arraycopy(readBytes, i, array1copy, 0, array1.length);
                if (!java.util.Arrays.equals(array1copy, array1)) {
                    i++;
                    continue;
                }
            } else {
                i = -1;
            }
            int j = i + array1.length; // i + 4
            while (true) {
                if (j < readBytes.length - array2.length + 1) {
                    System.arraycopy(readBytes, j, array2copy, 0, array2.length);
                    if (!java.util.Arrays.equals(array2copy, array2)) {
                        j++;
                        continue;
                    }
                } else {
                    j = -1;
                }
                if (i >= 0 && j >= 0) {
                    int i1 = j - i;
                    byte[] a5 = new byte[i1 - array1.length];
                    System.arraycopy(readBytes, i + array1.length, a5, 0, i1 - array1.length);
                    return a5;
                }
                return null;
            }
        }
    }
}
