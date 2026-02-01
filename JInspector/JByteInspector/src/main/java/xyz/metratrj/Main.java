package xyz.metratrj;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends Application {
    static void main(String[] args) {
        System.out.println("Hello, World!");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion);
        StackPane pane = new StackPane(l);
        Scene scene = new Scene(pane, 640, 480);



        MenuItem exit_item = new MenuItem("Exit");

        exit_item.setOnAction(actionEvent -> {
            System.exit(0);
        });

        Menu menu = new Menu("File", null, exit_item);
        MenuBar menuBar = new MenuBar(menu);
        Group group = new Group(menuBar, pane);

        scene.setRoot(group);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
