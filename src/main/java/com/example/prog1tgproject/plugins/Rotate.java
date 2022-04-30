package com.example.prog1tgproject.plugins;

import com.example.prog1tgproject.Plugin;
import javafx.scene.image.ImageView;

public class Rotate implements Plugin {
    @Override
    public String getName() {
        return "Forgatás";
    }

    @Override
    public void process(ImageView imageView) {
        imageView.setRotate(imageView.getRotate()+90);
    }

}
