package com.example.prog1tgproject;

import javax.imageio.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

public class Home {
    @FXML
    private VBox mainWindow;

    @FXML
    private ImageView imageView;

    @FXML
    private ToolBar toolBar;

    @FXML
    private MenuButton opButton, addToAlbumButton, filterButton;

    @FXML
    private MenuItem createNewAlbum, invertImageButton, grayscaleImageButton, blackandwhiteImageButton,  blueImageButton, greenImageButton, redImageButton;

    @FXML
    private Button prevImageButton, nextImageButton, slideShowButton, zoomInButton, zoomOutButton;

    @FXML
    private HBox root, drawSlider;

    @FXML
    private StackPane container;

    @FXML
    private ToggleButton drawButton;

    @FXML
    private Canvas canvasDraw;

    @FXML
    public ToggleButton pencilDrawButton, lineDrawButton, rectDrawButton, circleDrawButton, rubberDrawButton, textDrawButton;

    @FXML
    public ColorPicker colorDrawPicker;

    @FXML
    public Slider pencilstrDrawSlider;

    @FXML
    private Label pencilstrText, colorpickerText;

    @FXML
    private TextField writeTextField;

    @FXML
    private Button undoDrawButton, saveDrawButton, redoDrawButton;

    File currentFile;
    ImageChanger imageChanger;
    BufferedImage img;
    AlbumManager albumManager;

    Zoom zoom;
    Draw draw;

    @FXML
    private void initialize() {

        loadPlugins();
        drawSlider.setVisible(false);
        canvasDraw.setVisible(false);

        zoom = new Zoom(imageView);
        draw = new Draw(container, imageView, canvasDraw, pencilDrawButton, lineDrawButton, rectDrawButton, circleDrawButton, rubberDrawButton,
                colorDrawPicker, pencilstrDrawSlider, textDrawButton, undoDrawButton, redoDrawButton, saveDrawButton, writeTextField);
        imageView.fitWidthProperty().bind(container.widthProperty());
        imageView.fitHeightProperty().bind(container.heightProperty());
        container.setAlignment(imageView, Pos.CENTER);
        root.setFillHeight(true);
        HBox.setHgrow(container, Priority.ALWAYS);

        MenuItem open = new MenuItem("Megnyit??s");
        MenuItem save = new MenuItem("Ment??s");
        MenuItem savAs = new MenuItem("Ment??s m??sk??nt");
        MenuItem delete = new MenuItem("T??rl??s");
        MenuItem prop = new MenuItem("K??p r??szletek");
        opButton.getItems().add(open);
        opButton.getItems().add(save);
        opButton.getItems().add(savAs);
        opButton.getItems().add(delete);
        opButton.getItems().add(prop);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src/main/resources/AppPictures"));
        imageChanger = new ImageChanger(new Album("src/main/resources/AppPictures"));
        albumManager = new AlbumManager("src/main/resources/AppPictures/Albumok");
        loadAlbums();
        Filters filters = new Filters(filterButton, imageView, imageChanger);

        createNewAlbum.setOnAction(event -> {
            TextInputDialog newAlbumPopup = new TextInputDialog();
            newAlbumPopup.setTitle("??j album");
            newAlbumPopup.setHeaderText("Mi legyen az album neve?");
            Optional<String> result = newAlbumPopup.showAndWait();
            if (result.isPresent() && !newAlbumPopup.getResult().trim().equals("")) {
                String albumName = newAlbumPopup.getResult();
                File newAlbum = new File("src/main/resources/AppPictures/Albumok/" + albumName);
                if (!newAlbum.exists()) {
                    if (imageChanger.getCurrentImage() != null && newAlbum.mkdirs()) {
                        String extension = imageChanger.getCurrentImage().getAbsolutePath()
                                .substring(imageChanger.getCurrentImage().getAbsolutePath().length() - 3);
                        File targetDirectory = new File(newAlbum.getAbsolutePath() + "/" + imageChanger.getCurrentImage().getName());
                        saveImage(img, extension, targetDirectory);
                    }
                    loadAlbums();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Hiba");
                    alert.setHeaderText("Ilyen nev?? album m??r l??tezik!");
                    alert.showAndWait();
                }
            }

        });

        prop.setOnAction(actionEvent -> {
            if(imageView.getImage()!=null){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("K??p r??szletei");
                alert.setHeaderText(null);

                String imgExtension = "";
                String imgName = "";
                File dir = imageChanger.getCurrentImage();
                if (dir != null) {
                    imgExtension = dir.getAbsolutePath().substring(dir.getAbsolutePath().length() - 3);
                }
                imgName = dir.getName().substring(0, dir.getName().length() - imgExtension.length() - 1);
                BufferedImage image = img;
                int clr = image.getRGB(img.getMinX(), img.getMinY());
                int red =   (clr & 0x00ff0000) >> 16;
                int green = (clr & 0x0000ff00) >> 8;
                int blue =   clr & 0x000000ff;

                alert.setContentText("K??p neve: " + imgName +
                        "\nKiterjesz??se: (." + imgExtension + ")" +
                        "\nEl??r??si ??tvonal: " + dir.getAbsolutePath() +
                        "\nDimenzi??k: " + img.getWidth() + " x " + img.getHeight() +
                        "\nSz??less??g: " + img.getWidth() + " pixel" +
                        "\nMagass??g: " + img.getHeight() + " pixel" +
                        "\nV??r??s: " + red + " R" +
                        "\nZ??ld: " + green + " G" +
                        "\nK??k: " + blue + " B" );

                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image(this.getClass().getResource("/app-logo.png").toString()));
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

                alert.showAndWait();
            }
        });

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                imageChanger.endSlideShow();

