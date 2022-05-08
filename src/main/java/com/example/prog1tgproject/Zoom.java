package com.example.prog1tgproject;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;

public class Zoom {

    private final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);

    private ImageView imageView;
    private ScrollPane scrollPane;

    public Zoom(ImageView imageView, ScrollPane scrollPane) {

        this.imageView = imageView;
        this.scrollPane = scrollPane;
        init();
    }

    private void init(){
        zoomProperty.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable arg0) {
                imageView.setFitWidth(zoomProperty.get() * 4);
                imageView.setFitHeight(zoomProperty.get() * 3);
            }
        });

        scrollPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    zoomProperty.set(zoomProperty.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    zoomProperty.set(zoomProperty.get() / 1.1);
                }
            }
        });
    }

    public void zoomIn(double value){
        zoomProperty.set(zoomProperty.get() * value);
    }

    public void zoomOut(double value){
        zoomProperty.set(zoomProperty.get() / value);
    }

    public void clearZoom(){
        imageView.setFitWidth(scrollPane.getWidth());
        imageView.setFitHeight(scrollPane.getHeight());
    }

}
