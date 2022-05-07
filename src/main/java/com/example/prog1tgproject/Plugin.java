package com.example.prog1tgproject;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;

public interface Plugin {

    String[] getImagePaths();
    BufferedImage process(ImageView imageView, BufferedImage bimage, int code);
}
