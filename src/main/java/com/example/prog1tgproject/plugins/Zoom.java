package com.example.prog1tgproject.plugins;

import com.example.prog1tgproject.Home;
import com.example.prog1tgproject.Plugin;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Scale;

import java.awt.image.BufferedImage;

public class Zoom implements Plugin {

    private static final int ZOOM_IN = 0;
    private static final int ZOOM_OUT = 1;

    @Override
    public String[] getImagePaths() {
        return new String[]{"/media/zoom-in.png", "/media/zoom-out.png"};
    }

    @Override
    public BufferedImage process(ImageView imageView, BufferedImage bimage, int code) {

        if(code == ZOOM_IN){
            Home.zoomProperty.set(Home.zoomProperty.get() * 1.8);
        }else if(code == ZOOM_OUT){
            Home.zoomProperty.set(Home.zoomProperty.get() / 1.8);
        }
        return bimage;
    }
}
