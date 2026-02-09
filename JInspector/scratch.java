package xyz.metratrj.gpgpu.downloaderapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.jar.*;

public class HelloApplication extends Application {

    private Stage                               primaryStage;
    private BorderPane                          root;
    private TreeView<FileNode>                  projectTree;
    private ObservableList<BytecodeInstruction> instructions = FXCollections.observableArrayList();
    private ObservableList<RiskItem>            riskItems    = FXCollections.observableArrayList();
    private TextArea                            decompiledArea;
    private Label                               statusLabel;
    private ProgressBar                         progressBar;

    // Statistics
    private IntegerProperty totalClasses  = new SimpleIntegerProperty(0);
    private IntegerProperty totalMethods  = new SimpleIntegerProperty(0);
    private DoubleProperty  avgComplexity = new SimpleDoubleProperty(0);
    private IntegerProperty securityHits  = new SimpleIntegerProperty(0);

    // Dark theme colors
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

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        root              = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_DARK + ";");

        // Create menu bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        // Create main content
        SplitPane mainSplit = new SplitPane();
        mainSplit.setDividerPositions(0.2, 0.8);
        mainSplit.setStyle("-fx-background-color: " + BG_DARK + ";");

        // Left sidebar
        VBox sidebar = createSidebar();
        mainSplit.getItems().add(sidebar);

        // Center content with tab pane
        TabPane centerTabs = createCenterTabs();
        mainSplit.getItems().add(centerTabs);

        root.setCenter(mainSplit);

        // Bottom status bar
        HBox statusBar = createStatusBar();
        root.setBottom(statusBar);

        Scene scene = new Scene(root, 1400, 900);
        scene.getStylesheets().add(getClass().getResource("/dark-theme.css") != null ?
                                   getClass().getResource("/dark-theme.css").toExternalForm() : "");

        // Apply inline styles if CSS file not found
        applyInlineStyles();

        stage.setTitle("Bytecode Analyzer Pro v2.0");
        stage.setScene(scene);
        stage.show();
    }

    private void applyInlineStyles() {
        // Fallback inline styles
        root.lookupAll(".menu-bar").forEach(node ->
                                                    node.setStyle("-fx-background-color: " + BG_PANEL + "; -fx-border-color: " + BORDER_COLOR + ";"));
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: " + BG_PANEL + "; -fx-border-color: " + BORDER_COLOR + ";");

        Menu     fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem("Open JAR/Class...");
        openItem.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        openItem.setOnAction(e -> openFile());

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> Platform.exit());

        fileMenu.getItems().addAll(openItem, new SeparatorMenuItem(), exitItem);

        Menu     analysisMenu = new Menu("Analysis");
        MenuItem scanItem     = new MenuItem("Re-Scan");
        MenuItem reportItem   = new MenuItem("Generate Report");
        analysisMenu.getItems().addAll(scanItem, reportItem);

        Menu     viewMenu      = new Menu("View");
        MenuItem dashboardItem = new MenuItem("Dashboard");
        MenuItem bytecodeItem  = new MenuItem("Bytecode View");
        viewMenu.getItems().addAll(dashboardItem, bytecodeItem);

        menuBar.getMenus().addAll(fileMenu, analysisMenu, viewMenu);
        return menuBar;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-background-color: " + BG_PANEL + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 1 0 0;");
        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(200);

        // Search box
        TextField searchField = new TextField();
        searchField.setPromptText("Search classes or methods...");
        searchField.setStyle("-fx-background-color: " + BG_CARD + "; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-prompt-text-fill: " + TEXT_SECONDARY + ";");

        // Project Explorer header
        Label explorerLabel = new Label("📁 Project Explorer");
        explorerLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        explorerLabel.setTextFill(Color.web(TEXT_PRIMARY));

        // Tree view for project structure
        TreeItem<FileNode> rootItem = new TreeItem<>(new FileNode("No project loaded", null, FileNodeType.ROOT));
        projectTree = new TreeView<>(rootItem);
        projectTree.setShowRoot(false);
        projectTree.setStyle("-fx-background-color: " + BG_CARD + "; -fx-control-inner-background: " + BG_CARD + ";");
        projectTree.setCellFactory(tv -> new TreeCell<FileNode>() {
            @Override
            protected void updateItem(FileNode item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                }
                else {
                    setText(item.getName());
                    setTextFill(Color.web(TEXT_PRIMARY));
                    if (item.getType() == FileNodeType.CLASS) {
                        setGraphic(createIcon("🔷", ACCENT_BLUE));
                    }
                    else if (item.getType() == FileNodeType.METHOD) {
                        setGraphic(createIcon("⚡", ACCENT_ORANGE));
                    }
                    else if (item.getType() == FileNodeType.PACKAGE) {
                        setGraphic(createIcon("📦", TEXT_SECONDARY));
                    }
                    else {
                        setGraphic(createIcon("📁", TEXT_SECONDARY));
                    }
                }
            }
        });

        projectTree.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && newVal.getValue().getType() == FileNodeType.METHOD) {
                analyzeMethod(newVal.getValue());
            }
            else if (newVal != null && newVal.getValue().getType() == FileNodeType.CLASS) {
                analyzeClass(newVal.getValue());
            }
        });

        // Navigation items
        Label navLabel = new Label("Navigation");
        navLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        navLabel.setTextFill(Color.web(TEXT_SECONDARY));

        Button dashboardBtn  = createNavButton("📊 Dashboard", true);
        Button dependencyBtn = createNavButton("🌳 Dependency Tree", false);
        Button vulnBtn       = createNavButton("⚠️ Vulnerabilities", false);
        Button settingsBtn   = createNavButton("⚙️ Settings", false);

        VBox.setVgrow(projectTree, Priority.ALWAYS);
        sidebar.getChildren().addAll(searchField, explorerLabel, projectTree,
                                     new Separator(), navLabel, dashboardBtn, dependencyBtn, vulnBtn, settingsBtn);

        return sidebar;
    }

    private Button createNavButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        String bg = active ? ACCENT_BLUE : "transparent";
        String fg = active ? "#ffffff" : TEXT_PRIMARY;
        btn.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: %s; -fx-padding: 8 12; -fx-background-radius: 6;", bg, fg));
        btn.setOnMouseEntered(e -> {
            if (!active) {
                btn.setStyle("-fx-background-color: " + BG_CARD + "; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-padding: 8 12; -fx-background-radius: 6;");
            }
        });
        btn.setOnMouseExited(e -> {
            if (!active) {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-padding: 8 12; -fx-background-radius: 6;");
            }
        });
        return btn;
    }

    private Label createIcon(String emoji, String color) {
        Label label = new Label(emoji);
        label.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
        return label;
    }

    private TabPane createCenterTabs() {
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: " + BG_DARK + "; -fx-tab-min-width: 120;");

        // Dashboard Tab
        Tab dashboardTab = new Tab("Dashboard", createDashboardView());
        dashboardTab.setClosable(false);

        // Bytecode Tab
        Tab bytecodeTab = new Tab("Bytecode", createBytecodeView());
        bytecodeTab.setClosable(false);

        // Structure Tab
        Tab structureTab = new Tab("Structure", createStructureView());
        structureTab.setClosable(false);

        // Memory Tab
        Tab memoryTab = new Tab("Memory", createMemoryView());
        memoryTab.setClosable(false);

        tabPane.getTabs().addAll(dashboardTab, bytecodeTab, structureTab, memoryTab);
        return tabPane;
    }

    private ScrollPane createDashboardView() {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_DARK + "; -fx-background-color: " + BG_DARK + ";");

        VBox dashboard = new VBox(20);
        dashboard.setPadding(new Insets(20));
        dashboard.setStyle("-fx-background-color: " + BG_DARK + ";");

        // Header with breadcrumb
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label breadcrumb = new Label("core-engine.jar  /  com.analysis.engine  /  AnalyzerCore.class");
        breadcrumb.setTextFill(Color.web(TEXT_SECONDARY));
        breadcrumb.setFont(Font.font("Monospace", 12));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button rescanBtn = new Button("↻ Re-Scan");
        rescanBtn.setStyle("-fx-background-color: " + BG_CARD + "; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-padding: 6 16;");

        Button reportBtn = new Button("📄 Generate Report");
        reportBtn.setStyle("-fx-background-color: " + ACCENT_BLUE + "; -fx-text-fill: white; -fx-padding: 6 16; -fx-font-weight: bold;");

        header.getChildren().addAll(breadcrumb, spacer, rescanBtn, reportBtn);

        // Stats cards
        HBox statsBox = new HBox(15);
        statsBox.setAlignment(Pos.CENTER);

        statsBox.getChildren().addAll(
                createStatCard("TOTAL\nCLASSES", totalClasses, "🔷", ACCENT_BLUE, "+2.1%", true),
                createStatCard("TOTAL\nMETHODS", totalMethods, "⚡", ACCENT_BLUE, "+0.5%", true),
                createStatCard("AVG.\nCOMPLEXITY", avgComplexity, "📊", ACCENT_BLUE, "-1.2%", false),
                createStatCard("SECURITY\nHITS", securityHits, "⚠️", ACCENT_RED, "5 Critical", true)
                                     );

        // Charts row
        HBox chartsBox = new HBox(15);
        chartsBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(chartsBox, Priority.ALWAYS);

        // Instruction Frequency
        VBox                     freqBox   = createChartPanel("Instruction Frequency");
        BarChart<String, Number> freqChart = createFrequencyChart();
        freqBox.getChildren().add(freqChart);

        // Structural Health
        VBox                     healthBox   = createChartPanel("Structural Health");
        BarChart<String, Number> healthChart = createHealthChart();
        healthBox.getChildren().add(healthChart);

        chartsBox.getChildren().addAll(freqBox, healthBox);

        // Risk Assessment Table
        VBox riskBox = createRiskPanel();

        dashboard.getChildren().addAll(header, statsBox, chartsBox, riskBox);
        scroll.setContent(dashboard);
        return scroll;
    }

    private VBox createChartPanel(String title) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 8; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8;");
        box.setPrefWidth(400);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.web(TEXT_PRIMARY));
        box.getChildren().add(titleLabel);

        return box;
    }

    private BarChart<String, Number> createFrequencyChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setTickLabelFill(Color.web(TEXT_SECONDARY));
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickLabelFill(Color.web(TEXT_SECONDARY));

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setStyle("-fx-background-color: transparent;");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("aload_0", 24.5));
        series.getData().add(new XYChart.Data<>("invokevirtual", 18.2));
        series.getData().add(new XYChart.Data<>("getfield", 12.8));
        series.getData().add(new XYChart.Data<>("return", 9.4));

        chart.getData().add(series);

        // Style bars
        series.getData().forEach(data -> {
            data.nodeProperty().addListener((obs, old, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: " + ACCENT_BLUE + ";");
                }
            });
        });

        return chart;
    }

    private BarChart<String, Number> createHealthChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setTickLabelFill(Color.web(TEXT_SECONDARY));
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickLabelFill(Color.web(TEXT_SECONDARY));

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setStyle("-fx-background-color: transparent;");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("SIMPLE", 15));
        series.getData().add(new XYChart.Data<>("MODERATE", 28));
        series.getData().add(new XYChart.Data<>("COMPLEX", 45));
        series.getData().add(new XYChart.Data<>("HIGH_COMPLEXITY", 38));
        series.getData().add(new XYChart.Data<>("CRITICAL", 12));

        chart.getData().add(series);

        // Color code bars
        String[] colors = { ACCENT_GREEN, ACCENT_BLUE, ACCENT_BLUE, ACCENT_ORANGE, ACCENT_RED };
        for (int i = 0; i < series.getData().size(); i++) {
            final int idx = i;
            series.getData().get(i).nodeProperty().addListener((obs, old, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: " + colors[idx] + ";");
                }
            });
        }

        return chart;
    }

    private VBox createRiskPanel() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 8; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Risk Assessment Highlights");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setTextFill(Color.web(TEXT_PRIMARY));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label actionLabel = new Label("Action Required");
        actionLabel.setStyle("-fx-background-color: " + ACCENT_RED + "20; -fx-text-fill: " + ACCENT_RED + "; -fx-padding: 4 8; -fx-background-radius: 4;");

        header.getChildren().addAll(title, spacer, actionLabel);
        box.getChildren().add(header);

        // Risk table
        TableView<RiskItem> riskTable = new TableView<>();
        riskTable.setItems(riskItems);
        riskTable.setStyle("-fx-background-color: " + BG_PANEL + "; -fx-table-cell-border-color: " + BORDER_COLOR + ";");
        riskTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<RiskItem, String> locCol = new TableColumn<>("Location");
        locCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        locCol.setCellFactory(col -> new TableCell<RiskItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                }
                else {
                    setText(item);
                    setTextFill(Color.web(ACCENT_BLUE));
                    setFont(Font.font("Monospace", 12));
                }
            }
        });

        TableColumn<RiskItem, String> typeCol = new TableColumn<>("Issue Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("issueType"));
        typeCol.setCellFactory(col -> new TableCell<RiskItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                }
                else {
                    setText(item);
                    setTextFill(Color.web(TEXT_PRIMARY));
                    setFont(Font.font("System", FontWeight.BOLD, 12));
                }
            }
        });

        TableColumn<RiskItem, String> sevCol = new TableColumn<>("Severity");
        sevCol.setCellValueFactory(new PropertyValueFactory<>("severity"));
        sevCol.setCellFactory(col -> new TableCell<RiskItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                }
                else {
                    setText(item);
                    String color = item.equals("Critical") ? ACCENT_RED : item.equals("Medium") ? ACCENT_ORANGE : ACCENT_GREEN;
                    setStyle("-fx-background-color: " + color + "30; -fx-text-fill: " + color + "; -fx-padding: 2 8; -fx-background-radius: 4;");
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<RiskItem, String> detCol = new TableColumn<>("Details");
        detCol.setCellValueFactory(new PropertyValueFactory<>("details"));
        detCol.setCellFactory(col -> new TableCell<RiskItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                }
                else {
                    setText(item);
                    setTextFill(Color.web(TEXT_SECONDARY));
                    setWrapText(true);
                }
            }
        });

        riskTable.getColumns().addAll(locCol, typeCol, sevCol, detCol);
        VBox.setVgrow(riskTable, Priority.ALWAYS);
        box.getChildren().add(riskTable);

        // Add sample data
        riskItems.addAll(
                new RiskItem("com.analysis.engine.AnalyzerCore", "Reflective Access", "Medium", "Dynamic class loading via Class.forName() detected"),
                new RiskItem("org.jvm.internal.NativeBridge", "Suspicious Opcode Pattern", "Critical", "Multiple Runtime.exec() calls detected"),
                new RiskItem("com.crypto.VaultProvider", "Obfuscation Detected", "Low", "High entropy method names in decrypt() signature"));

        return box;
    }

    private Pane createStatCard(String title, Property<? extends Number> value, String icon, String color,
                                String change, boolean isInt) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 12; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12;");

        HBox  top       = new HBox(10);
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(24));
        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web(TEXT_SECONDARY));
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
        top.getChildren().addAll(iconLabel, titleLabel);

        Label valueLabel = new Label();
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        valueLabel.setTextFill(Color.web(TEXT_PRIMARY));

        if (isInt) {
            valueLabel.textProperty().bind(Bindings.format("%,d", value));
        }
        else {
            valueLabel.textProperty().bind(Bindings.format("%.1f", value));
        }

        Label   changeLabel = new Label(change);
        boolean positive    = change.startsWith("+") || change.contains("Optimized");
        changeLabel.setTextFill(Color.web(positive ? ACCENT_GREEN : ACCENT_RED));
        changeLabel.setFont(Font.font("System", 11));

        card.getChildren().addAll(top, valueLabel, changeLabel);
        return card;
    }

    private SplitPane createBytecodeView() {
        SplitPane split = new SplitPane();
        split.setDividerPositions(0.5);
        split.setStyle("-fx-background-color: " + BG_DARK + ";");

        // Left: Bytecode instructions
        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));
        leftPane.setStyle("-fx-background-color: " + BG_PANEL + ";");

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

        TableView<BytecodeInstruction> table = new TableView<>(instructions);
        table.setStyle("-fx-background-color: " + BG_CARD + "; -fx-table-cell-border-color: " + BORDER_COLOR + ";");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<BytecodeInstruction, String> offsetCol = new TableColumn<>("Offset");
        offsetCol.setCellValueFactory(new PropertyValueFactory<>("offset"));
        offsetCol.setPrefWidth(60);
        offsetCol.setCellFactory(col -> createCodeCell(ACCENT_GREEN));

        TableColumn<BytecodeInstruction, String> mnemonicCol = new TableColumn<>("Mnemonic");
        mnemonicCol.setCellValueFactory(new PropertyValueFactory<>("mnemonic"));
        mnemonicCol.setPrefWidth(100);
        mnemonicCol.setCellFactory(col -> createCodeCell(ACCENT_BLUE));

        TableColumn<BytecodeInstruction, String> argsCol = new TableColumn<>("Arguments");
        argsCol.setCellValueFactory(new PropertyValueFactory<>("arguments"));
        argsCol.setPrefWidth(300);
        argsCol.setCellFactory(col -> createCodeCell(TEXT_SECONDARY));

        table.getColumns().addAll(offsetCol, mnemonicCol, argsCol);

        // Add sample bytecode
        instructions.addAll(
                new BytecodeInstruction("0000", "aload_0", ""),
                new BytecodeInstruction("0001", "ifnonnull", "L12"),
                new BytecodeInstruction("0004", "new", "#4 // java/lang/NullPointerException"),
                new BytecodeInstruction("0007", "dup", ""),
                new BytecodeInstruction("0008", "invokespecial", "#5 // java/lang/NullPointerException.\"<init>\":()V"),
                new BytecodeInstruction("0011", "athrow", ""),
                new BytecodeInstruction("0012", "L12:", ""),
                new BytecodeInstruction("0012", "getstatic", "#6 // java/lang/System.out:Ljava/io/PrintStream;")
                           );

        leftPane.getChildren().addAll(toolbar, table);

        // Right: Decompiled Java
        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(10));
        rightPane.setStyle("-fx-background-color: " + BG_PANEL + ";");

        HBox rightToolbar = new HBox(10);
        rightToolbar.setAlignment(Pos.CENTER_LEFT);
        Label decompLabel = new Label("⇄ Decompiled Java");
        decompLabel.setTextFill(Color.web(TEXT_SECONDARY));
        decompLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        Button copyBtn   = new Button("⎘");
        Button expandBtn = new Button("⛶");
        rightToolbar.getChildren().addAll(decompLabel, rightSpacer, copyBtn, expandBtn);

        decompiledArea = new TextArea();
        decompiledArea.setEditable(false);
        decompiledArea.setWrapText(true);
        decompiledArea.setFont(Font.font("JetBrains Mono", 13));
        decompiledArea.setStyle("-fx-control-inner-background: " + BG_CARD + "; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-highlight-fill: " + ACCENT_BLUE + "40;");

        decompiledArea.setText(
                "public void execute(Runnable command) {\n" +
                        "    if (command == null)\n" +
                        "        throw new NullPointerException();\n" +
                        "    \n" +
                        "    /*\n" +
                        "     * Proceed with execution logic\n" +
                        "     * based on current pool state\n" +
                        "     */\n" +
                        "    int c = ctl.get();\n" +
                        "    if (workerCountOf(c) < corePoolSize) {\n" +
                        "        if (addWorker(command, true))\n" +
                        "            return;\n" +
                        "        c = ctl.get();\n" +
                        "    }\n" +
                        "    if (isRunning(c) && workQueue.offer(command)) {\n" +
                        "        int recheck = ctl.get();\n" +
                        "        if (! isRunning(recheck) && remove(command))\n" +
                        "            reject(command);\n" +
                        "        else if (workerCountOf(recheck) == 0)\n" +
                        "            addWorker(null, false);\n" +
                        "    }\n" +
                        "}"
                              );

        VBox.setVgrow(decompiledArea, Priority.ALWAYS);
        rightPane.getChildren().addAll(rightToolbar, decompiledArea);

        split.getItems().addAll(leftPane, rightPane);
        return split;
    }

    private TableCell<BytecodeInstruction, String> createCodeCell(String color) {
        return new TableCell<BytecodeInstruction, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                }
                else {
                    setText(item);
                    setTextFill(Color.web(color));
                    setFont(Font.font("JetBrains Mono", 12));
                }
            }
        };
    }

    private Pane createStructureView() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: " + BG_DARK + ";");

        Label title = new Label("Class Structure");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setTextFill(Color.web(TEXT_PRIMARY));

        TreeView<String> structureTree = new TreeView<>();
        structureTree.setStyle("-fx-background-color: " + BG_CARD + ";");

        TreeItem<String> root = new TreeItem<>("java.util.concurrent.ThreadPoolExecutor");
        root.setExpanded(true);

        TreeItem<String> fields = new TreeItem<>("Fields (12)");
        fields.getChildren().addAll(
                new TreeItem<>("- ctl: AtomicInteger"),
                new TreeItem<>("- workers: HashSet<Worker>"),
                new TreeItem<>("- workQueue: BlockingQueue<Runnable>")
                                   );

        TreeItem<String> methods = new TreeItem<>("Methods (45)");
        methods.getChildren().addAll(
                new TreeItem<>("+ execute(Runnable): void"),
                new TreeItem<>("+ shutdown(): void"),
                new TreeItem<>("+ tryTerminate(): void"),
                new TreeItem<>("- addWorker(Runnable, boolean): boolean")
                                    );

        root.getChildren().addAll(fields, methods);
        structureTree.setRoot(root);

        box.getChildren().addAll(title, structureTree);
        return box;
    }

    private Pane createMemoryView() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: " + BG_DARK + ";");

        Label title = new Label("Memory Layout");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setTextFill(Color.web(TEXT_PRIMARY));

        // Memory visualization
        GridPane memoryGrid = new GridPane();
        memoryGrid.setHgap(5);
        memoryGrid.setVgap(5);
        memoryGrid.setStyle("-fx-background-color: " + BG_CARD + "; -fx-padding: 20;");

        String[] sections = { "Constant Pool", "Methods", "Fields", "Attributes", "Code" };
        int[]    sizes    = { 1024, 512, 256, 128, 2048 };
        String[] colors   = new String[]{ ACCENT_BLUE, ACCENT_GREEN, ACCENT_ORANGE, ACCENT_RED, ACCENT_EXTRA };

        for (int i = 0; i < sections.length; i++) {
            Label nameLabel = new Label(sections[i]);
            nameLabel.setTextFill(Color.web(TEXT_PRIMARY));
            nameLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

            Label sizeLabel = new Label(sizes[i] + " bytes");
            sizeLabel.setTextFill(Color.web(TEXT_SECONDARY));

            Pane colorBar = new Pane();
            colorBar.setPrefHeight(20);
            colorBar.setPrefWidth(sizes[i] / 10.0);
            colorBar.setStyle("-fx-background-color: " + colors[i] + "; -fx-background-radius: 4;");

            memoryGrid.addRow(i, nameLabel, colorBar, sizeLabel);
        }

        box.getChildren().addAll(title, memoryGrid);
        return box;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(15);
        statusBar.setPadding(new Insets(8, 15, 8, 15));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setStyle("-fx-background-color: " + BG_PANEL + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1 0 0 0;");

        Circle indicator = new Circle(4, Color.web(ACCENT_GREEN));
        Label  liveLabel = new Label("Live Analysis Active");
        liveLabel.setTextFill(Color.web(TEXT_SECONDARY));
        liveLabel.setFont(Font.font(11));

        statusLabel = new Label("Ready");
        statusLabel.setTextFill(Color.web(TEXT_SECONDARY));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label versionLabel = new Label("Class Version: 61.0 (Java 17)");
        versionLabel.setTextFill(Color.web(TEXT_SECONDARY));
        versionLabel.setFont(Font.font("Monospace", 11));

        Label complexityLabel = new Label("Method complexity: 12");
        complexityLabel.setTextFill(Color.web(TEXT_SECONDARY));
        complexityLabel.setFont(Font.font("Monospace", 11));

        progressBar = new ProgressBar(0);
        progressBar.setVisible(false);
        progressBar.setPrefWidth(150);

        statusBar.getChildren().addAll(indicator, liveLabel, new Separator(Orientation.VERTICAL),
                                       statusLabel, spacer, versionLabel, new Separator(Orientation.VERTICAL),
                                       complexityLabel, progressBar);

        return statusBar;
    }

    private void openFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open JAR or Class File");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JAR Files", "*.jar"),
                new FileChooser.ExtensionFilter("Class Files", "*.class"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
                                            );

        File file = chooser.showOpenDialog(primaryStage);
        if (file != null) {
            loadFile(file);
        }
    }

    private void loadFile(File file) {
        progressBar.setVisible(true);
        progressBar.setProgress(-1);
        statusLabel.setText("Loading: " + file.getName());

        new Thread(() -> {
            try {
                if (file.getName().endsWith(".jar")) {
                    loadJarFile(file);
                }
                else if (file.getName().endsWith(".class")) {
                    loadClassFile(file);
                }

                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    statusLabel.setText("Loaded: " + file.getName());
                    updateStatistics();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    statusLabel.setText("Error loading file");
                    showError("Failed to load file: " + e.getMessage());
                });
            }
        }).start();
    }

    private void loadJarFile(File jarFile) throws IOException {
        TreeItem<FileNode>              rootItem   = new TreeItem<>(new FileNode(jarFile.getName(), jarFile.getAbsolutePath(), FileNodeType.ROOT));
        Map<String, TreeItem<FileNode>> packageMap = new HashMap<>();

        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries     = jar.entries();
            int                   classCount  = 0;
            int                   methodCount = 0;

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String   name  = entry.getName();

                if (name.endsWith(".class")) {
                    classCount++;
                    String   className = name.replace("/", ".").replace(".class", "");
                    String[] parts     = className.split("\\.");

                    // Build package hierarchy
                    StringBuilder      currentPackage = new StringBuilder();
                    TreeItem<FileNode> parent         = rootItem;

                    for (int i = 0; i < parts.length - 1; i++) {
                        if (currentPackage.length() > 0) {
                            currentPackage.append(".");
                        }
                        currentPackage.append(parts[i]);

                        String pkgPath = currentPackage.toString();
                        if (!packageMap.containsKey(pkgPath)) {
                            TreeItem<FileNode> pkgItem = new TreeItem<>(
                                    new FileNode(parts[i], pkgPath, FileNodeType.PACKAGE));
                            packageMap.put(pkgPath, pkgItem);
                            parent.getChildren().add(pkgItem);
                        }
                        parent = packageMap.get(pkgPath);
                    }

                    // Add class node
                    TreeItem<FileNode> classItem = new TreeItem<>(
                            new FileNode(parts[parts.length - 1] + ".class", name, FileNodeType.CLASS));
                    parent.getChildren().add(classItem);

                    // Analyze class for methods
                    try (InputStream is = jar.getInputStream(entry)) {
                        System.out.println("Analyzing class: " + className + ".class");
                        /*ClassReader reader = new ClassReader(is);
                        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9) {
                            @Override
                            public MethodVisitor visitMethod(int access, String name, String descriptor,
                                                             String signature, String[] exceptions) {
                                methodCount++;
                                Platform.runLater(() -> {
                                    TreeItem<FileNode> methodItem = new TreeItem<>(
                                            new FileNode(name + descriptor, name, FileNodeType.METHOD));
                                    classItem.getChildren().add(methodItem);
                                });
                                return null;
                            }
                        };
                        reader.accept(cv, 0);*/
                    }
                }
            }

            final int finalClassCount  = classCount;
            final int finalMethodCount = methodCount;

            Platform.runLater(() -> {
                totalClasses.set(finalClassCount);
                totalMethods.set(finalMethodCount);
                avgComplexity.set(12.4);
                securityHits.set(32);
                projectTree.setRoot(rootItem);
                rootItem.setExpanded(true);
            });
        }
    }

    private void loadClassFile(File classFile) throws IOException {
        TreeItem<FileNode> rootItem  = new TreeItem<>(new FileNode(classFile.getName(), classFile.getAbsolutePath(), FileNodeType.ROOT));
        TreeItem<FileNode> classItem = new TreeItem<>(new FileNode(classFile.getName(), classFile.getAbsolutePath(), FileNodeType.CLASS));
        rootItem.getChildren().add(classItem);

        try (InputStream is = new FileInputStream(classFile)) {
            /*ClassReader reader = new ClassReader(is);
            ClassVisitor cv = new ClassVisitor(Opcodes.ASM9) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor,
                                                 String signature, String[] exceptions) {
                    Platform.runLater(() -> {
                        TreeItem<FileNode> methodItem = new TreeItem<>(
                                new FileNode(name + descriptor, name, FileNodeType.METHOD));
                        classItem.getChildren().add(methodItem);
                    });
                    return null;
                }
            };
            reader.accept(cv, 0);*/
            System.out.println("Analyzing class: " + classFile.getName());
        }

        Platform.runLater(() -> {
            totalClasses.set(1);
            projectTree.setRoot(rootItem);
            rootItem.setExpanded(true);
        });
    }

    private void analyzeClass(FileNode classNode) {
        statusLabel.setText("Analyzing class: " + classNode.getName());
        // Update decompiled view with class info
    }

    private void analyzeMethod(FileNode methodNode) {
        statusLabel.setText("Analyzing method: " + methodNode.getName());

        // Update bytecode view with method instructions
        instructions.clear();

        // Sample bytecode for demonstration
        instructions.addAll(
                new BytecodeInstruction("0000", "aload_0", ""),
                new BytecodeInstruction("0001", "getfield", "#2 // Field command:Ljava/lang/Runnable;"),
                new BytecodeInstruction("0004", "ifnonnull", "L12"),
                new BytecodeInstruction("0007", "new", "#3 // class java/lang/NullPointerException"),
                new BytecodeInstruction("0010", "dup", ""),
                new BytecodeInstruction("0011", "invokespecial", "#4 // Method java/lang/NullPointerException.\"<init>\":()V"),
                new BytecodeInstruction("0014", "athrow", ""),
                new BytecodeInstruction("0015", "L12:", ""),
                new BytecodeInstruction("0015", "aload_0", ""),
                new BytecodeInstruction("0016", "getfield", "#5 // Field ctl:Ljava/util/concurrent/atomic/AtomicInteger;"),
                new BytecodeInstruction("0019", "invokevirtual", "#6 // Method java/util/concurrent/atomic/AtomicInteger.get:()I")
                           );
    }

    private void updateStatistics() {
        // Update dashboard statistics based on loaded data
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Data models
    public static class FileNode {
        private final String       name;
        private final String       path;
        private final FileNodeType type;

        public FileNode(String name, String path, FileNodeType type) {
            this.name = name;
            this.path = path;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        public FileNodeType getType() {
            return type;
        }
    }

    public enum FileNodeType {
        ROOT, PACKAGE, CLASS, METHOD
    }

    public static class BytecodeInstruction {
        private final String offset;
        private final String mnemonic;
        private final String arguments;

        public BytecodeInstruction(String offset, String mnemonic, String arguments) {
            this.offset    = offset;
            this.mnemonic  = mnemonic;
            this.arguments = arguments;
        }

        public String getOffset() {
            return offset;
        }

        public String getMnemonic() {
            return mnemonic;
        }

        public String getArguments() {
            return arguments;
        }
    }

    public static class RiskItem {
        private final String location;
        private final String issueType;
        private final String severity;
        private final String details;

        public RiskItem(String location, String issueType, String severity, String details) {
            this.location  = location;
            this.issueType = issueType;
            this.severity  = severity;
            this.details   = details;
        }

        public String getLocation() {
            return location;
        }

        public String getIssueType() {
            return issueType;
        }

        public String getSeverity() {
            return severity;
        }

        public String getDetails() {
            return details;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}