import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javassist.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

class FileActions {

    private final File file;
    private final String path;
    private ClassPool classPool;
    private CtClass ctClass;

    private final ArrayList<String> allClasses = new ArrayList<>();
    private final ArrayList<String> allPackages = new ArrayList<>();
    private final ArrayList<String> classesByPackage = new ArrayList<>();
    private final ArrayList<String> newClasses = new ArrayList<>();
    private final ArrayList<String> newPackages = new ArrayList<>();
    private final List<String> classFieldNames = new ArrayList<>();
    private final List<String> classConstructorNames = new ArrayList<>();
    private final List<String> classShortMethodNames = new ArrayList<>();



    public FileActions(File file, String path) {
        this.file = file;
        this.path = path;
    }

    public ArrayList<String> getNewClasses() {
        return newClasses;
    }
    public ArrayList<String> getNewPackages() { return newPackages; }

    public ArrayList<String> getAllClassesByPackage() {
        return classesByPackage;
    }

    public ArrayList<String> getAllPackages() {
        return allPackages;
    }


    public void getAllClassesAndPackagesNames() throws IOException, NotFoundException {
        ClassPool classPool;
        String className;
        String packageName;
        classPool = ClassPool.getDefault();
        classPool.insertClassPath(this.path);
        FileInputStream tmpFile = new FileInputStream(this.file);
        JarInputStream jarInputStream = new JarInputStream(tmpFile);
        JarEntry jarEntry;
        while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {

            if (jarEntry.isDirectory()) {
                packageName = jarEntry.getName();
                allPackages.add(packageName);
                continue;
            } else if (!jarEntry.getName().endsWith(".class")) {
                continue;
            }
            className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);
            allClasses.add(className);
        }

        allClasses.addAll(newClasses);
        allPackages.addAll(newPackages);

    }

    public void getClassesByPackage(String packageName) throws IOException, NotFoundException {
        String className;
        classPool = ClassPool.getDefault();
        classPool.insertClassPath(this.path);
        FileInputStream tmpFile = new FileInputStream(this.file);
        JarInputStream jarInputStream = new JarInputStream(tmpFile);
        JarEntry jarEntry;
        while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {

            if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
                continue; }

            if (jarEntry.getName().startsWith(packageName)) {
                className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);
                classesByPackage.add(className);
            }
        }

        for (String newClass: newClasses) {
            if (newClass.startsWith(packageName)) {
                classesByPackage.add(newClass);
            }
        }
    }

    public List<String> getClassFieldNames() {
        return classFieldNames;
    }


    public List<String> getClassConstructorNames() {
        return classConstructorNames;
    }


    public List<String> getClassShortMethodNames() {
        return classShortMethodNames;
    }


    public void getAttributesByClass(String className) {
        String newClassName = className.replace("/", ".");
        try {
            ctClass = classPool.get(newClassName);
            for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
                classShortMethodNames.add(ctMethod.getName());
            }

            for (CtField field : ctClass.getDeclaredFields()) {
                classFieldNames.add(field.getName());
            }

            for (CtConstructor constructor : ctClass.getDeclaredConstructors()) {
                classConstructorNames.add(constructor.getLongName());
            }

        } catch (Exception e) {
            System.out.println("There is some problem");
        }
    }


    public ArrayList<String> getClassesByPackage() {
        return classesByPackage;
    }


    public ArrayList<String> getAllClasses() {
        return allClasses;
    }


    public void clearClassesByPackage() {
        this.classesByPackage.clear();
    }

    public void clearClassAttributes() {
        this.classFieldNames.clear();
        this.classConstructorNames.clear();
        this.classShortMethodNames.clear();
    }


    public void addLibrary(String pathToLibrary, String outputDirectory) throws IOException {
        JarFile jarFile = new JarFile(pathToLibrary);
        Enumeration enumEntries = jarFile.entries();
        while (enumEntries.hasMoreElements()) {
            JarEntry file = (JarEntry) enumEntries.nextElement();
            if (file.getName().equals("MANIFEST.MF")) {
                continue;
            }
            File f = new File( outputDirectory + File.separator + file.getName());
            f.getParentFile().mkdirs();
            InputStream inputStream = jarFile.getInputStream(file);
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            while (inputStream.available() > 0) {
                fileOutputStream.write(inputStream.read());
            }
            fileOutputStream.close();
            inputStream.close();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Library added.");
        alert.showAndWait();
    }



    public void saveFile(String outputDirectory) throws NotFoundException, IOException {

        Optional<String> providedName;
        Dialog<String> dialog = new TextInputDialog();
        dialog.setTitle("You are saving file");
        dialog.setHeaderText("Enter file name (with extension)");
        providedName = dialog.showAndWait();
        String name = providedName.toString();
        name = name.substring(name.lastIndexOf("[")+1, name.lastIndexOf("]"));


        for(String className: getAllClasses()){

            String newClassName = className.replace("/", ".");
            this.classPool = ClassPool.getDefault();
            this.ctClass = this.classPool.get(newClassName);
            this.ctClass.defrost();
            this.ctClass.debugWriteFile(outputDirectory);
        }

        JarFile jarFile = new JarFile(path);
        Enumeration enumEntries = jarFile.entries();
        while (enumEntries.hasMoreElements()) {
            JarEntry file = (JarEntry) enumEntries.nextElement();

            if (file.getName().endsWith(".class")) {
                continue;
            }
            if (file.isDirectory()) {
                continue;
            }

            File f = new File( outputDirectory + File.separator + file.getName());
            f.getParentFile().mkdirs();
            InputStream inputStream = jarFile.getInputStream(file);
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            while (inputStream.available() > 0) {
                fileOutputStream.write(inputStream.read());
            }
            fileOutputStream.close();
            inputStream.close();


        }

        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "cd \""+outputDirectory + "\" && jar cfm "+ name + " META-INF/MANIFEST.MF *");
        builder.start();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("File " + name + " has been saved. You can find your new jar in " + outputDirectory);
        alert.showAndWait();
    }
}
