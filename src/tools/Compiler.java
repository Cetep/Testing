package tools;

import gui.Gui;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
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
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        Iterable options = Arrays.asList(
                    "-encoding", "UTF-8",
                    "-classpath", buildClassPath(libsDir),
                    "-d", compOutPath
        );
        boolean result = javaCompiler.getTask(null, null, diagnostics, options, null, sourceJavaFiles).call();
        gui.getLogArea().insertText(0, "Compilation diagnostics: " + diagnostics.getDiagnostics() + '\n');
        gui.getLogArea().insertText(0, "Compilation result: " + result + '\n');

        return getClassFilesFromDir(compOutPath);
    }

    public List<FileJavaClass> compileTestFiles(List<FileJavaSource> sourceJavaFiles, String libsDir, String compOutPath, List<FileJavaClass> compiledSolution) {
        File[] files = new File[compiledSolution.size()];
        for (int i = 0; i < compiledSolution.size(); i++){
            files[i] = compiledSolution.get(i).getFile();
        }
        if(!compiledSolution.isEmpty()){
            JarHandler.getInstance().createJarArchive(new File(libsDir + "/test.jar"), files);
        }
        return compile(sourceJavaFiles, libsDir, compOutPath);
    }

    private static String buildClassPath(String libsDir) {
        String classPath = "";
        File[] listOfFiles = new File(libsDir).listFiles();
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                if (listOfFile.getName().endsWith(".jar")) {
                    classPath = classPath + listOfFile.getAbsolutePath() + ";";
                }
            }
        }
        return classPath;
    }

    public static List<FileJavaClass> getClassFilesFromDir(String solutionRootDir) {
        File[] listOfFiles = new File(solutionRootDir).listFiles();
        List<FileJavaClass> solutionSource = new ArrayList<>();

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                if (listOfFile.getName().endsWith(".class")) {
                    solutionSource.add(new FileJavaClass(listOfFile));
                }
            } else if (listOfFile.isDirectory()) {
                System.out.println("Directory found: " + listOfFile.getName() + "\n");
            }
        }
        return solutionSource;
    }

}