package xyz.metratrj.jbyteinspectorgui;

import javafx.beans.property.SimpleStringProperty;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import xyz.metratrj.jbyteinspector.api.AnalysisService;
import xyz.metratrj.jbyteinspector.core.JByteInspectorEngine;
import xyz.metratrj.jbyteinspector.model.*;

import java.io.File;
import java.util.List;

public class MainController {

    @FXML
    public Button expandAll;
    @FXML
    public Button collapseAll;
    @FXML
    public TextField filterField;
    @FXML
    public ToolBar resourceToolbar;
    @FXML
    public Color x4;

    private int loadedResources = 0;

    @FXML
    public Label resourceCountLabel;
    @FXML
    private TabPane tabs;

    @FXML
    private TreeView<ResourceItem> resourceTree;

    private void expandTreeView(TreeItem<?> item){
        if (item != null && !item.isLeaf()){
            item.setExpanded(true);
            for (TreeItem<?> child:item.getChildren()){
                expandTreeView(child);
            }
        }
    }

    private void collapseTreeView(TreeItem<?> item){
        if (item != null && !item.isLeaf()){
            item.setExpanded(false);
            for (TreeItem<?> child:item.getChildren()){
                expandTreeView(child);
            }
        }
    }

    public void expandAll(ActionEvent actionEvent) {
        expandTreeView(resourceTree.getRoot());
    }

    public void collapseAll(ActionEvent actionEvent) {
        collapseTreeView(resourceTree.getRoot());
    }


    private static record ResourceItem(ClassReport cr, MethodReport mr, String name){}


    private static final String BG_DARK        = "#ececec";
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

    public void addDirectory(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Add Directory");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory == null)
            return;
        System.out.println("Selected directory: " + selectedDirectory.getAbsolutePath());

        AnalysisService analysisService = new JByteInspectorEngine();
        List<ClassReport> reports = analysisService.analyze(selectedDirectory.toPath());
        addReportsToTree(reports, selectedDirectory.getAbsolutePath());
    }

    @FXML
    public void addResource(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open .jar/.class File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Java .jar/.class Files", "*.class", "*.jar"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (null == selectedFile) {
            return;
        }

        System.out.println("Selected file: " + selectedFile.getAbsolutePath());

