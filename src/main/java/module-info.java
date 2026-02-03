module org.skytech.systemdestudent {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires spring.context;
    requires spring.beans;
    requires spring.boot.autoconfigure;
    requires spring.boot;

    opens org.skytech.systemdestudent to javafx.fxml;
    opens org.skytech.systemdestudent.config to spring.core, spring.beans, spring.context;
    exports org.skytech.systemdestudent;
}