package tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarHandler {
    private static JarHandler instance = null;

    protected JarHandler() {
    }

    public static JarHandler getInstance() {
        if(instance == null) {
            instance = new JarHandler();
        }
        return instance;
    }
    public static int BUFFER_SIZE = 10240;

    protected void createJarArchive(File archiveFile, File[] tobeJared) {
        try {
            byte buffer[] = new byte[BUFFER_SIZE];
            // Open archive file
            FileOutputStream stream = new FileOutputStream(archiveFile);
            JarOutputStream out = new JarOutputStream(stream, new Manifest());

            for (int i = 0; i < tobeJared.length; i++) {
                if (tobeJared[i] == null || !tobeJared[i].exists()
                        || tobeJared[i].isDirectory())
                    continue; // Just in case...
                System.out.println("Adding " + tobeJared[i].getName());

                // Add archive entry
                JarEntry jarAdd = new JarEntry(tobeJared[i].getName());
                jarAdd.setTime(tobeJared[i].lastModified());
                out.putNextEntry(jarAdd);

                // Write file to archive
                FileInputStream in = new FileInputStream(tobeJared[i]);
                while (true) {
                    int nRead = in.read(buffer, 0, buffer.length);
                    if (nRead <= 0)
                        break;
                    out.write(buffer, 0, nRead);
                }
                in.close();
            }

            out.close();
            stream.close();
            System.out.println("Adding completed OK");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
        }
    }

//    public void addJarToClassPath(File myJar){
//        URLClassLoader child = new URLClassLoader(myJar.toURL().toURI(), this.getClass().getClassLoader());
//
//        Class classToLoad = Class.forName ("com.MyClass", true, child);
//        Method method = classToLoad.getDeclaredMethod ("myMethod");
//        Object instance = classToLoad.newInstance ();
//        Object result = method.invoke (instance);
//    }
}
         