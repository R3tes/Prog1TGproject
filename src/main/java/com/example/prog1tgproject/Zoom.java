package com.example.prog1tgproject;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class Zoom {

    private static final int MIN_PIXELS = 20;

    private ImageView imageView;
    private Image image;

    ArrayList<Rectangle2D> prevZooms;

    public Zoom(ImageView imageView) {
        this.imageView = imageView;
        prevZooms = new ArrayList<>();
        init();
    }

    public void refresh(ImageView imageView){
        this.imageView = imageView;
        image = imageView.getImage();
        prevZooms.clear();
        imageView.setViewport(null);
        init();
    }

    private void init(){
        ObjectProperty<Point2D> mouseDown = new SimpleObjectProperty<>();
        double height = 0;
        double width = 0;
        try {
            height = image.getHeight();
            width = image.getWidth();

        }catch (Exception e){}
        double finalWidth = width;
        double finalHeight = height;

        imageView.setOnMousePressed(e -> {

            Point2D mousePress = imageViewToImage(new Point2D(e.getX(), e.getY()));
            mouseDown.set(mousePress);
        });

        imageView.setOnMouseDragged(e -> {
            Point2D dragPoint = imageViewToImage(new Point2D(e.getX(), e.getY()));
            shift(dragPoint.subtract(mouseDown.get()));
            mouseDown.set(imageViewToImage(new Point2D(e.getX(), e.getY())));
        });

        imageView.setOnScroll(e -> {
            double delta = e.getDeltaY();
            setZoom(delta, e.getX(), e.getY());
        });

        imageView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                reset(finalWidth, finalHeight);
            }
        });
    }

    public void setZoom(double delta, double x, double y){
        try {
            double height = 0;
            double width = 0;
            try {
                height = image.getHeight();
                width = image.getWidth();

            }catch (Exception e){}
            double finalWidth = width;
            double finalHeight = height;

            Rectangle2D viewport = imageView.getViewport();
            if(viewport == null){
                prevZooms.add(new Rectangle2D(0, 0, image.getWidth(), image.getHeight()));
                imageView.setViewport(new Rectangle2D(0, 0, image.getWidth(), image.getHeight()));
            }
            double scale = clamp(Math.pow(1.005, delta*(-1)),
                    Math.min(MIN_PIXELS / viewport.getWidth(), MIN_PIXELS / viewport.getHeight()),
                    Math.max(finalWidth / viewport.getWidth(), finalHeight / viewport.getHeight())
            );
            Point2D mouse = imageViewToImage(new Point2D(x, y));

            double newWidth = viewport.getWidth();
            double newHeight = viewport.getHeight();
            double imageViewRatio = (imageView.getFitWidth() / imageView.getFitHeight());
            //System.out.println("FitWidth: " + imageView.getFitWidth());
            //System.out.println("FitHeight: " + imageView.getFitHeight());
            double viewportRatio = (newWidth / newHeight);
            if (viewportRatio < imageViewRatio) {
                newHeight = newHeight * scale;
                newWidth = newHeight * imageViewRatio;
                if (newWidth > image.getWidth()) {
                    newWidth = image.getWidth();
                }
                /*System.out.println("WIDTH TO HEIGHT");
                System.out.println(imageViewRatio);
                System.out.println(viewportRatio);
                System.out.println(newHeight);
                System.out.println(" ");*/
            } else {
                newWidth = newWidth * scale;
                newHeight = newWidth / imageViewRatio;
                if (newHeight > image.getHeight()) {
                    newHeight = image.getHeight();
                }
                /*System.out.println("HEIGHT TO WIDTH");
                System.out.println(imageViewRatio);
                System.out.println(viewportRatio);
                System.out.println(newHeight);
                System.out.println(" ");*/
            }

            double newMinX = 0;
            if (newWidth < image.getWidth()) {
                newMinX = clamp(mouse.getX() - (mouse.getX() - viewport.getMinX()) * scale,
                        0, finalWidth - newWidth);
            }
            double newMinY = 0;
            if (newHeight < image.getHeight()) {
                newMinY = clamp(mouse.getY() - (mouse.getY() - viewport.getMinY()) * scale,
                        0, finalHeight - newHeight);
            }

            if(delta >= 0){
                if (scale != 1.0) {
                    imageView.setViewport(new Rectangle2D(newMinX, newMinY, newWidth, newHeight));
                    prevZooms.add(new Rectangle2D(newMinX, newMinY, newWidth, newHeight));
                }
            }else{
                if(prevZooms.size() > 1){
                    Rectangle2D zoom = prevZooms.get(prevZooms.size()-2);
                    imageView.setViewport(new Rectangle2D(newMinX, newMinY, zoom.getWidth(), zoom.getHeight()));
                    prevZooms.remove(prevZooms.get(prevZooms.size()-1));
                }
            }
        }catch (Exception e){}

    }

    private void reset(double width, double height) {
        prevZooms.clear();
        prevZooms.add(new Rectangle2D(0, 0, width, height));
        imageView.setViewport(new Rectangle2D(0, 0, width, height));
    }

    private void shift(Point2D delta) {
        try {
            Rectangle2D viewport = imageView.getViewport();
            double width = imageView.getImage().getWidth();
            double height = imageView.getImage().getHeight();
            double maxX = width - viewport.getWidth();
            double maxY = height - viewport.getHeight();
            double minX = clamp(viewport.getMinX() - delta.getX(), 0, maxX);
            double minY = clamp(viewport.getMinY() - delta.getY(), 0, maxY);
            if (minX < 0.0) {
                minX = 0.0;
            }
            if (minY < 0.0) {
                minY = 0.0;
            }
            imageView.setViewport(new Rectangle2D(minX, minY, viewport.getWidth(), viewport.getHeight()));
        } catch (NullPointerException ex){

        }
    }

    private double clamp(double value, double min, double max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }
    
    private Point2D imageViewToImage(Point2D imageViewCoordinates) {
        double xProportion = imageViewCoordinates.getX() / imageView.getBoundsInLocal().getWidth();
        double yProportion = imageViewCoordinates.getY() / imageView.getBoundsInLocal().getHeight();

        Rectangle2D viewport = imageView.getViewport();
        if(viewport == null){
            viewport = new Rectangle2D(0,0,0,0);
        }
        return new Point2D(
                viewport.getMinX() + xProportion * viewport.getWidth(),
                viewport.getMinY() + yProportion * viewport.getHeight());
    }

}
