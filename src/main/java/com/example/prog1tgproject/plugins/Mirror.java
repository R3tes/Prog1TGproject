package com.example.prog1tgproject.plugins;

import com.example.prog1tgproject.Home;
import com.example.prog1tgproject.Plugin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

public class Mirror implements Plugin {

    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;

    @Override
    public String[] getImagePaths() {
        return new String[]{"/media/mirror-horizontal.png", "/media/mirror-vertical.png"};
    }

    @Override
    public BufferedImage process(ImageView imageView, BufferedImage bimage, int code) {
        if(imageView.getImage() != null){
            BufferedImage after = new BufferedImage(bimage.getWidth(), bimage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            int height = bimage.getHeight();
            int width = bimage.getWidth();

            if(code == HORIZONTAL){
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        after.setRGB(x,(height-1)-y,bimage.getRGB(x,y));
                    }
                }
            }else if(code == VERTICAL){
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        after.setRGB((width-1)-x,y,bimage.getRGB(x,y));
                    }
                }
            }
            return after;
        }
        return null;
    }

}
