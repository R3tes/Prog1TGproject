package com.example.prog1tgproject;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Filters extends Home {

    private final MenuButton filterButton;
    private final StackPane container;
    private final ImageView imageView;
    private Image image;

    public Filters(MenuButton filterButton, ImageView imageView, StackPane container) {
        this.filterButton = filterButton;
        this.imageView = imageView;
        this.container = container;
    }

    //private final List<MenuItem> menuItems = new ArrayList<>();

    private List<Filter> filters = Arrays.asList(
            new Filter("Inverz", c -> c.invert()),
            new Filter("Szürkeskála", c -> c.grayscale()),
            new Filter("Fekete-fehér", c -> valueOf(c) < 1.5 ? Color.BLACK : Color.WHITE),
            new Filter("Vörös", c -> Color.color(1.0, c.getGreen(), c.getBlue())),
            new Filter("Zöld", c -> Color.color(c.getRed(), 1.0, c.getBlue())),
            new Filter("Kék", c -> Color.color(c.getRed(), c.getGreen(), 1.0))
    );

    private double valueOf(Color c) {
        return c.getRed() + c.getGreen() + c.getBlue();
    }

    public Image createFilter() {
        ImageView imageView2 = new ImageView();

        filters.forEach(filterItem -> {
            MenuItem item = new MenuItem(filterItem.name);
            item.setOnAction(e -> {
                imageView2.setImage(filterItem.apply(imageView.getImage()));
            });

            filterButton.getItems().add(item);
        });

        return imageView2.getImage();
    }

    private static class Filter implements Function<Image, Image> {

        private String name;
        private Function<Color, Color> colorMap;

        Filter(String name, Function<Color, Color> colorMap) {
            this.name = name;
            this.colorMap = colorMap;
        }

        @Override
        public Image apply(Image source) {
            int imgWidth = (int) source.getWidth();
            int imgHeight = (int) source.getHeight();

            WritableImage image = new WritableImage(imgWidth, imgHeight);

            for (int y = 0; y < imgHeight; y++) {
                for (int x = 0; x < imgWidth; x++) {
                    Color c1 = source.getPixelReader().getColor(x, y);
                    Color c2 = colorMap.apply(c1);

                    image.getPixelWriter().setColor(x, y, c2);
                }
            }

            return image;
        }
    }


}
