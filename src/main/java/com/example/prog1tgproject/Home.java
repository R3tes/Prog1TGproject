package com.example.prog1tgproject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Home {
    @FXML
    private ImageView imageView;

    @FXML
    private ToolBar toolBar;

    @FXML
    private MenuButton opButton;

    @FXML
    private void initialize(){
        File file = new File("C:\\Users\\Benec\\OneDrive\\Asztali gép\\KedvesFerenc.jpg");
        Image image = new Image(file.toURI().toString());
        imageView.setImage(image);
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

        
    }
    
    private void loadPlugins(){
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
        for (Plugin plugin :
                plugins) {
            Button button = new Button(plugin.getName());
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    plugin.process(imageView);
                }
            });
            toolBar.getItems().add(button);
        }
    }
}