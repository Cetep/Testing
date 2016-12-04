package tools;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * Created by Petec on 23.11.2016.
 */
public class CustomClassLoader {

    private static CustomClassLoader instance = null;

    protected CustomClassLoader() {

    }

    public static CustomClassLoader getInstance() {
        if(instance == null) {
            instance = new CustomClassLoader();
        }
        return instance;
    }

    public void loadClasses(List<FileJavaClass> sourceFiles, List<FileJavaClass> testFiles){
        if(sourceFiles.isEmpty()){
            System.out.println("Error: No source files.");
        }if (testFiles.isEmpty()){
            System.out.println("Error: No test files.");
        }else{

            String sourcePath = sourceFiles.get(0).getFile().getAbsolutePath();
            String testPath = testFiles.get(0).getFile().getAbsolutePath();
            String sourceDir = sourcePath.substring(0, sourcePath.length() - sourceFiles.get(0).getFile().getName().length());
            String testDir = testPath.substring(0, testPath.length() - testFiles.get(0).getFile().getName().length());
            System.out.println("Source DIR: " + sourceDir);
            System.out.println("Test DIR: " + testDir);

            URL[] urls = null;

            try {
                URL sourceURL = new URL("file:/" + sourceDir);
                URL testURL = new URL("file:/" + testDir);
                urls = new URL[] {sourceURL, testURL};

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            URLClassLoader cls = new URLClassLoader(urls);

            sourceFiles.forEach(fileJavaClass -> {
                try {
                    String name = fileJavaClass.getFile().getName().substring(0, fileJavaClass.getFile().getName().length() - 6);
                    fileJavaClass.setaClass(cls.loadClass(name));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });



            testFiles.forEach(fileJavaClass -> {
                try {
                    String name = fileJavaClass.getFile().getName().substring(0, fileJavaClass.getFile().getName().length() - 6);
                    fileJavaClass.setaClass(cls.loadClass(name));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
