package io.github.andrewsumsion.bluetoothprinter;

import io.github.andrewsumsion.bluetoothprinter.commands.InboundCommand;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Connection {
    private static final List<Integer> headerCodes = Arrays.asList(
            23,
            27,
            98,
            107
    );

    private DataInputStream in;
    private DataOutputStream out;
    private PrinterData data;

    public Connection(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
        this.data = new PrinterData();
    }

    public Connection(InputStream in, OutputStream out) {
        this(new DataInputStream(in), new DataOutputStream(out));
    }

    public PrinterData getData() {
        return data;
    }

    public void runConnection() throws IOException {
        ByteArrayOutputStream printData = new ByteArrayOutputStream();

        List<Byte> buffer = new ArrayList<>();
        while (true) {

            int inByteAsInt = in.read();
            byte inByte = (byte) inByteAsInt;
            if(inByteAsInt == -1) {
                break;
            }

            if ((headerCodes.contains((int) inByte) /*|| (rasterHeaderCodes.contains((int) inByte) && data.getPrintingMode() == PrintingMode.RASTER)*/) && buffer.size() > 0) {
                if (FakeBluetoothPrinter.debugInput) {
                    System.out.println("\nUnknown Command Received");
                }
                printData.write(createByteBuffer(buffer));
                buffer.clear();
            }

            buffer.add(inByte);

            for (InboundCommand command : FakeBluetoothPrinter.commands) {
                if (buffer.size() == command.getMatchingLength() && command.matches(createByteBuffer(buffer), data)) {
                    while (buffer.size() < command.getLength()) {
                        buffer.add((byte) in.read());
                    }
                    command.execute(createByteBuffer(buffer), in, out, data);
                    if (FakeBluetoothPrinter.debugInput) {
                        System.out.println("\nReceived command: " + command.getDescription());
                    }
                    if (data.isJobFinished()) {
                        handleJob(printData.toByteArray());
                        printData.reset();
                    }
                    buffer.clear();
                    break;
                }
            }
        }

        in.close();
        out.close();

    }

    private void handleJob(byte[] printData) {
        try {
            if(data.getPrintingMode() == PrintingMode.PAGE) {
                if(printData.length == 0) {
                    return;
                }
                JobManager.getInstance().submitJob(new TextPrintingJob(printData));
            } else if(data.getPrintingMode() == PrintingMode.HYBRID) {
                if(data.getRasterData().isEmpty()) {
                    return;
                }
                RasterPrintingJob rasterJob = new RasterPrintingJob(data.getRasterData());
                OCRPrintingJob ocrJob = new OCRPrintingJob(rasterJob.getData(), FakeBluetoothPrinter.tesseractDataPath);
                JobManager.getInstance().submitJob(rasterJob);
                JobManager.getInstance().submitJob(ocrJob);
                data.getRasterData().clear();
                data.setRasterPointer(0);
            } else {
                if(data.getRasterData().isEmpty()) {
                    return;
                }
                RasterPrintingJob rasterJob = new RasterPrintingJob(data.getRasterData());
                OCRPrintingJob ocrJob = new OCRPrintingJob(rasterJob.getData(), FakeBluetoothPrinter.tesseractDataPath);
                JobManager.getInstance().submitJob(rasterJob);
                JobManager.getInstance().submitJob(ocrJob);
                data.getRasterData().clear();
                data.setRasterPointer(0);
            }
        } catch (InterruptedException ignored) {

        }
    }

    private byte[] createByteBuffer(List<Byte> bytes) {
        byte[] result = new byte[bytes.size()];
        int i = 0;
        for(Byte b : bytes) {
            result[i] = b;
            i++;
        }
        return result;
    }

    public void close() throws IOException {
        in.close();
        out.close();
    }
}
