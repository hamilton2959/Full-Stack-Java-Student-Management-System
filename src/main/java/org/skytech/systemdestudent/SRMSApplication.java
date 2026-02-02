package org.skytech.systemdestudent;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.skytech.systemdestudent.config.DatabaseConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SRMSApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private Parent root;

    public static void main(String[] args) {
        // Initialize database before launching JavaFX
        System.out.println("Initializing database...");
        DatabaseConfig.initializeDatabase();
        System.out.println("Database initialization complete!");

        // Launch JavaFX application
        launch(args);
    }

    @Override
    public void init() throws Exception {
        // Initialize Spring context
        springContext = SpringApplication.run(SRMSApplication.class);

        // Load FXML with Spring context
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        root = fxmlLoader.load();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Student Records Management System");

        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        // Close Spring context on application exit
        springContext.close();
    }
}