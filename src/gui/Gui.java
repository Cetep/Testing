package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tester.Tester;
import tools.Compiler;
import tools.CustomXMLParser;
import tools.FileJavaClass;
import tools.FileJavaSource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petec on 25.8.2016.
 */
public class Gui extends Application {
    private MenuItem compileSolution;
    private MenuItem loadXML;
    private MenuItem getTests;
    private TextArea logArea;

    private ListView<String> listView;

    private Label preSolutionDirLabel;
    private Label preLibsDirLabel;
    private Label preCompOutLabel;

    private Label solutionDirLabel;
    private Label libsDirLabel;
    private Label compOutLabel;

    private List<FileJavaClass> compiledSolution;
    private List<FileJavaSource> testFiles;
    private List<FileJavaSource> taskFiles;

    private Compiler compiler;
    private CustomXMLParser xmlParser;
    private String duClassName;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        initGuiComponents(primaryStage);
        initGuiActions();


        duClassName = "";
        taskFiles = new ArrayList<>();
        compiledSolution = new ArrayList<>();
        testFiles = new ArrayList<>();
        xmlParser = new CustomXMLParser(this);
        compiler = new Compiler(this);
    }

    private void initGuiComponents(Stage primaryStage) {
        preSolutionDirLabel = new Label("Solution DIR: ");
        preLibsDirLabel = new Label("Libs DIR: ");
        preCompOutLabel = new Label("Compilation output DIR: ");

        BorderPane mainPane = new BorderPane();
        MenuBar menuBar = new MenuBar();

        Menu menuFile = new Menu("File");
        Menu menuRun = new Menu("Run");

        compileSolution = new MenuItem("Compile solution");
        getTests = new MenuItem("Get test setup");
        loadXML = new MenuItem("Load XML");

        menuFile.getItems().addAll(loadXML);
        menuRun.getItems().addAll(compileSolution, getTests);

        menuBar.getMenus().addAll(menuFile, menuRun);

        GridPane centerGrid = new GridPane();

        solutionDirLabel = new Label("");
        libsDirLabel = new Label("");
        compOutLabel = new Label("");

        logArea = new TextArea();
        listView = new ListView<>();

        centerGrid.setPadding(new Insets(5,5,5,5));
        centerGrid.add(preSolutionDirLabel, 0, 0);
        centerGrid.add(solutionDirLabel,1,0);
        centerGrid.add(preLibsDirLabel, 0, 2);
        centerGrid.add(libsDirLabel, 1, 2);
        centerGrid.add(preCompOutLabel, 0, 4);
        centerGrid.add(compOutLabel, 1 , 4);
        mainPane.setCenter(centerGrid);
        mainPane.setTop(menuBar);

        solutionDirLabel.setText("No Directory selected");
        libsDirLabel.setText("No Directory selected");
        compOutLabel.setText("No Directory selected");

        mainPane.setBottom(logArea);
        mainPane.setLeft(listView);

        Scene mainScene = new Scene(mainPane, 800, 600);

        primaryStage.setMaximized(true);
        primaryStage.setTitle("Testing");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void initGuiActions() {
        compileSolution.setOnAction(event -> {
            compiledSolution = compiler.compile(taskFiles, libsDirLabel.getText(), compOutLabel.getText());
        });

        getTests.setOnAction(event -> {
            compiledSolution = Compiler.getClassFilesFromDir(compOutLabel.getText());
            Tester.getInstance().getTestSetup(compiledSolution);
        });

        loadXML.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open XML File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("XML file", "*.xml"));
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                xmlParser.parseXML(selectedFile);
                if(solutionDirLabel.getText().equals("No Directory selected")){
                    logArea.insertText(0, "Check XML file. No path to the solution.");
                }else{
                    getTaskFiles(solutionDirLabel.getText());
                    Tester.getInstance().setGui(this);
                    Tester.getInstance().setCompiledSolution(compiledSolution);
                    Tester.getInstance().setDuClassName(duClassName);
                }

            }

        });
    }

    private void getTaskFiles(String absolutePath) {
        File [] listOfFiles = new File(absolutePath).listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                if(listOfFiles[i].getName().endsWith(".java")){
                    taskFiles.add(new FileJavaSource(listOfFiles[i]));
                    logArea.insertText(0,"Task file loaded: " + listOfFiles[i].getName() + "\n");
                }
            } else if (listOfFiles[i].isDirectory()) {
                logArea.insertText(0,"Directory found: " + listOfFiles[i].getName() + "\n");
            }
        }
    }

    public TextArea getLogArea() {
        return logArea;
    }

    public Label getSolutionDirLabel() {
        return solutionDirLabel;
    }

    public Label getLibsDirLabel() {
        return libsDirLabel;
    }

    public Label getCompOutLabel() {
        return compOutLabel;
    }

    public String getDuClassName() {
        return duClassName;
    }

    public void setDuClassName(String duClassName) {
        this.duClassName = duClassName;
    }
}