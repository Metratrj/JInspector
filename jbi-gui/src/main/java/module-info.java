module xyz.metratrj.jbyteinspector.gui {
    requires javafx.controls;
    requires javafx.fxml;

    opens xyz.metratrj.jbyteinspector.gui to javafx.fxml;
    exports xyz.metratrj.jbyteinspector.gui;
}