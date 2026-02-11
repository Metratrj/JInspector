package xyz.metratrj.jbyteinspectorgui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("JByteInspector Version 0.1.0-SNAPSHOT");
        stage.setScene(scene);
        stage.show();

        /*


        MenuBar  menuBar         = new MenuBar();
        Menu     menu            = new Menu("File");
        MenuItem openMenuItem    = new MenuItem("Add Resource");
        MenuItem reloadResources = new MenuItem("Reload Resources");
        MenuItem exitMenuItem    = new MenuItem("Exit");

        menu.getItems().add(openMenuItem);
        menu.getItems().add(reloadResources);
        menu.getItems().add(exitMenuItem);
        menuBar.getMenus().add(menu);


        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menuBar);

        Label     label = new Label("Hello, JavaFX!");
        StackPane root  = new StackPane(label);
        borderPane.setCenter(root);


        TreeView treeView = new TreeView<>();
        borderPane.setLeft(treeView);


        AnchorPane anchorPane = new AnchorPane(borderPane);

        Scene scene = new Scene(anchorPane, 800, 600);

        stage.setScene(scene);
        stage.setTitle("JByteInspector GUI");*/
    }

    public static void main(String[] args) {
        launch(args);
    }
}
