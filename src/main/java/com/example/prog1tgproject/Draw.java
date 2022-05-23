package com.example.prog1tgproject;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;

import java.util.NoSuchElementException;
import java.util.Stack;

public class Draw {

    private StackPane container;
    private final ImageView imageView;
    private Image image;
    private final Canvas canvasDraw;
    private final ToggleButton pencilDrawButton;
    private final ToggleButton rubberDrawButton;
    private final ToggleButton lineDrawButton;
    private final ToggleButton rectDrawButton;
    private final ToggleButton circleDrawButton;
    private final Slider pencilstrDrawSlider;
    private final ColorPicker colorDrawPicker;
    private final ToggleButton textDrawButton;
    private final Button undoDrawButton;
    private final Button redoDrawButton;
    private final Button saveDrawButton;
    private final TextField writeTextField;

    private final Stack<Shape> undoHistory = new Stack();
    private final Stack<Shape> redoHistory = new Stack();

    private final Stack<Image> undoStack = new Stack<>();
    private final Stack<Image> redoStack = new Stack<>();

    public GraphicsContext gc;

    public Draw(StackPane container, ImageView imageView, Canvas canvasDraw, ToggleButton pencilDrawButton, ToggleButton lineDrawButton, ToggleButton rectDrawButton,
                ToggleButton circleDrawButton, ToggleButton rubberDrawButton, ColorPicker colorDrawPicker, Slider pencilstrDrawSlider,
                ToggleButton textDrawButton, Button undoDrawButton, Button redoDrawButton, Button saveDrawButton, TextField writeTextField) {
        this.imageView = imageView;
        this.canvasDraw = canvasDraw;
        this.pencilDrawButton = pencilDrawButton;
        this.rubberDrawButton = rubberDrawButton;
        this.circleDrawButton = circleDrawButton;
        this.rectDrawButton = rectDrawButton;
        this.lineDrawButton = lineDrawButton;
        this.pencilstrDrawSlider = pencilstrDrawSlider;
        this.colorDrawPicker = colorDrawPicker;
        this.textDrawButton = textDrawButton;
        this.undoDrawButton = undoDrawButton;
        this.redoDrawButton = redoDrawButton;
        this.saveDrawButton = saveDrawButton;
        this.writeTextField = writeTextField;
        this.container = container;
    }

    public void initializeDraw() {
        image = imageView.getImage();
        
        double aspectRatio = image.getWidth() / image.getHeight();
        double realWidth = Math.min(imageView.getFitWidth(), imageView.getFitHeight() * aspectRatio);
        double realHeight = Math.min(imageView.getFitHeight(), imageView.getFitWidth() / aspectRatio);
        
        canvasDraw.setHeight(realHeight);
        canvasDraw.setWidth(realWidth);

        gc = canvasDraw.getGraphicsContext2D();

        //gc.setFill(Color.GOLD);
        //gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        //gc.setGlobalBlendMode(BlendMode.SCREEN);
        //gc.setGlobalAlpha(opacity);
        gc.setLineWidth(1);
        gc.drawImage(image, 0, 0, canvasDraw.getWidth(), canvasDraw.getHeight());
        gc.clearRect(0, 0, canvasDraw.getWidth(), canvasDraw.getHeight());
        canvasDraw.setVisible(true);

        Line line = new Line();
        Rectangle rect = new Rectangle();
        Circle circ = new Circle();

        canvasDraw.setOnMousePressed(e -> {
            if (pencilDrawButton.isSelected()) {
                gc.setStroke(colorDrawPicker.getValue());
                gc.beginPath();
                gc.lineTo(e.getX(), e.getY());
            } else if (rubberDrawButton.isSelected()) {
                double lineWidth = gc.getLineWidth();
                gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
            } else if (lineDrawButton.isSelected()) {
                gc.setStroke(colorDrawPicker.getValue());
                line.setStartX(e.getX());
                line.setStartY(e.getY());
            } else if (rectDrawButton.isSelected()) {
                gc.setStroke(colorDrawPicker.getValue());
                gc.setFill(Color.TRANSPARENT);
                rect.setX(e.getX());
                rect.setY(e.getY());
            } else if (circleDrawButton.isSelected()) {
                gc.setStroke(colorDrawPicker.getValue());
                gc.setFill(Color.TRANSPARENT);
                circ.setCenterX(e.getX());
                circ.setCenterY(e.getY());
            }
        });

        canvasDraw.setOnMouseDragged(e -> {
            if (pencilDrawButton.isSelected()) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            } else if (rubberDrawButton.isSelected()) {
                double lineWidth = gc.getLineWidth();
                gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
            }
        });

        canvasDraw.setOnMouseReleased(e -> {
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

                undoHistory.push(new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY()));
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

                undoHistory.push(new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));

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

