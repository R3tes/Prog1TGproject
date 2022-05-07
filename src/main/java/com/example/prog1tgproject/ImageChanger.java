package com.example.prog1tgproject;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageChanger{

    private Album album;
    private Image currentImage;
    private int currentImageIndex;
    public static Timer timer;

    public ImageChanger(Album album){
        this.album = album;
        this.currentImage = null;
        this.currentImageIndex = 0;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Image getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(String currentImagePath) {
        for(int i = 0; i < album.getImages().size(); i++){
            Image image = album.getImages().get(i);
            if(image.getUrl().equals(currentImagePath)){
                currentImage = image;
                currentImageIndex = i;
                break;
            }
        }
    }

    public boolean nextImage(ImageView imageView){
        if(album.getImages().size() == 0){
            return false;
        }
        currentImageIndex++;
        if(currentImageIndex >= album.getImages().size()) {
            currentImageIndex = 0;
        }
        currentImage = album.getImages().get(currentImageIndex);
        if(currentImage != null){
            imageView.setImage(currentImage);
            return true;
        }
        return false;
    }

    public boolean previousImage(ImageView imageView){
        if(album.getImages().size() == 0){
            return false;
        }
        currentImageIndex--;
        if(currentImageIndex < 0) {
            currentImageIndex = album.getImages().size()-1;
        }
        currentImage = album.getImages().get(currentImageIndex);
        if(currentImage != null){
            imageView.setImage(currentImage);
            return true;
        }
        return false;
    }

    final class SlideShow extends TimerTask{
        private ImageView imageView;

        public SlideShow(ImageView imageView){
            this.imageView = imageView;

        }

        @Override
        public void run() {
            nextImage(imageView);
        }
    }

    public void startSlideShow(ImageView imageView){
        timer = new Timer();
        timer.schedule(new SlideShow(imageView), 0, 5000);

    }

    public void endSlideShow(){
        if(timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }
}
