package com.example.prog1tgproject.plugins;

import com.example.prog1tgproject.Home;
import com.example.prog1tgproject.Plugin;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Rotate implements Plugin {

    private static final int RIGHT = 0;
    private static final int LEFT = 1;

    @Override
    public String[] getImagePaths() {
        String[] paths = new String[]{"/media/rotate-right.png", "/media/rotate-left.png"};
        return paths;
    }

    @Override
    public void process(ImageView imageView, int code) {
        if(imageView.getImage() != null){
            BufferedImage img;
            img = Home.img;
            double rads = 0;
            if(code == RIGHT){
                rads = Math.toRadians(90);
            }else if(code == LEFT){
                rads = Math.toRadians(-90);
            }
            double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
            int w = img.getWidth();
            int h = img.getHeight();
            int newWidth = (int) Math.floor(w * cos + h * sin);
            int newHeight = (int) Math.floor(h * cos + w * sin);

            BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = rotated.createGraphics();
            AffineTransform at = new AffineTransform();
            at.translate((newWidth - w) / 2, (newHeight - h) / 2);

            int x = w / 2;
            int y = h / 2;

            at.rotate(rads, x, y);
            g2d.setTransform(at);
            g2d.drawImage(img, 0, 0,null);
            g2d.dispose();
            imageView.setImage(Plugin.convertToFxImage(rotated));
            Home.img = rotated;
        }
    }
}
