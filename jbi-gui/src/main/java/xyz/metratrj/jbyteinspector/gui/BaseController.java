package xyz.metratrj.jbyteinspector.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;

import java.io.File;

public class BaseController {
    @FXML
    public void expandAll(ActionEvent actionEvent) {
    }

    @FXML
    public void collapseAll(ActionEvent actionEvent) {
    }

    @FXML
    public void openResource(ActionEvent actionEvent) {
    }

    @FXML
    public void openInNewTab(ActionEvent actionEvent) {
    }

    @FXML
    public void removeResource(ActionEvent actionEvent) {
    }

    @FXML
    public void reloadResource(ActionEvent actionEvent) {
    }

    public void exportAsJava(ActionEvent actionEvent) {
    }

    public void exportAsBytecode(ActionEvent actionEvent) {
    }

    public void exportAsSmali(ActionEvent actionEvent) {
    }

    public void copyPath(ActionEvent actionEvent) {
    }

    public void copyFqn(ActionEvent actionEvent) {
    }

    @FXML
    public void closeWindow(ActionEvent actionEvent) {
        System.exit(0);
    }

    @FXML
    public void selectFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open .jar/.class File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Java .jar/.class Files", "*.class", "*.jar"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }
    }
}
