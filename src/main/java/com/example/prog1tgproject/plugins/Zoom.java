package com.example.prog1tgproject.plugins;

import com.example.prog1tgproject.Plugin;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

public class Zoom implements Plugin {

    @Override
    public String[] getImagePaths() {
        return new String[]{"/media/magnifier.png"};
    }

    @Override
    public BufferedImage process(ImageView imageView, BufferedImage bimage, int code) {
        System.out.println("Zoom");
        return bimage;
    }
}