                refreshDraw();

                BufferedImage bimg = null;
                try {
                    bimg = SwingFXUtils.fromFXImage(imageView.getImage(), null);
                } catch (NullPointerException ex){

                }

                File dir = imageChanger.getCurrentImage();
                if (dir != null) {
                    String extension = dir.getAbsolutePath().substring(dir.getAbsolutePath().length() - 3);
                    saveImage(bimg, extension, dir);
                }
            }
        });

        open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                imageChanger.endSlideShow();

                refreshDraw();

                File file = fileChooser.showOpenDialog(imageView.getScene().getWindow());
                if (file != null) {
                    imageChanger.getAlbum().setPath(file.getParent());
                    imageChanger.setCurrentImage(file.getAbsolutePath());

                    imageView.setImage(new Image(imageChanger.getCurrentImage().getAbsolutePath()));
                    setBufferedImage(imageChanger.getCurrentImage());
                    Rectangle2D viewport = imageView.getViewport();
                    filters.setOriginal(imageView.getImage());
                    zoom.refresh(imageView);

                }
            }
        });

        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                imageChanger.endSlideShow();

                refreshDraw();

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

                refreshDraw();

                BufferedImage bimg = null;
                try {
                    bimg = SwingFXUtils.fromFXImage(imageView.getImage(), null);
                } catch (NullPointerException ex){

                }

                File dir = fileChooser.showSaveDialog(opButton.getScene().getWindow());
                if (dir != null) {
                    String extension = dir.getAbsolutePath().substring(dir.getAbsolutePath().length() - 3);
                    saveImage(bimg, extension, dir);
                }
            }
        });

        saveDrawButton.setOnAction((e) -> {
            FileChooser savefile = new FileChooser();
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg");
            savefile.setTitle("Save File");

            File file = savefile.showSaveDialog(imageView.getScene().getWindow());
            if (file != null) {

                WritableImage writableImage = canvasDraw.snapshot(new SnapshotParameters(), null);

                //System.out.println(writableImage.getWidth() + " " + writableImage.getHeight());
                BufferedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                String extension = file.getAbsolutePath().substring(file.getAbsolutePath().length() - 3);
                saveImage(renderedImage, extension, file);

                refreshDraw();
            }
        });

        setGraphic(prevImageButton, "/media/left-button.png", 43, 43);
        prevImageButton.setOnAction(event -> {
            imageChanger.endSlideShow();
            imageChanger.previousImage(imageView);
            setBufferedImage(imageChanger.getCurrentImage());
            zoom.refresh(imageView);
            filters.setOriginal(imageView.getImage());
            refreshDraw();
        });

        setGraphic(nextImageButton, "/media/right-button.png", 43, 43);
        nextImageButton.setOnAction(event -> {
            imageChanger.endSlideShow();
            imageChanger.nextImage(imageView);
            setBufferedImage(imageChanger.getCurrentImage());
            zoom.refresh(imageView);
            filters.setOriginal(imageView.getImage());
            refreshDraw();
        });

        opButton.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.LEFT) {
                imageChanger.endSlideShow();
                imageChanger.previousImage(imageView);
                setBufferedImage(imageChanger.getCurrentImage());
                zoom.refresh(imageView);

                refreshDraw();
            }
            if (keyEvent.getCode() == KeyCode.RIGHT) {
                imageChanger.endSlideShow();
                imageChanger.nextImage(imageView);
                setBufferedImage(imageChanger.getCurrentImage());
                zoom.refresh(imageView);

                refreshDraw();
            }
        });

        slideShowButton.setOnAction(event -> {
            imageChanger.endSlideShow();

            refreshDraw();

            imageChanger.startSlideShow(imageView);
            setBufferedImage(imageChanger.getCurrentImage());
            filters.setOriginal(imageView.getImage());
            zoom.refresh(imageView);
        });

        setGraphic(zoomInButton, "/media/zoom-in.png", 20, 18);
        zoomInButton.setOnAction(event -> {
            imageChanger.endSlideShow();

            refreshDraw();
            
            setBufferedImage(imageChanger.getCurrentImage());
            zoom.setZoom(34, imageView.getImage().getWidth() / 2, imageView.getImage().getHeight() / 2);
        });

        setGraphic(zoomOutButton, "/media/zoom-out.png", 20, 18);
        zoomOutButton.setOnAction(event -> {
            imageChanger.endSlideShow();

            refreshDraw();

            setDrawToDefault();
            setBufferedImage(imageChanger.getCurrentImage());
            zoom.setZoom(-34, imageView.getImage().getWidth() / 2, imageView.getImage().getHeight() / 2);
        });

        drawButton.selectedProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue) {
                drawSlider.setVisible(true);

                setDrawToDefault();
                zoom.refresh(imageView);
                try {
                    if(imageView.getImage()!=null){
                        draw.initializeDraw();
                        draw.gc.drawImage(imageView.getImage(), 0, 0, canvasDraw.getWidth(), canvasDraw.getHeight());
                    }
                } catch (NullPointerException exep){

                }
            } else {
                drawSlider.setVisible(false);
                setDrawToDefault();
                canvasDraw.setVisible(false);
            }
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
                    button.setCursor(Cursor.HAND);
                    int finalI = i;
                    button.setOnAction(event -> {
                        if (ImageChanger.timer != null) {
                            setBufferedImage(imageChanger.getCurrentImage());
                        }
                        imageChanger.endSlideShow();
                        img = plugin.process(imageView, img, finalI);
                        imageView.setImage(convertToFxImage(img));
                        zoom.refresh(imageView);

                        refreshDraw();
                    });
                    toolBar.getItems().add(button);
                }
                if(paths.length == 0){
                    for (int i = 0; i < plugin.getName().length; i++) {
                        Button button = new Button(plugin.getName()[i]);
                        button.setCursor(Cursor.HAND);
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

    public void saveImage(BufferedImage imageToSave, String extension, File targetDirectory) {
        try {
            boolean isSaved = ImageIO.write(imageToSave, extension, targetDirectory);
            if (!isSaved) {
                BufferedImage image = new BufferedImage(imageToSave.getWidth(), imageToSave.getHeight(), BufferedImage.TYPE_INT_RGB);

                for (int y = 0; y < imageToSave.getHeight(); y++) {
                    for (int x = 0; x < imageToSave.getWidth(); x++) {
                        image.setRGB(x, y, imageToSave.getRGB(x, y));
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
                MenuItem addPhoto = new MenuItem("K??p hozz??ad??sa");
                addPhoto.setOnAction(event -> {
                    String extension = imageChanger.getCurrentImage().getAbsolutePath()
                            .substring(imageChanger.getCurrentImage().getAbsolutePath().length() - 3);
                    File targetDirectory = new File(album.getPath().replace('\\', '/') + "/" + imageChanger.getCurrentImage().getName());
                    saveImage(img, extension, targetDirectory);
                });

                MenuItem renameAlbum = new MenuItem("??tnevez??s");
                renameAlbum.setOnAction(event -> {
                    TextInputDialog newAlbumPopup = new TextInputDialog();
                    newAlbumPopup.setTitle("??tnevez??s");
                    newAlbumPopup.setHeaderText("Mi legyen az album ??j neve?");
                    Optional<String> result = newAlbumPopup.showAndWait();
                    if (result.isPresent() && !newAlbumPopup.getResult().trim().equals("")) {
                        String albumName = newAlbumPopup.getResult();
                        File newAlbum = new File("src/main/resources/AppPictures/Albumok/" + albumName);
                        File oldAlbum = new File(album.getPath().replace('\\', '/'));
                        if (oldAlbum.renameTo(newAlbum)) {
                            albumButton.setText(albumName);
                            album.setPath("src/main/resources/AppPictures/Albumok/" + albumName);
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("??zenet");
                            alert.setHeaderText("Az albumot sikeresen ??tnevezted!");
                            alert.showAndWait();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Hiba");
                            alert.setHeaderText("Hiba t??rt??nt az album ??tnevez??se sor??n!");
                            alert.showAndWait();
                        }
                    }
                });

                MenuItem deleteAlbum = new MenuItem("T??rl??s");
                deleteAlbum.setOnAction(event -> {
                    File albumToDelete = new File(album.getPath().replace('\\', '/'));
                    if (deleteFolder(albumToDelete)) {
                        addToAlbumButton.getItems().remove(albumButton);
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("??zenet");
                        alert.setHeaderText("Az albumot sikeresen t??r??lted!");
                        alert.showAndWait();

                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Hiba");
                        alert.setHeaderText("Hiba t??rt??nt az album t??rl??se sor??n!");
                        alert.showAndWait();
                    }

                });
                albumButton.getItems().addAll(addPhoto, renameAlbum, deleteAlbum);
                addToAlbumButton.getItems().add(albumButton);
            }
        }
    }

    private boolean deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    file.delete();
                } else {
                    deleteFolder(file);
                }
            }
        }
        return folder.delete();
    }

    public void setDrawToDefault(){
        pencilDrawButton.setSelected(false);
        rubberDrawButton.setSelected(false);
        lineDrawButton.setSelected(false);
        rectDrawButton.setSelected(false);
        circleDrawButton.setSelected(false);
        colorDrawPicker.setValue(Color.WHITE);
        pencilstrDrawSlider.setValue(0);
        writeTextField.setText("");
    }
    
    public void refreshDraw(){
        setDrawToDefault();
        canvasDraw.setVisible(false);
        drawSlider.setVisible(false);
        drawButton.setSelected(false);
    }
}