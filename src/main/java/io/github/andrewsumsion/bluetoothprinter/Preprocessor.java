package io.github.andrewsumsion.bluetoothprinter;

import java.awt.image.BufferedImage;

public interface Preprocessor {
    BufferedImage preprocess(BufferedImage image);
}
