package com.example.prog1tgproject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.scene.image.Image;

public class Album {

    private ArrayList<Image> images;
    private String path;
    private final String[] validExtensions = {".png", ".jpg", ".jpeg", ".gif"};


    public Album(String path){
        this.path = path;
        images = new ArrayList<>();
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.loadImages(path);
        this.path = path;
    }

    public boolean addToAlbum(Image image){
        if(!images.contains(image)){
            images.add(image);
            return true;
        }
        return false;
    }

    public boolean loadImages(String path){
        List<String> result = null;
        try (Stream<Path> walk = Files.walk(Path.of(path), 1)) {
            result = walk
                    .filter(p -> !Files.isDirectory(p))
                    // convert path to string
                    .map(Path::toString)
                    .filter(f -> endsWith(f, validExtensions))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(result != null) {
            for (String imagePath : result) {
                Image image = new Image(imagePath);
                addToAlbum(image);
            }
        }
        return true;
    }

    private boolean endsWith(String filePath, String[] extensions){
        for(String extension : extensions){
            if(filePath.endsWith(extension)){
                return true;
            }
        }
        return false;
    }
}
