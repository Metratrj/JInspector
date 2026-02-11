package xyz.metratrj.jbyteinspectorgui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import xyz.metratrj.jbyteinspector.api.AnalysisService;
import xyz.metratrj.jbyteinspector.core.JByteInspectorEngine;
import xyz.metratrj.jbyteinspector.model.ClassReport;

import java.io.File;
import java.util.List;

public class MainController {

    @FXML
    private TabPane tabs;

    @FXML
    private TreeView<ResourceItem> resourceTree;

    private static record ResourceItem(ClassReport cr, String name){}


    private static final String BG_DARK        = "#0d1117";
    private static final String BG_PANEL       = "#161b22";
    private static final String BG_CARD        = "#21262d";
    private static final String BORDER_COLOR   = "#30363d";
    private static final String TEXT_PRIMARY   = "#e6edf3";
    private static final String TEXT_SECONDARY = "#7d8590";
    private static final String ACCENT_BLUE    = "#58a6ff";
    private static final String ACCENT_GREEN   = "#3fb950";
    private static final String ACCENT_RED     = "#f85149";
    private static final String ACCENT_ORANGE  = "#d29922";
    private static final String ACCENT_EXTRA   = "#a371f7";


    @FXML
    public void addResource(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open .jar/.class File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Java .jar/.class Files", "*.class", "*.jar"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());

            AnalysisService analysisService = new JByteInspectorEngine();
            List<ClassReport> reports = analysisService.analyze(selectedFile.toPath());
            TreeItem<ResourceItem> rootItem = new TreeItem<>();
            rootItem.setExpanded(true);

            for (ClassReport report : reports) {
                System.out.println(report.className());
                TreeItem<ResourceItem> branch = new TreeItem<>(new ResourceItem(report, report.className()));
                report.methods().forEach(method -> {
                    TreeItem<ResourceItem> leaf = new TreeItem<>(new ResourceItem(report, method.name()));
                    branch.getChildren().add(leaf);
                });

                rootItem.getChildren().add(branch);
            }
            resourceTree.setRoot(rootItem);

            resourceTree.setCellFactory(param -> {
                TreeCell<ResourceItem> cell =  new TreeCell<ResourceItem>(){
                    @Override
                    protected void updateItem(ResourceItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null ) {
                            setText(null);
                            setStyle("");
                        }else {
                            setText(item.name());
                        }
                    }
                };

                cell.setOnMouseClicked(event -> {
                    ResourceItem ri = cell.getItem();
                    if (ri == null) return;
                    ClassReport cr = ri.cr();
                    Tab tab = new Tab(cr.className());

                    SplitPane split = new SplitPane();
                    split.setDividerPositions(0.5);

                    // Left: Bytecode instructions
                    VBox leftPane = new VBox(10);
                    leftPane.setPadding(new Insets(10));

                    HBox toolbar = new HBox(10);
                    toolbar.setAlignment(Pos.CENTER_LEFT);

                    ToggleGroup viewGroup   = new ToggleGroup();
                    RadioButton mnemonicBtn = new RadioButton("Bytecode Mnemonics");
                    mnemonicBtn.setToggleGroup(viewGroup);
                    mnemonicBtn.setSelected(true);
                    mnemonicBtn.setTextFill(Color.web(TEXT_PRIMARY));

                    RadioButton hexBtn = new RadioButton("Hex View");
                    hexBtn.setToggleGroup(viewGroup);
                    hexBtn.setTextFill(Color.web(TEXT_PRIMARY));

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Label offsetLabel = new Label("Offsets");
                    offsetLabel.setTextFill(Color.web(ACCENT_BLUE));
                    offsetLabel.setUnderline(true);

                    toolbar.getChildren().addAll(mnemonicBtn, hexBtn, spacer, offsetLabel);


                    ObservableList<BytecodeInstruction> instructions = FXCollections.observableArrayList();

                    cr.methods().forEach(methodReport -> {

                    });

                    TableView<BytecodeInstruction>      table = new TableView<>(instructions);

                    leftPane.getChildren().addAll(toolbar, table);

                    split.getItems().addAll(leftPane);

                    tab.setContent(split);

                    tabs.getTabs().add(tab);
                });
                return cell;
            });
        }
    }

    @FXML
    public void closeApp(ActionEvent actionEvent) {
        System.exit(0);
    }

    public static record BytecodeInstruction(String offset, String mnemonic, String arguments){}
}
