import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javassist.NotFoundException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button newMethodButton;
    @FXML
    private Button newFieldButton;
    @FXML
    private Button newConstructorButton;
    @FXML
    private Button deleteMethodButton;
    @FXML
    private Button deleteFieldButton;
    @FXML
    private Button deleteConstructorButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button beginningCodeButton;
    @FXML
    private Button endCodeButton;
    @FXML
    private Button overwriteMethodButton;
    @FXML
    private Button overwriteConstructorButton;
    @FXML
    private Button addPackageButton;
    @FXML
    private Button packageDeleteButton;
    @FXML
    private Button addClassButton;
    @FXML
    private Button deleteClassButton;
    @FXML
    private Button addLibraryButton;
    @FXML
    private Button newInterfaceButton;
    @FXML
    private Button deleteInterfaceButton;


    private File file;
    private String outputPath;
    private FileActions fileActions;
    @FXML
    private ListView<String> packagesListView;
    @FXML
    private ListView<String> classesListView;
    @FXML
    private ListView<String> fieldsListView;
    @FXML
    private ListView<String> constructorsListView;
    @FXML
    private ListView<String> methodsListView;
    @FXML
    private TextField outputDirectory;

    private final JarOperations jarOperations = new JarOperations();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        addLibraryButton.setDisable(true);

        newMethodButton.disableProperty().bind(
                classesListView.getSelectionModel().selectedItemProperty().isNull()
        );
        newConstructorButton.disableProperty().bind(
                classesListView.getSelectionModel().selectedItemProperty().isNull()
        );
        newFieldButton.disableProperty().bind(
                classesListView.getSelectionModel().selectedItemProperty().isNull()
        );

        deleteFieldButton.disableProperty().bind(
                fieldsListView.getSelectionModel().selectedItemProperty().isNull()
        );


        deleteMethodButton.disableProperty().bind(
                methodsListView.getSelectionModel().selectedItemProperty().isNull()
        );


        deleteConstructorButton.disableProperty().bind(
                constructorsListView.getSelectionModel().selectedItemProperty().isNull()
        );

        beginningCodeButton.disableProperty().bind(
                methodsListView.getSelectionModel().selectedItemProperty().isNull()
        );

        endCodeButton.disableProperty().bind(
                methodsListView.getSelectionModel().selectedItemProperty().isNull()
        );

        overwriteMethodButton.disableProperty().bind(
                methodsListView.getSelectionModel().selectedItemProperty().isNull()
        );

        overwriteConstructorButton.disableProperty().bind(
                constructorsListView.getSelectionModel().selectedItemProperty().isNull()
        );

        addClassButton.disableProperty().bind(
                packagesListView.getSelectionModel().selectedItemProperty().isNull()
        );

        deleteClassButton.disableProperty().bind(
                classesListView.getSelectionModel().selectedItemProperty().isNull()
        );

        newInterfaceButton.disableProperty().bind(
                packagesListView.getSelectionModel().selectedItemProperty().isNull()
        );

        deleteInterfaceButton.disableProperty().bind(
                classesListView.getSelectionModel().selectedItemProperty().isNull()
        );


        addPackageButton.setDisable(true);
        packageDeleteButton.setDisable(true);
        saveButton.setDisable(true);


    }




    @FXML
    private void fileOpen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        this.file = fileChooser.showOpenDialog(null);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JAR", "*.jar"));
        try {
            String path = this.file.getPath();
            fileActions = new FileActions(this.file, path);
            fileActions.getAllClassesAndPackagesNames();
            packagesListView.setItems(FXCollections.observableArrayList(
                    fileActions.getAllPackages()
            ));


            packagesListView.setOnMouseClicked(event1 -> {
                String packageName;
                packageName = packagesListView.getSelectionModel().getSelectedItem();
                fileActions.clearClassesByPackage();
                try {
                    fileActions.getClassesByPackage(packageName);
                } catch (IOException e) {
                    System.out.println("Input/Output Exception");
                } catch (NotFoundException e) {
                    System.out.println("File not found");
                }
                classesListView.setItems(FXCollections.observableArrayList(
                        fileActions.getClassesByPackage()
                ));
            });

            classesListView.setOnMouseClicked(event12 -> {
                String className;
                className = classesListView.getSelectionModel().getSelectedItem();
                fileActions.clearClassAttributes();
                fileActions.getAttributesByClass(className);
                fieldsListView.setItems(FXCollections.observableList(
                        fileActions.getClassFieldNames()
                ));
                constructorsListView.setItems(FXCollections.observableList(
                        fileActions.getClassConstructorNames()
                ));
                methodsListView.setItems(FXCollections.observableList(
                        fileActions.getClassShortMethodNames()
                ));
            });
        } catch (Exception e) {
            System.out.println("File not chosen");
        }


    }




    public void saveFile(ActionEvent actionEvent) {
        try {
            fileActions.saveFile(this.outputPath);
        } catch (Exception e) {
            jarOperations.showErrorMessage();
        }
    }

    public void createNewMethod(ActionEvent actionEvent) {
        try {
            jarOperations.addMethod(classesListView.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            jarOperations.showErrorMessage();
        }
    }

    public void createNewField(ActionEvent actionEvent) {
        try {
            jarOperations.addField(classesListView.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            jarOperations.showErrorMessage();
        }
    }

    public void createNewConstructor(ActionEvent actionEvent) {
        try {
            jarOperations.addConstructor(classesListView.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            jarOperations.showErrorMessage();
        }
    }

    public void deleteMethod() {
        try {
            jarOperations.deleteMethod(classesListView.getSelectionModel().getSelectedItem(), methodsListView.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            jarOperations.showErrorMessage();
        }
    }

    public void deleteField() {
        try {
            jarOperations.deleteField(classesListView.getSelectionModel().getSelectedItem(), fieldsListView.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            jarOperations.showErrorMessage();
        }
    }

    public void deleteConstructor() {
        try {
            jarOperations.deleteConstructor(classesListView.getSelectionModel().getSelectedItem(), constructorsListView.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            jarOperations.showErrorMessage();
        }
    }

    public void appendCodeAtBeginningOfTheMethod() {
        try {
            jarOperations.appendCodeAtBeginningOfTheMethod(classesListView.getSelectionModel().getSelectedItem(), methodsListView.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            jarOperations.showErrorMessage();
        }
    }

    public void appendCodeAtTheEndOfTheMethod() {
        try {
            jarOperations.appendCodeAtEndOfTheMethod(classesListView.getSelectionModel().getSelectedItem(), methodsListView.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            jarOperations.showErrorMessage();
        }
    }

    public void overwriteCodeOfTheMethod() {
        try {
            jarOperations.overwriteCodeOfTheMethod(classesListView.getSelectionModel().getSelectedItem(), methodsListView.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            jarOperations.showErrorMessage();
        }
    }

    public void addClass() {
        try {
            fileActions.getNewClasses().add(jarOperations.addClass(packagesListView.getSelectionModel().getSelectedItem()));
            fileActions.getAllClassesAndPackagesNames();

        } catch (Exception e) {
            jarOperations.showErrorMessage();
        }
    }

    public void addInterface() {
        try {
            fileActions.getNewClasses().add(jarOperations.addInterface(packagesListView.getSelectionModel().getSelectedItem()));
            fileActions.getAllClassesAndPackagesNames();
        } catch (Exception e)  {
            jarOperations.showErrorMessage();
        }
    }

    public void overwriteTheConstructor() {
        try {
            jarOperations.overwriteCodeOfConstructor(classesListView.getSelectionModel().getSelectedItem(), constructorsListView.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            jarOperations.showErrorMessage();
        }
    }

    public void addPackage() {
        try {
            fileActions.getNewPackages().add(jarOperations.addPackage(this.outputPath));
            fileActions.getAllClassesAndPackagesNames();
            packagesListView.setItems(FXCollections.observableArrayList(
                    fileActions.getAllPackages()
            ));
        } catch (Exception e) {
            jarOperations.showErrorMessage();
        }
    }

    public void deletePackage() {
        try {
            String selected = packagesListView.getSelectionModel().getSelectedItem();

            if (fileActions.getNewPackages().size()>0) {
                for (int i=0; i < fileActions.getNewPackages().size(); i++) {
                    if (fileActions.getNewPackages().get(i).equals(selected)) {
                        fileActions.getNewPackages().remove(fileActions.getNewPackages().get(i));
                    }
                }
            }

            if (fileActions.getAllPackages().size()>0) {
                for (int i = 0; i < fileActions.getAllPackages().size(); i++) {
                    if (fileActions.getAllPackages().get(i).equals(selected)) {
                        fileActions.getAllPackages().remove(fileActions.getAllPackages().get(i));
                    }
                }
            }

            jarOperations.deletePackage(packagesListView.getSelectionModel().getSelectedItem());

            packagesListView.setItems(FXCollections.observableArrayList(
                    fileActions.getAllPackages()
            ));


        } catch (Exception e) {
            e.printStackTrace();
            //jarOperations.showErrorMessage();
        }
    }

    public void deleteClass() {
        try {

            String selected = classesListView.getSelectionModel().getSelectedItem();

            jarOperations.deleteClass(selected);


            if (fileActions.getNewClasses().size()>0) {
                for (int i=0; i < fileActions.getNewClasses().size(); i++) {
                    if (fileActions.getNewClasses().get(i).equals(selected)) {
                        fileActions.getNewClasses().remove(fileActions.getNewClasses().get(i));
                    }
                }
            }

            if (fileActions.getAllClasses().size()>0) {
                for (int i = 0; i < fileActions.getAllClasses().size(); i++) {
                    if (fileActions.getAllClasses().get(i).equals(selected)) {
                        fileActions.getAllClasses().remove(fileActions.getAllClasses().get(i));
                    }
                }
            }

            if (fileActions.getClassesByPackage().size()>0) {
                for (int i = 0; i < fileActions.getAllClassesByPackage().size(); i++) {
                    if (fileActions.getAllClassesByPackage().get(i).equals(selected)) {
                        fileActions.getAllClassesByPackage().remove(fileActions.getAllClassesByPackage().get(i));
                    }
                }
            }


        } catch (Exception e) {
            jarOperations.showErrorMessage();
        }
    }

    public void deleteInterface() {
        try {

            String selected = classesListView.getSelectionModel().getSelectedItem();

            jarOperations.deleteInterface(selected);


            if (fileActions.getNewClasses().size()>0) {
                for (int i=0; i < fileActions.getNewClasses().size(); i++) {
                    if (fileActions.getNewClasses().get(i).equals(selected)) {
                        fileActions.getNewClasses().remove(fileActions.getNewClasses().get(i));
                    }
                }
            }

            if (fileActions.getAllClasses().size()>0) {
                for (int i = 0; i < fileActions.getAllClasses().size(); i++) {
                    if (fileActions.getAllClasses().get(i).equals(selected)) {
                        fileActions.getAllClasses().remove(fileActions.getAllClasses().get(i));
                    }
                }
            }

            if (fileActions.getClassesByPackage().size()>0) {
                for (int i = 0; i < fileActions.getAllClassesByPackage().size(); i++) {
                    if (fileActions.getAllClassesByPackage().get(i).equals(selected)) {
                        fileActions.getAllClassesByPackage().remove(fileActions.getAllClassesByPackage().get(i));
                    }
                }
            }


        } catch (Exception e) {
            jarOperations.showErrorMessage();
        }
    }


    public void chooseSavingDirectory(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File outputFile = directoryChooser.showDialog(null);

        try {
            if (file != null) {
                this.outputPath = outputFile.getPath();
                this.outputDirectory.setText(this.outputPath);
                addLibraryButton.setDisable(false);
            }
        } catch (Exception e) {
            jarOperations.showErrorMessage();
        }

        addPackageButton.setDisable(false);
        packageDeleteButton.setDisable(false);
        saveButton.setDisable(false);

    }


    public void addLibrary() {
        FileChooser fileChooser = new FileChooser();
        File library = fileChooser.showOpenDialog(null);
        try {
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JAR", "*.jar"));
            String path = library.getPath();
            fileActions.addLibrary(path, this.outputPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    }