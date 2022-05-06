package com.example.prog1tgproject.plugins;

import com.example.prog1tgproject.Plugin;
import javafx.scene.image.ImageView;

public class Zoom implements Plugin {

    @Override
    public String[] getImagePaths() {
        return new String[]{"/media/magnifier.png"};
    }

    @Override
    public void process(ImageView imageView, int code) {
        System.out.println("Zoom");
    }
}
