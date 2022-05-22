module com.example.prog1tgproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;


    opens com.example.prog1tgproject to javafx.fxml;
    exports com.example.prog1tgproject;
    exports com.example.prog1tgproject.plugins;
    opens com.example.prog1tgproject.plugins to javafx.fxml;
}