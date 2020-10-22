package io.github.andrewsumsion.bluetoothprinter;

import io.github.andrewsumsion.bluetoothprinter.commands.*;
import io.github.andrewsumsion.threepos.Plugin;

import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FakeBluetoothPrinter extends Plugin {
    public static boolean debugInput = false;
    public static boolean debugOutput = false;

    public static PrinterStatus status = new PrinterStatus();

    public static final String MODEL_NAME = "FakeBluetoothPrinterVer1.0.0";
    public static final Set<InboundCommand> commands = new HashSet<>();
    public static String tesseractDataPath = "";

    private Thread jobManagerThread;
    private Map<Connection, Thread> connectionThreads = new HashMap<>();

    public static void registerJobHandler(JobHandler handler) {
        JobManager.getInstance().registerHandler(handler);
    }

    public void run() {
        System.out.println("FakeBluetoothPrinter loaded");

        status.paperPresent = true;

        commands.add(new InitialCommand());
        commands.add(new RetrieveStatusCommand());
        commands.add(new RequestModelInfoCommand());
        commands.add(new InitializeETBCommand());
        commands.add(new QuitRasterModeCommand());
        commands.add(new UpdateETBStatusCommand());
        commands.add(new SelectCodePageCommand());
        commands.add(new PrefixCommand());
        commands.add(new KanjiSpacingCommand());
        commands.add(new CharsetCommand());
        commands.add(new CharacterSpacingCommand());
        commands.add(new PositionAlignmentCommand());
        commands.add(new PaperFeedCommand());
        commands.add(new FineRasterImageCommand());
        commands.add(new InitializeRasterModeCommand());
        commands.add(new EnterRasterModeCommand());
        commands.add(new SetRasterPageLengthCommand());
        commands.add(new CutterModeCommand());
        commands.add(new RasterVerticalMoveCommand());
        commands.add(new RasterDataTransferCommand());

        registerJobHandler(new JobHandler() {
            @Override
            public void handle(PrintingJob job) {
                if(!(job instanceof TextPrintingJob)) {
                    return;
                }
                TextPrintingJob textJob = (TextPrintingJob) job;
                System.out.println("Received text print job:");
                System.out.println(textJob.getData());
            }
        });
        registerJobHandler(new TestRasterHandler());
        registerJobHandler(new TestOCRHandler());

        try {
            setupResources();
            tesseractDataPath = new File(getPluginFolder(), "tessdata").getPath();
        } catch (IOException e) {
            System.err.println("Unable to initialize resources. OCR will not work!");
            e.printStackTrace();
        }

        jobManagerThread = new Thread() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        JobManager.getInstance().executeJob();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        };

        jobManagerThread.start();

        try {
            startRFCOMMServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupResources() throws IOException {
        File pluginFolder = getPluginFolder();
        File tessDataFolder = new File(pluginFolder, "tessdata");
        if(!tessDataFolder.exists()) {
            File zipFile = copyFileIfNotExistent("/tessdata.zip");
            unzip(zipFile);
            zipFile.delete();
        }

    }

    private static void unzip(File zipFile) {
        String destDir = zipFile.getAbsolutePath().replace(".zip", "");
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                if(ze.isDirectory()) {
                    newFile.mkdir();
                } else {
                    //create directories for sub directories in zip
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private File copyFileIfNotExistent(String fileName) {
        return copyFileIfNotExistent(fileName, new File(getPluginFolder(), fileName));
    }

    private File copyFileIfNotExistent(String fileName, File destinationFile) {
        InputStream in = FakeBluetoothPrinter.class.getResourceAsStream(fileName);

        if(!destinationFile.exists()) {
            try {
                Files.copy(in, destinationFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return destinationFile;
    }

    private void startRFCOMMServer() throws IOException {
//        File file = new File(System.getProperty("user.home") + "/Desktop//network-in.dat");
//        InputStream inputStream = new FileInputStream(file);
//        OutputStream outputStream = new OutputStream() {
//            @Override
//            public void write(int b) throws IOException {
//
//            }
//        };
//        Connection connection = new Connection(inputStream, outputStream);
//        connection.runConnection();
        LocalDevice local = LocalDevice.getLocalDevice();
        System.out.println("Device name: " + local.getFriendlyName());
        System.out.println("Bluetooth Address: " +
                local.getBluetoothAddress());

        UUID uuid = new UUID("0000110100001000800000805F9B34FB", false);
        String connectionString = "btspp://localhost:" + uuid +";name=FakeBluetoothPrinter";

        StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier) Connector.open( connectionString );

        System.out.println("\nServer Started. Waiting for clients to connect...");

        while (true) {
            if(Thread.currentThread().isInterrupted()) {
                break;
            }

            final StreamConnection bluetoothConnection = streamConnNotifier.acceptAndOpen();
            Connection connection = new Connection(bluetoothConnection.openDataInputStream(), bluetoothConnection.openDataOutputStream());
            if(debugInput) {
                System.out.println("Client connected!");
            }

            Thread connectionThread = new Thread() {
                @Override
                public void run() {
                    try {
                        connection.runConnection();
                        if(debugInput) {
                            System.out.println("\nDevice disconnected");
                        }
                    } catch (IOException ignored) {

                    } finally {
                        try {
                            connection.close();
                        } catch (IOException ignored) {

                        }
                    }
                }
            };

            connectionThreads.put(connection, connectionThread);
            connectionThread.start();
        }
    }

    private String formatModelString(String modelString) {
        if (modelString.length() > 128) {
            throw new IllegalArgumentException("Model String is too large!");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(modelString);
        while (sb.length() < 128) {
            sb.append(" ");
        }
        return sb.toString();
    }

    private static class WrappedInputStream extends InputStream {
        private final InputStream wrapped;
        private OutputStream fileOutputStream;

        public WrappedInputStream(InputStream wrapped) {
            this.wrapped = wrapped;
            File output = new File(System.getProperty("user.home") + "/Desktop/network-in.dat");
//            try {
//                fileOutputStream = new FileOutputStream(output);
//            } catch (FileNotFoundException e) {
//                fileOutputStream = new OutputStream() {
//                    @Override
//                    public void write(int i) throws IOException {
//
//                    }
//                };
//            }
            fileOutputStream = new OutputStream() {
                @Override
                public void write(int i) throws IOException {

                }
            };
        }

        @Override
        public int read() throws IOException {
            int data = wrapped.read();
            if(debugInput) {
                System.out.print("" + data + " ");
                //System.out.print((char) data);
                //String s1 = String.format("%8s", Integer.toBinaryString(data & 0xFF)).replace('0', ' ');
                //System.out.print(s1 + "");
            }
            fileOutputStream.write(data);
            return data;
        }

        @Override
        public void close() throws IOException {
            wrapped.close();
            fileOutputStream.close();
        }
    }

    private static class WrappedOutputStream extends OutputStream {
        private final OutputStream wrapped;
        private OutputStream fileOutputStream;

        public WrappedOutputStream(OutputStream wrapped) {
            this.wrapped = wrapped;
//            File output = new File(System.getProperty("user.home") + "/Desktop/network-out.dat");
//            try {
//                fileOutputStream = new FileOutputStream(output);
//            } catch (FileNotFoundException e) {
//                fileOutputStream = new OutputStream() {
//                    @Override
//                    public void write(int i) throws IOException {
//
//                    }
//                };
//            }
            fileOutputStream = new OutputStream() {
                    @Override
                    public void write(int i) throws IOException {

                    }
                };
        }

        @Override
        public void write(int i) throws IOException {
            if(debugOutput) {
                System.out.print("" + i + " ");
            }
            wrapped.write(i);
            fileOutputStream.write(i);
        }

        @Override
        public void close() throws IOException {
            wrapped.close();
            fileOutputStream.close();
        }
    }
}
