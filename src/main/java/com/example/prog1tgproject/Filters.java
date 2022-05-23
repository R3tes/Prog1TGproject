package com.example.prog1tgproject;

import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class Filters extends Home {

    private final MenuButton filterButton;
    private final StackPane container;
    private final ImageView imageView;
    private Image image;
    public ImageView imageView2 = new ImageView();

    public Filters(MenuButton filterButton, ImageView imageView, StackPane container) {
        this.filterButton = filterButton;
        this.imageView = imageView;
        this.container = container;
    }

    private List<Filter> filters = Arrays.asList(
            new Filter("Inverz", Color::invert), new Filter("Szürkeskála", Color::grayscale),
            new Filter("Fekete-fehér", c -> valueOf(c) < 1.5 ? Color.BLACK : Color.WHITE),
            new Filter("Vörös", c -> Color.color(1.0, c.getGreen(), c.getBlue())),
            new Filter("Zöld", c -> Color.color(c.getRed(), 1.0, c.getBlue())),
            new Filter("Kék", c -> Color.color(c.getRed(), c.getGreen(), 1.0)),
            new Filter("Eredeti", c -> Color.color(c.getRed(), c.getGreen(), c.getBlue()))
    );

    private double valueOf(Color c) {
        return c.getRed() + c.getGreen() + c.getBlue();
    }

    public Image createFilter() {

        AtomicBoolean eredetiE = new AtomicBoolean(false);

        filters.forEach(filterItem -> {
            MenuItem item = new MenuItem(filterItem.name);
            filterButton.getItems().add(item);

            if (Objects.equals(item.getText(), "Eredeti")) {
                eredetiE.set(true);
            }

            item.setOnAction(e -> {
                imageView2.setImage(filterItem.apply(imageView.getImage()));

            });

        });

        if (eredetiE.get()) {
            return imageView.getImage();
        } else {
            return imageView2.getImage();
        }
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
