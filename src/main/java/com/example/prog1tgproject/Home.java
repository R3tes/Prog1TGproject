package com.example.prog1tgproject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;

public class Home {
    @FXML
    private ImageView imageView;

    @FXML
    private ToolBar toolBar;

    @FXML
    private MenuButton opButton, addToAlbumButton;

    @FXML
    private MenuItem createNewAlbum, invertImageButton, grayscaleImageButton;

    @FXML
    private Button prevImageButton, nextImageButton, slideShowButton, zoomInButton, zoomOutButton;

    @FXML
    private HBox root;

    @FXML
    private StackPane container;

    File currentFile;
    ImageChanger imageChanger;
    BufferedImage img;
    AlbumManager albumManager;

    Zoom zoom;

    @FXML
    private void initialize() {

        loadPlugins();

        zoom = new Zoom(imageView);
        imageView.fitWidthProperty().bind(container.widthProperty());
        imageView.fitHeightProperty().bind(container.heightProperty());
        container.setAlignment(imageView, Pos.CENTER);
        root.setFillHeight(true);
        HBox.setHgrow(container, Priority.ALWAYS);

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
        albumManager = new AlbumManager("src/main/resources/AppPictures/Album");
        loadAlbums();

        createNewAlbum.setOnAction(event -> {
            TextInputDialog newAlbumPopup = new TextInputDialog();
            newAlbumPopup.setTitle("Új album");
            newAlbumPopup.setHeaderText("Mi legyen az album neve?");
            Optional<String> result = newAlbumPopup.showAndWait();
            if (result.isPresent() && !newAlbumPopup.getResult().trim().equals("")) {
                String albumName = newAlbumPopup.getResult();
                File newAlbum = new File("src/main/resources/AppPictures/Album/" + albumName);
                if (!newAlbum.exists()) {
                    if (imageChanger.getCurrentImage() != null && newAlbum.mkdirs()) {
                        String extension = imageChanger.getCurrentImage().getAbsolutePath()
                                .substring(imageChanger.getCurrentImage().getAbsolutePath().length() - 3);
                        File targetDirectory = new File(newAlbum.getAbsolutePath() + "/" + imageChanger.getCurrentImage().getName());
                        saveImage(extension, targetDirectory);
                    }
                    loadAlbums();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Hiba");
                    alert.setHeaderText("Ilyen nevű album már létezik!");
                    alert.showAndWait();
                }
            }

        });

        open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                imageChanger.endSlideShow();
                File file = fileChooser.showOpenDialog(imageView.getScene().getWindow());
                if (file != null) {
                    imageChanger.getAlbum().setPath(file.getParent());
                    imageChanger.setCurrentImage(file.getAbsolutePath());

                    imageView.setImage(new Image(imageChanger.getCurrentImage().getAbsolutePath()));
                    setBufferedImage(imageChanger.getCurrentImage());
                    Rectangle2D viewport = imageView.getViewport();
                    zoom.refresh(imageView);
                }
            }
        });

        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                imageChanger.endSlideShow();
                imageChanger.getAlbum().getImages().remove(imageChanger.getCurrentImage());
                imageChanger.getCurrentImage().delete();
                imageView.setImage(null);
            }
        });

        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg");

        savAs.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                imageChanger.endSlideShow();
                File dir = fileChooser.showSaveDialog(opButton.getScene().getWindow());
                if (dir != null) {
                    String extension = dir.getAbsolutePath().substring(dir.getAbsolutePath().length() - 3);
                    saveImage(extension, dir);
                }
            }
        });

        setGraphic(prevImageButton, "/media/left-button.png", 43, 43);
        prevImageButton.setOnAction(event -> {
            imageChanger.endSlideShow();
            imageChanger.previousImage(imageView);
            setBufferedImage(imageChanger.getCurrentImage());
            zoom.refresh(imageView);
        });

        setGraphic(nextImageButton, "/media/right-button.png", 43, 43);
        nextImageButton.setOnAction(event -> {
            imageChanger.endSlideShow();
            imageChanger.nextImage(imageView);
            setBufferedImage(imageChanger.getCurrentImage());
            zoom.refresh(imageView);
        });

        opButton.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.LEFT) {
                imageChanger.endSlideShow();
                imageChanger.previousImage(imageView);
                setBufferedImage(imageChanger.getCurrentImage());
                zoom.refresh(imageView);
            }
            if (keyEvent.getCode() == KeyCode.RIGHT) {
                imageChanger.endSlideShow();
                imageChanger.nextImage(imageView);
                setBufferedImage(imageChanger.getCurrentImage());
                zoom.refresh(imageView);
            }
        });

        slideShowButton.setOnAction(event -> {
            imageChanger.startSlideShow(imageView);
            setBufferedImage(imageChanger.getCurrentImage());
            zoom.refresh(imageView);
        });

        setGraphic(zoomInButton, "/media/zoom-in.png", 20, 18);
        zoomInButton.setOnAction(event -> {
            imageChanger.endSlideShow();
            setBufferedImage(imageChanger.getCurrentImage());
            zoom.setZoom(34, imageView.getImage().getWidth() / 2, imageView.getImage().getHeight() / 2);
        });

        setGraphic(zoomOutButton, "/media/zoom-out.png", 20, 18);
        zoomOutButton.setOnAction(event -> {
            imageChanger.endSlideShow();
            setBufferedImage(imageChanger.getCurrentImage());
            zoom.setZoom(-34, imageView.getImage().getWidth() / 2, imageView.getImage().getHeight() / 2);
        });


    }

    private void setBufferedImage(File currentImage) {
        try {
            if (currentImage != null) {
                img = ImageIO.read(currentImage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPlugins() {
        ArrayList<Plugin> plugins;
        try {
            plugins = new PluginLoader().getPlugins();

            for (Plugin plugin : plugins) {
                String[] paths = plugin.getImagePaths();
                for (int i = 0; i < paths.length; i++) {
                    Button button = new Button();
                    setGraphic(button, paths[i], 20, 18);
                    int finalI = i;
                    button.setOnAction(event -> {
                        if (ImageChanger.timer != null) {
                            setBufferedImage(imageChanger.getCurrentImage());
                        }
                        imageChanger.endSlideShow();
                        img = plugin.process(imageView, img, finalI);
                        imageView.setImage(convertToFxImage(img));
                        zoom.refresh(imageView);
                    });
                    toolBar.getItems().add(button);
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
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

    private void setGraphic(Button button, String mediaPath, int width, int height) {
        URL _url = getClass().getResource(mediaPath);
        ImageView image = new ImageView(new Image(_url.toExternalForm()));
        image.setFitWidth(width);
        image.setFitHeight(height);
        button.setGraphic(image);
    }

    public void saveImage(String extension, File targetDirectory) {
        try {
            boolean isSaved = ImageIO.write(img, extension, targetDirectory);
            if (!isSaved) {
                BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
                for (int y = 0; y < img.getHeight(); y++) {
                    for (int x = 0; x < img.getWidth(); x++) {
                        image.setRGB(x, y, img.getRGB(x, y));
                    }
                }
                ImageIO.write(image, extension, targetDirectory);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAlbums() {
        albumManager.loadAlbums();
        for (Album album : albumManager.getAlbums()) {
            String[] splitPath;
            splitPath = album.getPath().replace('\\', '/').split(Pattern.quote("/"));
            boolean exits = false;
            for (MenuItem item : addToAlbumButton.getItems()) {
                if (item.getText().equals(splitPath[splitPath.length - 1])) {
                    exits = true;
                }
            }
            if (!exits) {
                Menu albumButton = new Menu(splitPath[splitPath.length - 1]);
                MenuItem addPhoto  = new MenuItem("Kép hozzáadása");
                addPhoto.setOnAction(event -> {
                    String extension = imageChanger.getCurrentImage().getAbsolutePath()
                            .substring(imageChanger.getCurrentImage().getAbsolutePath().length() - 3);
                    File targetDirectory = new File(album.getPath().replace('\\', '/') + "/" + imageChanger.getCurrentImage().getName());
                    saveImage(extension, targetDirectory);
                });

                MenuItem renameAlbum  = new MenuItem("Átnevezés");
                renameAlbum.setOnAction(event -> {
                    TextInputDialog newAlbumPopup = new TextInputDialog();
                    newAlbumPopup.setTitle("Átnevezés");
                    newAlbumPopup.setHeaderText("Mi legyen az album új neve?");
                    Optional<String> result = newAlbumPopup.showAndWait();
                    if (result.isPresent() && !newAlbumPopup.getResult().trim().equals("")) {
                        String albumName = newAlbumPopup.getResult();
                        File newAlbum = new File("src/main/resources/AppPictures/Album/" + albumName);
                        File oldAlbum = new File(album.getPath().replace('\\', '/'));
                        if(oldAlbum.renameTo(newAlbum)){
                            albumButton.setText(albumName);
                            album.setPath("src/main/resources/AppPictures/Album/" + albumName);
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Üzenet");
                            alert.setHeaderText("Az albumot sikeresen átnevezted!");
                            alert.showAndWait();
                        }else{
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Hiba");
                            alert.setHeaderText("Hiba történt az album átnevezése során!");
                            alert.showAndWait();
                        }
                    }
                });

                MenuItem deleteAlbum  = new MenuItem("Törlés");
                deleteAlbum.setOnAction(event -> {
                    File albumToDelete = new File(album.getPath().replace('\\', '/'));
                    if(deleteFolder(albumToDelete)){
                        addToAlbumButton.getItems().remove(albumButton);
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Üzenet");
                        alert.setHeaderText("Az albumot sikeresen törölted!");
                        alert.showAndWait();

                    }else{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Hiba");
                        alert.setHeaderText("Hiba történt az album törlése során!");
                        alert.showAndWait();
                    }

                });
                albumButton.getItems().addAll(addPhoto, renameAlbum, deleteAlbum);
                addToAlbumButton.getItems().add(albumButton);
            }
        }
    }

    private boolean deleteFolder(File folder){
        File[] files = folder.listFiles();
        if(files != null && files.length > 0){
            for(File file : files){
                if(!file.isDirectory()){
                    file.delete();
                }else{
                    deleteFolder(file);
                }
            }
        }
        return folder.delete();
    }
}