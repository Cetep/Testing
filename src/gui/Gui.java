package gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tester.Tester;
import tools.*;
import tools.Compiler;

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
    private List<FileJavaClass> compiledTestFiles;

    private Compiler compiler;
    private CustomXMLParser xmlParser;

    private String noDirText;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        initComponents(primaryStage);
        initActions();
        initOther();
    }

    private void initOther() {
        noDirText = "No Directory selected";
        taskFiles = new ArrayList<>();
        compiledSolution = new ArrayList<>();
        compiledTestFiles = new ArrayList<>();
        testFiles = new ArrayList<>();
        xmlParser = new CustomXMLParser(this);
        compiler = new Compiler(this);
    }

    private void initComponents(Stage primaryStage) {
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

        solutionDirLabel.setText(noDirText);
        libsDirLabel.setText(noDirText);
        compOutLabel.setText(noDirText);

        mainPane.setBottom(logArea);
        mainPane.setLeft(listView);

        Scene mainScene = new Scene(mainPane, 800, 600);

        primaryStage.setMaximized(true);
        primaryStage.setTitle("Testing");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void initActions() {
        compileSolution.setOnAction(event -> {
            logArea.insertText(0, "Compiling..." + '\n');
            compiledSolution = compiler.compile(taskFiles, libsDirLabel.getText(), compOutLabel.getText());

            createTemp(compOutLabel.getText());
            logArea.insertText(0, "Compiling..." + '\n');

            compiledTestFiles = compiler.compileTestFiles(testFiles, libsDirLabel.getText(), getTempPath(), compiledSolution);

            CustomClassLoader.getInstance().loadClasses(compiledSolution, compiledTestFiles);
        });

        getTests.setOnAction(event -> Tester.getInstance().runTests(compiledTestFiles));

        loadXML.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open XML File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("XML file", "*.xml"));
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                xmlParser.parseXML(selectedFile);
                if(solutionDirLabel.getText().equals(noDirText)){
                    logArea.insertText(0, "Check XML file. No path to the solution.");
                }else{
                    getTaskFiles(solutionDirLabel.getText());
                    Tester.getInstance().setGui(this);
                }
            }
        });
    }

    private void createTemp(String path){
        if(!path.endsWith("\\")){
            path = path + "\\";
        }
        if(new File(path + "temp").mkdirs()){
            System.out.println("Temp DIR created.");
        }else{
            System.out.println("Temp DIR creation failed.");
        }
    }

    private String getTempPath() {
        String path = compOutLabel.getText();
        if(!path.endsWith("\\")){
            path = path + "\\";
        }
        return path + "temp";
    }

    private void getTaskFiles(String absolutePath) {
        File [] listOfFiles = new File(absolutePath).listFiles();

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                if (listOfFile.getName().endsWith(".java")) {
                    taskFiles.add(new FileJavaSource(listOfFile));
                    logArea.insertText(0, "Task file loaded: " + listOfFile.getName() + "\n");
                }
            } else if (listOfFile.isDirectory()) {
                logArea.insertText(0, "Directory found: " + listOfFile.getName() + "\n");
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

    public void setTestFiles(String path){
        File [] listOfFiles = new File(path).listFiles();
        List<String> fileNames = new ArrayList<>();

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile() && listOfFile.getName().endsWith(".java")) {
                fileNames.add(listOfFile.getName());
                testFiles.add(new FileJavaSource(listOfFile));
            }
        }
        ObservableList<String> items = FXCollections.observableArrayList (fileNames);
        listView.setItems(items);
    }

    public void clearTestFiles(){
        testFiles.clear();
        listView.getItems().clear();
    }

    public void clearTaskFiles(){
        taskFiles.clear();
        compiledSolution.clear();

        solutionDirLabel.setText(noDirText);
        libsDirLabel.setText(noDirText);
        compOutLabel.setText(noDirText);
    }

    public void clearAll(){
        clearTestFiles();
        clearTaskFiles();
    }

    public List<FileJavaClass> getCompiledSolution() {
        return compiledSolution;
    }

    public void setCompiledSolution(List<FileJavaClass> compiledSolution) {
        this.compiledSolution = compiledSolution;
    }

    public List<FileJavaClass> getCompiledTestFiles() {
        return compiledTestFiles;
    }

    public void setCompiledTestFiles(List<FileJavaClass> compiledTestFiles) {
        this.compiledTestFiles = compiledTestFiles;
    }
}