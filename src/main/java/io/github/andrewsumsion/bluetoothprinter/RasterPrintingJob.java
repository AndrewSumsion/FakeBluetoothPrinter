package io.github.andrewsumsion.bluetoothprinter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RasterPrintingJob extends PrintingJob {
    private Map<Integer, BufferedImage> imageData;
    private BufferedImage renderedImage = null;

    public RasterPrintingJob(Map<Integer, BufferedImage> imageData) {
        super(new byte[0]);
        this.imageData = new HashMap<>(imageData);
    }

    public RasterPrintingJob(byte[] data) {
        super(data);
        this.imageData = new HashMap<>();
    }

    private BufferedImage dataToImage(byte[] data, int width) {
        int finalWidth = width;
        int finalHeight = (int)(Math.ceil((double)data.length / (double)finalWidth));
        System.out.println("Width:  " + finalWidth);
        System.out.println("Height: " + finalHeight);
        BufferedImage finalImage = new BufferedImage(finalWidth + 1000, finalHeight + 1000, BufferedImage.TYPE_INT_ARGB);
        int x = 0;
        int y = 0;
        for(int i = 0; i < data.length; i++) {
            Color transparent = new Color(0, 0, 0, 0);
            finalImage.setRGB(x + 0, y, (((data[i] & 128) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 1, y, (((data[i] &  64) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 2, y, (((data[i] &  32) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 3, y, (((data[i] &  16) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 4, y, (((data[i] &   8) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 5, y, (((data[i] &   4) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 6, y, (((data[i] &   2) != 0) ? Color.BLACK : transparent).getRGB());
            finalImage.setRGB(x + 7, y, (((data[i] &   1) != 0) ? Color.BLACK : transparent).getRGB());

            x += 8;
            if(x >= finalWidth) {
                x = 0;
                y += 1;
            }
        }
        return finalImage;
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
    public RasterPrintingJob clone() {
        return new RasterPrintingJob(imageData);
    }

    @Override
    public byte[] getRawData() {
        if(rawData.length < 1) {
            throw new UnsupportedOperationException("Hybrid Raster Jobs do not support raw data");
        }
        return rawData;
    }
}