                undoHistory.push(new Circle(circ.getCenterX(), circ.getCenterY(), circ.getRadius()));
            } else if (textDrawButton.isSelected()) {
                gc.setLineWidth(1);
                gc.setFont(Font.font(pencilstrDrawSlider.getValue()));
                gc.setStroke(colorDrawPicker.getValue());
                gc.setFill(Color.TRANSPARENT);
                gc.fillText(writeTextField.getText(), e.getX(), e.getY());
                gc.strokeText(writeTextField.getText(), e.getX(), e.getY());
            }

            try {
                redoHistory.clear();
                Shape lastUndo = undoHistory.lastElement();
                lastUndo.setFill(gc.getFill());
                lastUndo.setStroke(gc.getStroke());
                lastUndo.setStrokeWidth(gc.getLineWidth());
            } catch (NoSuchElementException ex){

            }
        });

        // Color picker
        colorDrawPicker.setOnAction(e -> {
            gc.setStroke(colorDrawPicker.getValue());
        });

        // Pencil strength slider
        pencilstrDrawSlider.valueProperty().addListener(e -> {
            double width = pencilstrDrawSlider.getValue();
            if (textDrawButton.isSelected()) {
                gc.setLineWidth(1);
                gc.setFont(Font.font(pencilstrDrawSlider.getValue()));
                return;
            }
            gc.setLineWidth(width);
        });

        //Undo Button
        undoDrawButton.setOnAction(e -> {
            if (!undoHistory.empty()) {
                gc.clearRect(0, 0, 1080, 790);
                Shape removedShape = undoHistory.lastElement();
                if (removedShape.getClass() == Line.class) {
                    Line tempLine = (Line) removedShape;
                    tempLine.setFill(gc.getFill());
                    tempLine.setStroke(gc.getStroke());
                    tempLine.setStrokeWidth(gc.getLineWidth());
                    redoHistory.push(new Line(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY()));

                } else if (removedShape.getClass() == Rectangle.class) {
                    Rectangle tempRect = (Rectangle) removedShape;
                    tempRect.setFill(gc.getFill());
                    tempRect.setStroke(gc.getStroke());
                    tempRect.setStrokeWidth(gc.getLineWidth());
                    redoHistory.push(new Rectangle(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight()));
                } else if (removedShape.getClass() == Circle.class) {
                    Circle tempCirc = (Circle) removedShape;
                    tempCirc.setStrokeWidth(gc.getLineWidth());
                    tempCirc.setFill(gc.getFill());
                    tempCirc.setStroke(gc.getStroke());
                    redoHistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
                } else if (removedShape.getClass() == Ellipse.class) {
                    Ellipse tempElps = (Ellipse) removedShape;
                    tempElps.setFill(gc.getFill());
                    tempElps.setStroke(gc.getStroke());
                    tempElps.setStrokeWidth(gc.getLineWidth());
                    redoHistory.push(new Ellipse(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY()));
                }
                Shape lastRedo = redoHistory.lastElement();
                lastRedo.setFill(removedShape.getFill());
                lastRedo.setStroke(removedShape.getStroke());
                lastRedo.setStrokeWidth(removedShape.getStrokeWidth());
                undoHistory.pop();

                for (int i = 0; i < undoHistory.size(); i++) {
                    Shape shape = undoHistory.elementAt(i);
                    if (shape.getClass() == Line.class) {
                        Line temp = (Line) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.strokeLine(temp.getStartX(), temp.getStartY(), temp.getEndX(), temp.getEndY());
                    } else if (shape.getClass() == Rectangle.class) {
                        Rectangle temp = (Rectangle) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.fillRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                        gc.strokeRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                    } else if (shape.getClass() == Circle.class) {
                        Circle temp = (Circle) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                        gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                    } else if (shape.getClass() == Ellipse.class) {
                        Ellipse temp = (Ellipse) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                        gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                    }
                }
            } else {
                System.out.println("Minden visszaállítva");
            }
        });

        // Redo Button
        redoDrawButton.setOnAction(e -> {
            if (!redoHistory.empty()) {
                Shape shape = redoHistory.lastElement();
                gc.setLineWidth(shape.getStrokeWidth());
                gc.setStroke(shape.getStroke());
                gc.setFill(shape.getFill());

                redoHistory.pop();
                if (shape.getClass() == Line.class) {
                    Line tempLine = (Line) shape;
                    gc.strokeLine(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY());
                    undoHistory.push(new Line(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY()));
                } else if (shape.getClass() == Rectangle.class) {
                    Rectangle tempRect = (Rectangle) shape;
                    gc.fillRect(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight());
                    gc.strokeRect(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight());

                    undoHistory.push(new Rectangle(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight()));
                } else if (shape.getClass() == Circle.class) {
                    Circle tempCirc = (Circle) shape;
                    gc.fillOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());
                    gc.strokeOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());

                    undoHistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
                } else if (shape.getClass() == Ellipse.class) {
                    Ellipse tempElps = (Ellipse) shape;
                    gc.fillOval(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY());
                    gc.strokeOval(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY());

                    undoHistory.push(new Ellipse(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY()));
                }
                Shape lastUndo = undoHistory.lastElement();
                lastUndo.setFill(gc.getFill());
                lastUndo.setStroke(gc.getStroke());
                lastUndo.setStrokeWidth(gc.getLineWidth());

            } else {
                System.out.println("Minden helyrehozva");
            }
        });

        // Save
        /*saveDrawButton.setOnAction((e)->{
            FileChooser savefile = new FileChooser();
            //new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg");
            savefile.setTitle("Save File");

            File file = savefile.showSaveDialog(imageView.getScene().getWindow());
            if (file != null) {
                try {
                    WritableImage writableImage = new WritableImage((int)canvasDraw.getWidth(), (int)canvasDraw.getHeight());
                    canvasDraw.snapshot(null, writableImage);
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ImageIO.write(renderedImage, "png", file);
                } catch (IOException ex) {
                    System.out.println("Error!");
                }
            }

        });*/

        /*saveDrawButton.setOnAction((e)-> {
            FileChooser savefile = new FileChooser();
            //new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg");
            savefile.setTitle("Save File");

            File file = savefile.showSaveDialog(imageView.getScene().getWindow());
            if (file != null) {
                try {
                    WritableImage writableImage = new WritableImage((int) canvasDraw.getWidth(), (int) canvasDraw.getHeight());
                    container.snapshot(new SnapshotParameters(), writableImage);
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ImageIO.write(renderedImage, "png", file);
                } catch (IOException ex) {
                    System.out.println("Error!");
                }
            }
        });*/

    }
}
