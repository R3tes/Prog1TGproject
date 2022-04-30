package com.example.prog1tgproject;

import javafx.scene.image.ImageView;

public interface Plugin {
    String getName();
    void process(ImageView imageView);
}
