package io.github.andrewsumsion.bluetoothprinter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class TestRasterHandler implements JobHandler {
    @Override
    public void handle(PrintingJob job) {
        if(!(job instanceof RasterPrintingJob)) {
            return;
        }
        RasterPrintingJob rasterPrintingJob = (RasterPrintingJob) job;
        try {
//            int i = 0;
//            for(Map.Entry<Integer, BufferedImage> entry : FakeBluetoothPrinter.data.getRasterData().entrySet()) {
//                ImageIO.write(entry.getValue(), "png", new File("/home/andrew/Desktop/raster/raster"+entry.getKey()+".png"));
//                i += 1;
//            }
            ImageIO.write(rasterPrintingJob.getData(), "png", new File(System.getProperty("user.home") + "/Desktop/raster.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
