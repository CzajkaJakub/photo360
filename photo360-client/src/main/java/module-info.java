module pl.put.photo360 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens pl.put.photo360 to javafx.fxml;
    exports pl.put.photo360.controller;
    opens pl.put.photo360.controller to javafx.fxml;
    exports pl.put.photo360;
}