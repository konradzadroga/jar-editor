import javafx.scene.control.*;
import javassist.*;
import javassist.bytecode.BadBytecode;

import java.io.File;
import java.io.IOException;
import java.util.Optional;


class JarOperations {


    public void addMethod(String classPath) throws NotFoundException {

        Optional<String> providedCode;
        Dialog<String> dialog = new TextInputDialog();
        dialog.setTitle("You are creating a new method");
        dialog.setHeaderText("Enter the body of the method");
        providedCode = dialog.showAndWait();
        String newMethodBody = providedCode.toString();
        newMethodBody = newMethodBody.substring(newMethodBody.lastIndexOf("[")+1, newMethodBody.lastIndexOf("]"));
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(classPath);
        String newClassPath = classPath.replace("/", ".");
        try {
            CtClass ctClass = classPool.get(newClassPath);
            CtMethod newMethod = CtNewMethod.make(newMethodBody, ctClass);
            ctClass.addMethod(newMethod);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Method has been created.");
            alert.showAndWait();
        } catch (Exception e) {
            System.out.println("There is some problem");
        }
    }

    public void addField(String classPath) throws CannotCompileException, NotFoundException {

        Optional<String> providedCode;
        Dialog<String> dialog = new TextInputDialog();
        dialog.setTitle("You are creating a new field");
        dialog.setHeaderText("Enter the field");
        providedCode = dialog.showAndWait();
        String newFieldBody = providedCode.toString();
        newFieldBody =  newFieldBody.substring( newFieldBody.lastIndexOf("[")+1, newFieldBody.lastIndexOf("]"));
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(classPath);
        String newClassPath = classPath.replace("/", ".");
        CtClass ctClass = classPool.get(newClassPath);
        CtField newField = CtField.make(newFieldBody, ctClass);
        ctClass.addField(newField);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Field has been created.");
        alert.showAndWait();
    }

    public void addConstructor(String classPath) throws CannotCompileException, NotFoundException {

        Optional<String> providedCode;
        Dialog<String> dialog = new TextInputDialog();
        dialog.setTitle("You are creating a new constructor");
        dialog.setHeaderText("Enter the body of the constructor");
        providedCode = dialog.showAndWait();
        String newConstructorBody = providedCode.toString();
        newConstructorBody = newConstructorBody.substring(newConstructorBody.lastIndexOf("[")+1, newConstructorBody.lastIndexOf("]"));
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(classPath);
        String newClassPath = classPath.replace("/", ".");
        CtClass ctClass = classPool.get(newClassPath);
        CtConstructor newConstructor = CtNewConstructor.make(newConstructorBody, ctClass);
        ctClass.addConstructor(newConstructor);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Constructor has been created.");
        alert.showAndWait();
    }

    public String addClass(String packageName) {

        Optional<String> providedCode;
        Dialog<String> dialog = new TextInputDialog();
        dialog.setTitle("You are creating a new class");
        dialog.setHeaderText("Enter the name of new class");
        providedCode = dialog.showAndWait();
        String newClass = providedCode.toString();
        newClass = newClass.substring(newClass.lastIndexOf("[") + 1, newClass.lastIndexOf("]"));
        String newClassPath = packageName + newClass;
        String newClassPathWithDots = newClassPath.replace("/", ".");
        ClassPool classPool = ClassPool.getDefault();
        classPool.makeClass(newClassPathWithDots);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Class " + newClass + " has been created.");
        alert.showAndWait();


        return newClassPath;

    }

    public String addInterface(String packageName) {

        Optional<String> providedCode;
        Dialog<String> dialog = new TextInputDialog();
        dialog.setTitle("You are creating a new interface");
        dialog.setHeaderText("Enter the name of new interface");
        providedCode = dialog.showAndWait();
        String newInterface = providedCode.toString();
        newInterface = newInterface.substring(newInterface.lastIndexOf("[") + 1, newInterface.lastIndexOf("]"));
        String newInterfacePath = packageName + newInterface;
        String newInterfacePathWithDots = newInterfacePath.replace("/", ".");
        ClassPool classPool = ClassPool.getDefault();
        classPool.makeInterface(newInterfacePathWithDots);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Interface " + newInterface + " has been created.");
        alert.showAndWait();


        return newInterfacePath;

    }

