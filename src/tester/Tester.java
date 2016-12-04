package tester;

import gui.Gui;
import tools.FileJavaClass;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Petec on 12.11.2016.
 */
public class Tester {

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

    public void runTests(List<FileJavaClass> testFiles){

        tests.forEach(test -> {
            if(test.getTestElement().equals("class")){
                runClassTests(testFiles, test);
            }

            if(test.getTestElement().equals("variables")){
                runVariableTests(testFiles, test);
            }
        });
    }

    private void runVariableTests(List<FileJavaClass> testFiles, Test test) {
        test.getTestSpec().forEach(testSpec -> {
            if(testSpec.getTestSpecification().equals("type")){
                testFiles.forEach(file ->{
                    int result = runVariableTypeTest(file, testSpec.getTestValue());
                    int count = isCountRequired(test);
                    if(count > 0){
                        if(result > count){
                            System.out.println("Variable type test: pass");
                        }else{
                            System.out.println("Variable type test: fail");
                        }
                    }else{
                        if(result > 0){
                            System.out.println("Variable type test: pass");
                        }else{
                            System.out.println("Variable type test: fail");
                        }
                    }
                });
            }
        });
    }

    private int isCountRequired(Test test) {
        for (int i = 0; i < test.getTestSpec().size(); i++){
            if(test.getTestSpec().get(i).getTestSpecification().equals("count")){
                return Integer.parseInt(test.getTestSpec().get(i).getTestValue());
            }
        }
        return 0;
    }

    private int runVariableTypeTest(FileJavaClass testedFile, String testValue) {
        int count = 0;
        String[] types = testValue.split(",");
        List<FileJavaClass> typeFiles = new ArrayList<>();
        System.out.println("Testing variable types: ");
        for (String type : types) {
            for (int j = 0; j < gui.getCompiledSolution().size(); j++) {
                String typeName = gui.getCompiledSolution().get(j).getaClass().getName();
                if (typeName.equals(type)) {
                    typeFiles.add(gui.getCompiledSolution().get(j));
                    System.out.println("Test type: " + type);
                }
            }
        }
        System.out.println("in a file: " + testedFile.getaClass().getName());

        Field[] fields = testedFile.getaClass().getDeclaredFields();
        for (Field field : fields) {
            for (FileJavaClass typeFile : typeFiles) {
                if (field.getType() == typeFile.getaClass()) {
                    count++;
                    System.out.println("Field: " + field.getName() + " matches: " + typeFile.getaClass().getName());
                }
            }
        }
        return count;
    }

    private void runClassTests(List<FileJavaClass> testFiles, Test test) {
        test.getTestSpec().forEach(testSpec -> {
            if(testSpec.getTestSpecification().equals("nameCondition")){
                testFiles.forEach(file -> runClassNameTest(file, testSpec.getTestValue()));
            }
        });
    }

    public void runClassNameTest(FileJavaClass testedFile, String value){

        Pattern pattern = Pattern.compile(value);
        Matcher matcher = pattern.matcher(testedFile.getaClass().getName());
        System.out.println("Test: " + value + ", " + testedFile.getaClass().getName());
        if(matcher.find()){
            System.out.println("Class name test: pass");
        }else{
            System.out.println("Class name test: fail");
        }
    }

    public void setGui(Gui gui) {
        this.gui = gui;
    }

    public List<Test> getTests() {
        return tests;
    }


}
