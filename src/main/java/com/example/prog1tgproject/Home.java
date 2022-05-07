package com.example.prog1tgproject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
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

    File currentFile;
    ImageChanger imageChanger;
    BufferedImage img;

    @FXML
    private void initialize() {

        loadPlugins();

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
                    img = toBufferedImage(imageView.getImage());
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
                    BufferedImage bufferedImage = null;
                    try {
                        bufferedImage = ImageIO.read(currentFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        ImageIO.write(bufferedImage, dir.getAbsolutePath().substring(dir.getAbsolutePath().length() - 3), dir);
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


    }

    private void loadPlugins() {
        ArrayList<Plugin> plugins = null;
        try {
            plugins = new PluginLoader().getPlugins();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
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
                        plugin.process(imageView, img, finalI);
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

    public static BufferedImage toBufferedImage(Image img) {


        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage((int) img.getWidth(), (int) img.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        //bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public ImageView getImageView() {
        return imageView;
    }
}