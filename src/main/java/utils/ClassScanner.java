package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClassScanner {

    public static List<Class<?>> getClasses(String packageName) throws Exception {
        String path = packageName.replace('.', '/');
        File directory = new File("src/main/java/" + path);

        List<Class<?>> classes = new ArrayList<>();
        for (File file : directory.listFiles()) {
            if (file.getName().endsWith(".java")) {
                String className = packageName + "." + file.getName().replace(".java", "");
                classes.add(Class.forName(className));
            }
        }
        return classes;
    }
}
