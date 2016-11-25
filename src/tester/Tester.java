package tester;

import gui.Gui;
import tools.CustomClassLoader;
import tools.FileJavaClass;
import tools.FileJavaSource;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Petec on 12.11.2016.
 */
public class Tester {
    private List<FileJavaClass> compiledSolution;
    private List<FileJavaSource> testFiles;
    private Gui gui;
    private String duClassName;
    private List<Test> tests;

    private static Tester instance = null;

    protected Tester() {
        this.tests = new ArrayList<>();
    }

    public static Tester getInstance() {
        if(instance == null) {
            instance = new Tester();
        }
        return instance;
    }

    public Tester(List<FileJavaClass> compiledSolution, Gui gui, String duClassName){
        this.compiledSolution = compiledSolution;
        this.gui = gui;
        this.duClassName = duClassName;
        this.tests = new ArrayList<>();
    }

    public void getTestSetup(List<FileJavaClass> solution){
        CustomClassLoader.getInstance().loadClasses(solution);
        tests.forEach(test -> {
            if(test.getTestElement().equals("class") && test.getTestSpecification().equals("nameCondition")){
                FileJavaClass file = getClassFromList(solution, test.getClassName());
                if(file != null){
                    boolean result = runClassNameTest(file, test.getTestValue());
                    System.out.println("Test result:" + result);
                }
            }
        });
    }

    private FileJavaClass getClassFromList(List<FileJavaClass> solution, String className) {
        final FileJavaClass[] file = {null};
        solution.forEach(aFile -> {
            if(aFile.getaClass().getName().equals(className)) {
                file[0] = aFile;
                System.out.println("Found one!");
            }
        });
        return file[0];
    }

    public boolean runClassNameTest(FileJavaClass testedFile, String value){
        System.out.println("Testing: " + value + ", " + testedFile.getaClass().getName());
        Pattern pattern = Pattern.compile(value);
        Matcher matcher = pattern.matcher(testedFile.getaClass().getName());

        return matcher.find();
    }

    public List<FileJavaClass> getCompiledSolution() {
        return compiledSolution;
    }

    public String getDuClassName() {
        return duClassName;
    }

    public void setDuClassName(String duClassName) {
        this.duClassName = duClassName;
    }

    public void setCompiledSolution(List<FileJavaClass> compiledSolution) {
        this.compiledSolution = compiledSolution;
    }

    public Gui getGui() {
        return gui;
    }

    public void setGui(Gui gui) {
        this.gui = gui;
    }

    public List<Test> getTests() {
        return tests;
    }


}
