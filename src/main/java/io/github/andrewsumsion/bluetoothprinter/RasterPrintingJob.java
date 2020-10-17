package io.github.andrewsumsion.bluetoothprinter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RasterPrintingJob extends PrintingJob {
    private final Map<Integer, BufferedImage> imageData;
    private BufferedImage renderedImage = null;

    protected RasterPrintingJob(Map<Integer, BufferedImage> imageData) {
        super(new byte[0]);
        this.imageData = new HashMap<>(imageData);
    }

    @Override
    public BufferedImage getData() {
        if(renderedImage != null) {
            return renderedImage;
        }
        int finalWidth = 0;
        int finalHeight = 0;
        for(Map.Entry<Integer, BufferedImage> entry : imageData.entrySet()) {
            if(entry.getValue().getWidth() > finalWidth) {
                finalWidth = entry.getValue().getWidth();
            }
            if(entry.getKey() + 24 > finalHeight) {
                finalHeight = entry.getKey() + 24;
            }
        }

        BufferedImage finalImage = new BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D finalImageG2 = finalImage.createGraphics();
        finalImageG2.setPaint(new Color(255, 255, 255));
        finalImageG2.fillRect(0, 0, finalImage.getWidth(), finalImage.getHeight());


        for(Map.Entry<Integer, BufferedImage> entry : imageData.entrySet()) {
            int yOffset = entry.getKey();
            BufferedImage image = entry.getValue();

            finalImageG2.drawImage(image, 0, yOffset, null);
        }

        renderedImage = finalImage;

        return finalImage;
    }

    @Override
    public byte[] getRawData() {
        throw new UnsupportedOperationException("Raster Jobs do not support raw data");
    }
}
