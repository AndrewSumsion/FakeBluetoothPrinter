package io.github.andrewsumsion.bluetoothprinter;

import com.sun.tools.javac.util.StringUtils;
import io.github.andrewsumsion.bluetoothprinter.commands.*;
import io.github.andrewsumsion.threepos.Plugin;
import org.apache.commons.io.FileUtils;

import javax.bluetooth.*;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FakeBluetoothPrinter extends Plugin {
    public static boolean debugInput = false;
    public static boolean debugOutput = false;

    public static PrinterStatus status = new PrinterStatus();
    public static PrinterData data = new PrinterData();
    public static PrintingMode printingMode = PrintingMode.PAGE;

    public static final String MODEL_NAME = "FakeBluetoothPrinterVer1.0.0";
    private static String tesseractDataPath = "";
    private static final Set<InboundCommand> commands = new HashSet<>();
    private static final List<Integer> headerCodes = Arrays.asList(
            4,
            5,
            7,
            9,
            11,
            12,
            13,
            14,
            15,
            17,
            18,
            19,
            20,
            23,
            24,
            25,
            26,
            27,
            28
//            30
    );

    private Thread jobManagerThread;

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
            //zipFile.delete();
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
        LocalDevice local = LocalDevice.getLocalDevice();
        System.out.println("Device name: " + local.getFriendlyName());
        System.out.println("Bluetooth Address: " +
                local.getBluetoothAddress());
        boolean res = local.setDiscoverable(DiscoveryAgent.GIAC);
        System.out.println("Discoverability set: " + res);

        //Create a UUID for SPP
//        UUID uuid = new UUID("1103", true);
        UUID uuid = new UUID("0000110100001000800000805F9B34FB", false);
        //Create the service url
        String connectionString = "btspp://localhost:" + uuid +";name=FakeBluetoothPrinter";

        //open server url
        StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier) Connector.open( connectionString );

        //Wait for client connection
        System.out.println("\nServer Started. Waiting for clients to connect...");

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

        while (true) {
            if(Thread.currentThread().isInterrupted()) {
                jobManagerThread.interrupt();
                break;
            }

            final StreamConnection connection = streamConnNotifier.acceptAndOpen();
            if(debugInput) {
                System.out.println("Client connected!");
            }

            try {
                runConnection(connection);
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
    }

    private void runConnection(StreamConnection connection) throws IOException {
        RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
        if(debugInput) {
            System.out.println("Remote device address: " + dev.getBluetoothAddress());
            System.out.println("Remote device name: " + dev.getFriendlyName(true));
        }
        DataInputStream in = new DataInputStream(new WrappedInputStream(connection.openInputStream()));
        DataOutputStream out = new DataOutputStream(new WrappedOutputStream(connection.openOutputStream()));

        ByteArrayOutputStream printData = new ByteArrayOutputStream();

        List<Byte> buffer = new ArrayList<>();
        try {
            while (true) {
                int inByteAsInt = in.read();
                byte inByte = (byte) inByteAsInt;
                if(inByteAsInt == -1) {
                    break;
                }

                if (headerCodes.contains((int) inByte) && buffer.size() > 0) {
                    printData.write(createByteBuffer(buffer));
                    buffer.clear();
                }

                buffer.add(inByte);


                for (InboundCommand command : commands) {
                    if (buffer.size() == command.getMatchingLength() && command.matches(createByteBuffer(buffer))) {
                        while (buffer.size() < command.getLength()) {
                            buffer.add((byte) in.read());
                        }
                        command.execute(createByteBuffer(buffer), in, out);
                        if (debugInput) {
                            System.out.println("\nReceived command: " + command.getDescription());
                        }
                        buffer.clear();
                        break;
                    }
                }
            }
        } catch (IOException e) {

        }



        try {
            if(FakeBluetoothPrinter.data.getPrintingMode() == PrintingMode.PAGE) {
                JobManager.getInstance().submitJob(new TextPrintingJob(printData.toByteArray()));
            } else {
                RasterPrintingJob rasterJob = new RasterPrintingJob(FakeBluetoothPrinter.data.getRasterData());
                OCRPrintingJob ocrJob = new OCRPrintingJob(rasterJob.getData(), tesseractDataPath);
                JobManager.getInstance().submitJob(rasterJob);
                JobManager.getInstance().submitJob(ocrJob);
                FakeBluetoothPrinter.data.getRasterData().clear();
                FakeBluetoothPrinter.data.setRasterPointer(0);
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

        public WrappedInputStream(InputStream wrapped) {
            this.wrapped = wrapped;
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
            return data;
        }
    }

    private static class WrappedOutputStream extends OutputStream {
        private final OutputStream wrapped;

        public WrappedOutputStream(OutputStream wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public void write(int i) throws IOException {
            if(debugOutput) {
                System.out.print("" + i + " ");
            }
            wrapped.write(i);
        }
    }
}
