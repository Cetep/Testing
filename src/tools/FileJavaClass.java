package tools;

import javax.tools.SimpleJavaFileObject;
import java.io.File;

/**
 * Created by Petec on 5.11.2016.
 */
public class FileJavaClass extends SimpleJavaFileObject {
    private File file;
    private Class aClass;

    public FileJavaClass(File file) {
        super(file.toURI(), Kind.CLASS);
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }
}
