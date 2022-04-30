package com.example.prog1tgproject.plugins;

import com.example.prog1tgproject.Plugin;
import javafx.scene.image.ImageView;

public class Mirror implements Plugin {
    @Override
    public String getName() {
        return "Tükrözés";
    }

    @Override
    public void process(ImageView imageView) {
        System.out.println("Tukrozes");
    }
}
