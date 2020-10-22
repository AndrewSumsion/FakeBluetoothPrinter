package io.github.andrewsumsion.bluetoothprinter.commands;

import io.github.andrewsumsion.bluetoothprinter.FakeBluetoothPrinter;
import io.github.andrewsumsion.bluetoothprinter.PrinterData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UpdateETBStatusCommand implements InboundCommand {
    @Override
    public boolean matches(byte[] command, PrinterData printerData) {
        return command[0] == 23;
    }

    @Override
    public void execute(byte[] command, DataInputStream in, DataOutputStream out, PrinterData printerData) throws IOException {
//        if(printerData.getPrintingMode() == PrintingMode.RASTER) {
//            return;
//        }
        printerData.getStatus().etbExecuted = true;
        printerData.getStatus().etbCounter += 1;
        printerData.getStatus().paperPresent = false;

        printerData.flagFinishedJob();
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
