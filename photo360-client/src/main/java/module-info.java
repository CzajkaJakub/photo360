module pl.photo.gui.photo360gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires com.fasterxml.jackson.databind;
    requires spring.context;
    requires spring.beans;
    requires lombok;

    opens pl.put.photo360 to javafx.fxml;
    exports pl.put.photo360;
    exports pl.put.photo360.controllers;
    exports pl.put.photo360.web to com.fasterxml.jackson.databind;
    opens pl.put.photo360.controllers to javafx.fxml;
}