package com.example.prog1tgproject;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

public class Draw {

    private ImageView imageView;
    private Image image;
    private Canvas canvas;
    ToggleButton pencilDrawButton;
    ToggleButton rubberDrawButton;
    ToggleButton lineDrawButton;
    ToggleButton rectDrawButton;
    ToggleButton circleDrawButton;
    Slider pencilstrDrawSlider;
    ColorPicker colorDrawPicker;

    public Draw(ImageView imageView, Canvas canvas, ToggleButton pencilDrawButton, ToggleButton lineDrawButton, ToggleButton rectDrawButton,
                ToggleButton circleDrawButton, ToggleButton rubberDrawButton, ColorPicker colorDrawPicker, Slider pencilstrDrawSlider) {
        this.imageView = imageView;
        this.canvas = canvas;
        this.pencilDrawButton = pencilDrawButton;
        this.rubberDrawButton = rubberDrawButton;
        this.circleDrawButton = circleDrawButton;
        this.rectDrawButton = rectDrawButton;
        this.lineDrawButton = lineDrawButton;
        this.pencilstrDrawSlider = pencilstrDrawSlider;
        this.colorDrawPicker = colorDrawPicker;
    }

    public void initializeDraw() {
        image=imageView.getImage();

        ColorPicker cpLine = new ColorPicker(Color.BLACK);
        Slider pencilstrDrawSlider=new Slider();

        canvas.setHeight(imageView.getFitHeight());
        canvas.setWidth(imageView.getFitWidth());
        GraphicsContext gc;
        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);

        Line line = new Line();
        Rectangle rect = new Rectangle();
        Circle circ = new Circle();

        canvas.setOnMousePressed(e -> {
            if (pencilDrawButton.isSelected()) {
                gc.setStroke(cpLine.getValue());
                gc.beginPath();
                gc.lineTo(e.getX(), e.getY());
            } else if (rubberDrawButton.isSelected()) {
                double lineWidth = gc.getLineWidth();
                gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
            } else if (lineDrawButton.isSelected()) {
                gc.setStroke(cpLine.getValue());
                line.setStartX(e.getX());
                line.setStartY(e.getY());
            } else if (rectDrawButton.isSelected()) {
                gc.setStroke(cpLine.getValue());
                //gc.setFill(cpFill.getValue());
                rect.setX(e.getX());
                rect.setY(e.getY());
            } else if (circleDrawButton.isSelected()) {
                gc.setStroke(cpLine.getValue());
                //gc.setFill(cpFill.getValue());
                circ.setCenterX(e.getX());
                circ.setCenterY(e.getY());
            }
        });

        canvas.setOnMouseDragged(e -> {
            if (pencilDrawButton.isSelected()) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            } else if (rubberDrawButton.isSelected()) {
                double lineWidth = gc.getLineWidth();
                gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
            }
        });

        canvas.setOnMouseReleased(e -> {
            if (pencilDrawButton.isSelected()) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
                gc.closePath();
            } else if (rubberDrawButton.isSelected()) {
                double lineWidth = gc.getLineWidth();
                gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
            } else if (lineDrawButton.isSelected()) {
                line.setEndX(e.getX());
                line.setEndY(e.getY());
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());

                //undoHistory.push(new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY()));
            } else if (rectDrawButton.isSelected()) {
                rect.setWidth(Math.abs((e.getX() - rect.getX())));
                rect.setHeight(Math.abs((e.getY() - rect.getY())));
                //rect.setX((rect.getX() > e.getX()) ? e.getX(): rect.getX());
                if (rect.getX() > e.getX()) {
                    rect.setX(e.getX());
                }
                //rect.setY((rect.getY() > e.getY()) ? e.getY(): rect.getY());
                if (rect.getY() > e.getY()) {
                    rect.setY(e.getY());
                }

                gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

                //undoHistory.push(new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));

            } else if (circleDrawButton.isSelected()) {
                circ.setRadius((Math.abs(e.getX() - circ.getCenterX()) + Math.abs(e.getY() - circ.getCenterY())) / 2);

                if (circ.getCenterX() > e.getX()) {
                    circ.setCenterX(e.getX());
                }
                if (circ.getCenterY() > e.getY()) {
                    circ.setCenterY(e.getY());
                }

                gc.fillOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                gc.strokeOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());

                //undoHistory.push(new Circle(circ.getCenterX(), circ.getCenterY(), circ.getRadius()));
            }
            /*redoHistory.clear();
            Shape lastUndo = undoHistory.lastElement();
            lastUndo.setFill(gc.getFill());
            lastUndo.setStroke(gc.getStroke());
            lastUndo.setStrokeWidth(gc.getLineWidth());*/

        });

        // Color picker
        cpLine.setOnAction(e -> {
            gc.setStroke(cpLine.getValue());
        });
        /*cpFill.setOnAction(e -> {
            gc.setFill(cpFill.getValue());
        })

        // Pencil strength slider
        pencilstrDrawSlider.valueProperty().addListener(e -> {
            double width = pencilstrDrawSlider.getValue();
            if (textbtn.isSelected()) {
                gc.setLineWidth(1);
                gc.setFont(Font.font(slider.getValue()));
                line_width.setText(String.format("%.1f", width));
                return;
            }
            //line_width.setText(String.format("%.1f", width));
            gc.setLineWidth(width);
        });*/

    }
}
