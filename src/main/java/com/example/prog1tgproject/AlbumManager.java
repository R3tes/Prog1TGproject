package com.example.prog1tgproject;

import javafx.scene.control.MenuItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AlbumManager {
    private String currentPath;
    private ArrayList<Album> albums;
    public AlbumManager(String path) {
        this.currentPath = path;
        this.albums = new ArrayList<>();
        loadAlbums();
    }

    public boolean loadAlbums() {
        albums.clear();
        List<String> result = null;
        try (Stream<Path> walk = Files.walk(Path.of(currentPath), 1)) {
            result = walk
                    .filter(Files::isDirectory)
                    .map(Path::toString)
                    .filter(this::notCurrentDirectory)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(result != null) {
            for (String albumPath : result) {
                albums.add(new Album(albumPath));
            }
        }
        return true;
    }
    public boolean notCurrentDirectory(String path){
        return !currentPath.equals(path.replace('\\', '/'));
    }
    public ArrayList<Album> getAlbums(){
        return albums;
    }

}
