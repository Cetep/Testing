package tools;

import gui.Gui;

import javax.tools.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Petec on 5.11.2016.
 */
public class Compiler {
    private Gui gui;

    public Compiler(Gui gui) {
        this.gui = gui;
    }

    public List<FileJavaClass> compile(List<FileJavaSource> sourceJavaFiles, String libsDir, String compOutPath) {
        PrintWriter writer = null;
        boolean result;
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(diagnostics, null, null);
        Iterable options = Arrays.asList(
                    "-encoding", "UTF-8",
                    "-classpath", buildClassPath(libsDir),
                    "-d", compOutPath
        );
        result = javaCompiler.getTask(null, null, diagnostics, options, null, sourceJavaFiles).call();
        gui.getLogArea().insertText(0, "Compilation diagnostics: " + diagnostics.getDiagnostics() + '\n');
        gui.getLogArea().insertText(0, "Compilation result: " + result + '\n');

        return getClassFilesFromDir(compOutPath);
    }

    private static String buildClassPath(String libsDir) {
        String classPath = "";
        File[] listOfFiles = new File(libsDir).listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                if(listOfFiles[i].getName().endsWith(".jar")){
                    classPath = classPath + listOfFiles[i].getAbsolutePath() + ";";
                }
            }
        }
        return classPath;
    }

    public static List<FileJavaClass> getClassFilesFromDir(String solutionRootDir) {
        File[] listOfFiles = new File(solutionRootDir).listFiles();
        List<FileJavaClass> solutionSource = new ArrayList<>();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                if(listOfFiles[i].getName().endsWith(".class")){
                    solutionSource.add(new FileJavaClass(listOfFiles[i]));
                }
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory found: " + listOfFiles[i].getName() + "\n");
            }
        }
        return solutionSource;
    }
}