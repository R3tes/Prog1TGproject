package com.example.prog1tgproject;

import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class Filters extends Home {

    private final MenuButton filterButton;
    private final ImageChanger imageChanger;
    private final ImageView imageView;
    private Image original;

    public Filters(MenuButton filterButton, ImageView imageView, ImageChanger imageChanger) {
        this.filterButton = filterButton;
        this.imageView = imageView;
        this.imageChanger = imageChanger;
        createFilter();
    }

    private List<Filter> filters = Arrays.asList(
            new Filter("Inverz", Color::invert), new Filter("Szürkeárnyalat", Color::grayscale),
            new Filter("Fekete-fehér", c -> valueOf(c) < 1.5 ? Color.BLACK : Color.WHITE),
            new Filter("Vörös", c -> Color.color(1.0, c.getGreen(), c.getBlue())),
            new Filter("Zöld", c -> Color.color(c.getRed(), 1.0, c.getBlue())),
            new Filter("Kék", c -> Color.color(c.getRed(), c.getGreen(), 1.0)),
            new Filter("Eredeti", c -> Color.color(c.getRed(), c.getGreen(), c.getBlue()))
    );

    public void setOriginal(Image original) {
        this.original = original;
    }

    private double valueOf(Color c) {
        return c.getRed() + c.getGreen() + c.getBlue();
    }

    public void createFilter() {
        original = imageView.getImage();
        filters.forEach(filterItem -> {
            MenuItem item = new MenuItem(filterItem.name);
            filterButton.getItems().add(item);
            if (Objects.equals(item.getText(), "Eredeti")) {
                imageChanger.endSlideShow();
                imageView.setImage(original);
            }

            item.setOnAction(e -> {
                imageChanger.endSlideShow();

                imageView.setImage(filterItem.apply(original));
            });
        });
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
            WritableImage image = null;

            try {
                int imgWidth = (int) source.getWidth();
                int imgHeight = (int) source.getHeight();

                image = new WritableImage(imgWidth, imgHeight);

                for (int y = 0; y < imgHeight; y++) {
                    for (int x = 0; x < imgWidth; x++) {
                        Color c1 = source.getPixelReader().getColor(x, y);
                        Color c2 = colorMap.apply(c1);

                        image.getPixelWriter().setColor(x, y, c2);
                    }
                }

            } catch (NullPointerException ex) {

            } finally {
                return image;
            }
        }
    }

}
