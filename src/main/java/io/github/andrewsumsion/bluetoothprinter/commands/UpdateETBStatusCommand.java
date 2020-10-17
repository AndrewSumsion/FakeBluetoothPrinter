package io.github.andrewsumsion.bluetoothprinter.commands;

import io.github.andrewsumsion.bluetoothprinter.FakeBluetoothPrinter;
import io.github.andrewsumsion.bluetoothprinter.PrintingMode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UpdateETBStatusCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command) {
        return command[0] == 23;
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out) throws IOException {
//        if(FakeBluetoothPrinter.data.getPrintingMode() == PrintingMode.RASTER) {
//            return;
//        }
        FakeBluetoothPrinter.status.etbExecuted = true;
        FakeBluetoothPrinter.status.etbCounter += 1;
        FakeBluetoothPrinter.status.paperPresent = false;
    }

    @Override
    public String getDescription() {
        return "Request etb to be marked when complete";
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public int getMatchingLength() {
        return 1;
    }
}