        AnalysisService analysisService = new JByteInspectorEngine();
        List<ClassReport> reports = analysisService.analyze(selectedFile.toPath());
        addReportsToTree(reports, selectedFile.getAbsolutePath());
    }

    private void addReportsToTree(List<ClassReport> reports, String path) {
        TreeItem<ResourceItem> rootItem = resourceTree.getRoot();
        if (rootItem == null) {
            rootItem = new TreeItem<>();
            rootItem.setExpanded(true);
            resourceTree.setRoot(rootItem);
        }

        TreeItem<ResourceItem> reportRoot = new TreeItem<>(new ResourceItem(null, null, path));


        for (ClassReport report : reports) {
            System.out.println(report.className());
            TreeItem<ResourceItem> branch = new TreeItem<>(new ResourceItem(report, null, report.className()));
            report.methods().forEach(method -> {
                TreeItem<ResourceItem> leaf = new TreeItem<>(new ResourceItem(report, method, method.name()));
                branch.getChildren().add(leaf);
            });

            reportRoot.getChildren().add(branch);
        }

        rootItem.getChildren().add(reportRoot);
        loadedResources++;

        resourceCountLabel.setText(loadedResources + " Resources");

        configureTreeCellFactory();
    }

    private void configureTreeCellFactory() {
        resourceTree.setCellFactory(param -> {
            TreeCell<ResourceItem> cell =  new TreeCell<>(){
                @Override
                protected void updateItem(ResourceItem item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null ) {
                        setText(null);
                    } else {
                        setText(item.name());
                    }
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !cell.isEmpty()) {
                    ResourceItem ri = cell.getItem();
                    if (ri == null || ri.mr() == null) return;

                    MethodReport mr = ri.mr();
                    ClassReport cr = ri.cr();

                    String tabTitle = cr.className().substring(cr.className().lastIndexOf('/') + 1) + "." + mr.name();
                    Tab tab = new Tab(tabTitle);

                    SplitPane split = new SplitPane();
                    split.setDividerPositions(0.8);

                    // Left: Bytecode instructions
                    VBox leftPane = new VBox(10);
                    leftPane.setPadding(new Insets(10));
                    //leftPane.setStyle("-fx-background-color: " + BG_DARK + ";");

                    HBox toolbar = new HBox(10);
                    toolbar.setAlignment(Pos.CENTER_LEFT);

                    Label methodLabel = new Label(mr.name() + mr.descriptor());
                    methodLabel.setTextFill(Color.web(ACCENT_GREEN));
                    methodLabel.setStyle("-fx-font-weight: bold; -fx-font-family: 'Monospaced';");

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    toolbar.getChildren().addAll(methodLabel, spacer);

                    ObservableList<BytecodeInstruction> instructions = FXCollections.observableArrayList();

                    if (mr.code() != null && mr.code().instructions() != null) {
                        for (InstructionReport ir : mr.code().instructions()) {
                            String args = String.join(", ", ir.operands());
                            if (!ir.resolvedComment().isEmpty()) {
                                args += " // " + ir.resolvedComment();
                            }
                            instructions.add(new BytecodeInstruction(
                                String.format("%04d", ir.pc()),
                                ir.mnemonic(),
                                args
                            ));
                        }
                    }

                    TableView<BytecodeInstruction> table = new TableView<>(instructions);
                    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
                    VBox.setVgrow(table, Priority.ALWAYS);
                    //table.setStyle("-fx-control-inner-background: " + BG_PANEL + "; -fx-text-fill: " + TEXT_PRIMARY + ";");

                    TableColumn<BytecodeInstruction, String> offsetCol = new TableColumn<>("Offset");
                    offsetCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().offset()));
                    offsetCol.setPrefWidth(40);
                    offsetCol.setMaxWidth(40);
                    offsetCol.setMinWidth(40);

                    TableColumn<BytecodeInstruction, String> mnemonicCol = new TableColumn<>("Mnemonic");
                    mnemonicCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().mnemonic()));
                    mnemonicCol.setPrefWidth(120);

                    TableColumn<BytecodeInstruction, String> argsCol = new TableColumn<>("Description / Arguments");
                    argsCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().arguments()));

                    table.getColumns().addAll(offsetCol, mnemonicCol, argsCol);

                    leftPane.getChildren().addAll(toolbar, table);

                    // Right pane for attributes or details
                    TabPane detailsTabPane = new TabPane();
                    VBox.setVgrow(detailsTabPane, Priority.ALWAYS);

                    // Tab 1: Basic Details
                    Tab basicDetailsTab = new Tab("General");
                    basicDetailsTab.setClosable(false);
                    VBox basicDetailsPane = new VBox(10);
                    basicDetailsPane.setPadding(new Insets(10));

                    if (mr.code() != null) {
                        Label stackLabel = new Label("Max Stack: " + mr.code().maxStack());
                        Label localsLabel = new Label("Max Locals: " + mr.code().maxLocals());
                        Label lengthLabel = new Label("Code Length: " + mr.code().codeLength());

                        stackLabel.setTextFill(Color.web(TEXT_SECONDARY));
                        localsLabel.setTextFill(Color.web(TEXT_SECONDARY));
                        lengthLabel.setTextFill(Color.web(TEXT_SECONDARY));

                        basicDetailsPane.getChildren().addAll(stackLabel, localsLabel, lengthLabel);
                    }
                    basicDetailsTab.setContent(basicDetailsPane);

                    // Tab 2: Exception Table
                    Tab exceptionTab = new Tab("Exceptions");
                    exceptionTab.setClosable(false);
                    if (mr.code() != null && mr.code().exceptionTable() != null && mr.code().exceptionTable().length > 0) {
                        TableView<ExceptionTableEntry> exceptionTable = new TableView<>(FXCollections.observableArrayList(mr.code().exceptionTable()));
                        exceptionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

                        TableColumn<ExceptionTableEntry, String> startCol = new TableColumn<>("Start");
                        startCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().startPc())));

                        TableColumn<ExceptionTableEntry, String> endCol = new TableColumn<>("End");
                        endCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().endPc())));

                        TableColumn<ExceptionTableEntry, String> handlerCol = new TableColumn<>("Handler");
                        handlerCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().handlerPc())));

                        TableColumn<ExceptionTableEntry, String> typeCol = new TableColumn<>("Catch Type");
                        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().catchType()));

                        exceptionTable.getColumns().addAll(startCol, endCol, handlerCol, typeCol);
                        exceptionTab.setContent(exceptionTable);
                    } else {
                        Label noExcLabel = new Label("No exception handlers defined.");
                        noExcLabel.setPadding(new Insets(10));
                        noExcLabel.setTextFill(Color.web(TEXT_SECONDARY));
                        exceptionTab.setContent(noExcLabel);
                    }

                    // Tab 3: Local Variable Table
                    Tab lvtTab = new Tab("Local Variables");
                    lvtTab.setClosable(false);
                    if (mr.code() != null && mr.code().localVariableTable() != null && !mr.code().localVariableTable().isEmpty()) {
                        TableView<LocalVariableEntry> lvtTable = new TableView<>(FXCollections.observableArrayList(mr.code().localVariableTable()));
                        lvtTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

                        TableColumn<LocalVariableEntry, String> startCol = new TableColumn<>("Start");
                        startCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().startPc())));

                        TableColumn<LocalVariableEntry, String> lenCol = new TableColumn<>("Length");
                        lenCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().length())));

                        TableColumn<LocalVariableEntry, String> nameCol = new TableColumn<>("Name");
                        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name()));

                        TableColumn<LocalVariableEntry, String> descCol = new TableColumn<>("Descriptor");
                        descCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().descriptor()));

                        TableColumn<LocalVariableEntry, String> indexCol = new TableColumn<>("Index");
                        indexCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().index())));

                        lvtTable.getColumns().addAll(startCol, lenCol, nameCol, descCol, indexCol);
                        lvtTab.setContent(lvtTable);
                    } else {
                        Label noLvtLabel = new Label("No local variable table available.");
                        noLvtLabel.setPadding(new Insets(10));
                        noLvtLabel.setTextFill(Color.web(TEXT_SECONDARY));
                        lvtTab.setContent(noLvtLabel);
                    }

                    detailsTabPane.getTabs().addAll(basicDetailsTab, exceptionTab, lvtTab);

                    split.getItems().addAll(leftPane, detailsTabPane);
                    tab.setContent(split);
                    tabs.getTabs().add(tab);
                    tabs.getSelectionModel().select(tab);
                }
            });
            return cell;
        });
    }

    @FXML
    public void closeApp(ActionEvent actionEvent) {
        System.exit(0);
    }

    public static record BytecodeInstruction(String offset, String mnemonic, String arguments){}
}
