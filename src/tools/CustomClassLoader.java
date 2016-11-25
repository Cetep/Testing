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

    public void loadClasses(List<FileJavaClass> sourceFiles){
        if(sourceFiles.isEmpty()){
            System.out.println("Error: No source files.");
        }else{

            String path = sourceFiles.get(0).getFile().getAbsolutePath();
            String dirPath = path.substring(0, path.length() - sourceFiles.get(0).getFile().getName().length());

            URL[] urls = null;
            try {
                URL url = new URL("file:/" + dirPath);
                urls = new URL[] {url};
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
        }
    }
}
