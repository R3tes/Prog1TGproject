package com.example.prog1tgproject;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;

public class Home {
    @FXML
    private ImageView imageView;

    @FXML
    private ToolBar toolBar;

    @FXML
    private MenuButton opButton;

    @FXML
    private Button prevImageButton;

    @FXML
    private Button nextImageButton;

    @FXML
    private Button slideShowButton;

    @FXML
    private ScrollPane scrollPane;

    File currentFile;
    ImageChanger imageChanger;
    BufferedImage img;


    public final static DoubleProperty zoomProperty = new SimpleDoubleProperty(200);

    @FXML
    private void initialize() {

        loadPlugins();

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

        MenuItem open = new MenuItem("Megnyitás");
        MenuItem save = new MenuItem("Mentés");
        MenuItem savAs = new MenuItem("Mentés másként");
        MenuItem delete = new MenuItem("Törlés");
        MenuItem prop = new MenuItem("Részletek");
        opButton.getItems().add(open);
        opButton.getItems().add(save);
        opButton.getItems().add(savAs);
        opButton.getItems().add(delete);
        opButton.getItems().add(prop);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src/main/resources/AppPictures"));
        imageChanger = new ImageChanger(new Album("src/main/resources/AppPictures"));

        open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                File file = fileChooser.showOpenDialog(imageView.getScene().getWindow());
                if (file != null) {
                    imageChanger.getAlbum().setPath(file.getParent());
                    imageChanger.setCurrentImage(file.getAbsolutePath());
                    imageView.setImage(imageChanger.getCurrentImage());
                    try {
                        img = ImageIO.read(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                currentFile.delete();
                imageView.setImage(null);
            }
        });

        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg");

        savAs.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                File dir = fileChooser.showSaveDialog(opButton.getScene().getWindow());
                if (dir != null) {
                    try {
                        boolean isSaved = ImageIO.write(img, dir.getAbsolutePath().substring(dir.getAbsolutePath().length() - 3), dir);
                        if(!isSaved){
                            BufferedImage image = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_RGB);
                            for (int y = 0; y < img.getHeight(); y++) {
                                for (int x = 0; x < img.getWidth(); x++) {
                                    image.setRGB(x,y,img.getRGB(x,y));
                                }
                            }
                            ImageIO.write(image, dir.getAbsolutePath().substring(dir.getAbsolutePath().length() - 3), dir);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        prevImageButton.setOnAction(event -> {
            imageChanger.endSlideShow();
            imageChanger.previousImage(imageView);
        });

        nextImageButton.setOnAction(event -> {
            imageChanger.endSlideShow();
            imageChanger.nextImage(imageView);
        });

        slideShowButton.setOnAction(event -> {
            imageChanger.startSlideShow(imageView);
        });

        /*URL _url = getClass().getResource("/media/left-button.png");
        ImageView image = new ImageView(new Image(_url.toExternalForm()));
        image.setFitWidth(20);
        image.setFitHeight(18);
        prevImageButton.setGraphic(image);

        _url = getClass().getResource("/media/right-button.png");
        image = new ImageView(new Image(_url.toExternalForm()));
        image.setFitWidth(20);
        image.setFitHeight(18);
        prevImageButton.setGraphic(image);*/
    }

    private void loadPlugins() {
        ArrayList<Plugin> plugins = null;
        try {
            plugins = new PluginLoader().getPlugins();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        for (Plugin plugin : plugins) {

            String[] paths = plugin.getImagePaths();
            for (int i = 0; i < paths.length; i++) {
                Button button = new Button();
                URL _url = getClass().getResource(paths[i]);
                ImageView image = new ImageView(new Image(_url.toExternalForm()));
                image.setFitWidth(20);
                image.setFitHeight(18);
                button.setGraphic(image);

                int finalI = i;
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        img = plugin.process(imageView, img, finalI);
                        imageView.setImage(convertToFxImage(img));
                    }
                });
                toolBar.getItems().add(button);
            }
        }
    }

    private Image convertToFxImage(BufferedImage image) {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }
        return new ImageView(wr).getImage();
    }

    public ImageView getImageView() {
        return imageView;
    }
}