    public void addJython(String outPath) throws NotFoundException, CannotCompileException, IOException {
        String newClass= "application.JythonAndSocketManage";
        ClassPool classPool = ClassPool.getDefault();
        CtClass threadClass = classPool.get("java.lang.Thread");
        CtClass ctClass = classPool.makeClass(newClass);
        ctClass.setSuperclass(threadClass);
        ctClass.defrost();
        ctClass.writeFile(outPath);
        ctClass.defrost();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("public void run(){\n").append(
                "        System.out.println(\"Socket started on port 4200\");\n").append(
                "        try {\n").append(
                "            java.net.ServerSocket serverSocket = new java.net.ServerSocket(4200);\n").append(
                "            java.net.Socket socket = serverSocket.accept();\n").append(
                "        javax.script.ScriptEngineManager scriptEngineManager = new javax.script.ScriptEngineManager();\n").append(
                "        javax.script.ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(\"python\");\n").append(
                "        try {\n").append(
                "            java.util.Scanner scanner = new java.util.Scanner(new java.io.BufferedInputStream(socket.getInputStream()));\n").append(
                "            {\n").append(
                "                while (scanner.hasNextLine()) {\n" ).append(
                "                    java.lang.String comingLine = scanner.nextLine();\n").append(
                "                    scriptEngine.eval(comingLine);\n").append(
                "                };\n").append(
                "\n").append(
                "            }\n").append(
                "        }catch(javax.script.ScriptException e){\n").append(
                "                System.out.println(\"Script error\");\n").append(
                "            };\n").append(
                "\n").append(
                "        } catch (java.io.IOException e) {\n").append(
                "            System.out.println(\"Wrong input\");\n").append(
                "        };\n").append(

                "    }");
        try {
            ctClass = classPool.get(newClass);
            CtMethod newMethod = CtNewMethod.make(stringBuilder.toString(), ctClass);
            ctClass.addMethod(newMethod);
            newMethod.getMethodInfo().rebuildStackMapForME(classPool);
            ctClass.writeFile(outPath);

            ctClass = classPool.get("application.FXController");

            CtField newField = CtField.make("application.JythonAndSocketManage jython = new application.JythonAndSocketManage();",ctClass);
            ctClass.addField(newField);
            ctClass.writeFile();
            ctClass.defrost();

            CtMethod ctMethod = ctClass.getDeclaredMethod("initialize");
            ctMethod.insertAfter("jython.start();");
            ctMethod.getMethodInfo().rebuildStackMapForME(classPool);
            ctClass.writeFile();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Jython has been added.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public String addPackage(String path) {
        Optional<String> providedPackageName;
        Dialog<String> dialog = new TextInputDialog();
        dialog.setTitle("You are creating new package");
        dialog.setHeaderText("Enter name of the package");
        providedPackageName = dialog.showAndWait();
        String packageName = providedPackageName.toString();
        packageName = packageName.substring(packageName.lastIndexOf("[") + 1, packageName.lastIndexOf("]"));
        File file = new File(path+"/"+packageName);
        file.mkdirs();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Package " + packageName + " has been created.");
        alert.showAndWait();

        return packageName;
    }

    public void deletePackage(String packageName) {
        File file = new File(packageName);
        file.delete();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Package " + packageName + " has been deleted.");
        alert.showAndWait();
    }

    public void deleteMethod(String classPath, String methodName) throws NotFoundException {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(classPath);
        String newClassPath = classPath.replace("/", ".");
        CtClass ctClass = classPool.get(newClassPath);
        CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);
        ctClass.removeMethod(ctMethod);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Method " + methodName + " has been deleted.");
        alert.showAndWait();
    }

    public void deleteClass(String classPath) {
        String newClassPath = classPath.replace("/",".");
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get(newClassPath);
            ctClass.detach();
        } catch (NotFoundException e) {
            System.out.println("Couldnt add class");
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Class " + classPath + " has been deleted.");
        alert.showAndWait();
    }

    public void deleteInterface(String interfacePath) {
        String newInterfacePath = interfacePath.replace("/",".");
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get(newInterfacePath);
            ctClass.detach();
        } catch (NotFoundException e) {
            System.out.println("Couldnt add interface");
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Interface " + newInterfacePath + " has been deleted.");
        alert.showAndWait();
    }



    public void deleteField(String classPath, String fieldName) throws NotFoundException {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(classPath);
        String newClassPath = classPath.replace("/", ".");
        CtClass ctClass = classPool.get(newClassPath);
        CtField ctField = ctClass.getDeclaredField(fieldName);
        ctClass.removeField(ctField);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Field " + fieldName + " has been deleted.");
        alert.showAndWait();
    }

    public void deleteConstructor(String classPath, String constructorName) throws NotFoundException {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(classPath);
        String newClassPath = classPath.replace("/", ".");
        CtClass ctClass = classPool.get(newClassPath);

        for(CtConstructor constructor: ctClass.getDeclaredConstructors()){
            if(constructor.getLongName().equals(constructorName)){
                ctClass.removeConstructor(constructor);
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Constructor " + constructorName + " has been deleted.");
        alert.showAndWait();
    }

    public void appendCodeAtBeginningOfTheMethod(String classPath, String methodName) throws NotFoundException, IOException, CannotCompileException {
        Optional<String> providedCode;
        Dialog<String> dialog = new TextInputDialog();
        dialog.setTitle("You are adding code at the beginning of the method");
        dialog.setHeaderText("Enter the code you want to add");
        providedCode = dialog.showAndWait();
        String code = providedCode.toString();
        code = code.substring(code.lastIndexOf("[")+1, code.lastIndexOf("]"));
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(classPath);
        String newClassPath = classPath.replace("/", ".");
        CtClass ctClass = classPool.get(newClassPath);
        CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);
        ctMethod.insertBefore(code);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("You appended code " + code + " at the beginning of method " + methodName);
        alert.showAndWait();
    }

    public void appendCodeAtEndOfTheMethod(String classPath, String methodName) throws NotFoundException, IOException, CannotCompileException {
        Optional<String> providedCode;
        Dialog<String> dialog = new TextInputDialog();
        dialog.setTitle("You are adding code at the end of the method");
        dialog.setHeaderText("Enter the code you want to add");
        providedCode = dialog.showAndWait();
        String code = providedCode.toString();
        code = code.substring(code.lastIndexOf("[")+1, code.lastIndexOf("]"));
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(classPath);
        String newClassPath = classPath.replace("/", ".");
        CtClass ctClass = classPool.get(newClassPath);
        CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);
        ctMethod.insertAfter(code);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("You appended code " + code + " at the end of method " + methodName);
        alert.showAndWait();
    }

    public void overwriteCodeOfTheMethod(String classPath, String methodName) throws NotFoundException, IOException, CannotCompileException {
        Optional<String> providedCode;
        Dialog<String> dialog = new TextInputDialog();
        dialog.setTitle("You are overwriting the method");
        dialog.setHeaderText("Enter the code");
        providedCode = dialog.showAndWait();
        String code = providedCode.toString();
        code = code.substring(code.lastIndexOf("[")+1, code.lastIndexOf("]"));

        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(classPath);
        String newClassPath = classPath.replace("/", ".");
        CtClass ctClass = classPool.get(newClassPath);
        CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);
        try {
            ctMethod.setBody(code);
        } catch (NullPointerException e) {
            System.out.println("Null pointer exception");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("You overwrote method " + methodName + " with code " + code);
        alert.showAndWait();
    }


    public void overwriteCodeOfConstructor(String classPath, String constructorName) throws NotFoundException, IOException, CannotCompileException, BadBytecode {
        CtConstructor ctConstructor = null;
        Optional<String> providedCode;
        Dialog<String> dialog = new TextInputDialog();
        dialog.setTitle("You are overwriting the constructor");
        dialog.setHeaderText("Enter the code");
        providedCode = dialog.showAndWait();
        String code = providedCode.toString();

        code = code.substring(code.lastIndexOf("[")+1, code.lastIndexOf("]"));
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(classPath);
        String newClassPath = classPath.replace("/", ".");
        CtClass ctClass = classPool.get(newClassPath);
        for(CtConstructor constructor: ctClass.getDeclaredConstructors()){
            if(constructor.getLongName().equals(constructorName)){
                ctConstructor = constructor;
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("You overwrote constructor " + constructorName + " with code " + code);
        alert.showAndWait();

        try {
            assert ctConstructor != null;
            ctConstructor.setBody(code);
        } catch (NullPointerException e) {
            System.out.println("Null pointer");
        }
    }

    public void showErrorMessage() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("There seems to be a problem.");
        alert.showAndWait();
    }




}
