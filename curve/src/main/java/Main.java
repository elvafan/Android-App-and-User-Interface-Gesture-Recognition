import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // Create menu items
        MenuBar menubar = new MenuBar();

        //File menu
        Menu fileMenu = new Menu("File");
        MenuItem fileNew = new MenuItem("New");
        MenuItem fileLoad = new MenuItem("Load");
        MenuItem fileSave = new MenuItem("Save");
        MenuItem fileQuit = new MenuItem("Quit");
        fileMenu.getItems().addAll(fileNew, fileLoad,fileSave, fileQuit);

        //help menu
        Menu helpMenu = new Menu("Help");
        MenuItem helpAbout = new MenuItem("About");
        helpMenu.getItems().add(helpAbout);

        // Map accelerator keys to menu items
        fileNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        fileLoad.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));
        fileSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        fileQuit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));

        // Put menus together
        menubar.getMenus().addAll(fileMenu, helpMenu);

        // create and initialize the Model to hold our counter
        Model model = new Model();

        // create each view, and tell them about the model
        // the views will register themselves with the model
        ToolbarView toolbar = new ToolbarView(model);
        Canvasview view2 = new Canvasview(model);

        // Setup handlers
        fileNew.setOnAction(actionEvent -> {
            if (model.save()){
                String answer = ConfirmBox.Display("Confirm", "you have unsaved curve, save before starting new file?");
                if (answer.equals("save")) {
                    filesave(model,stage);// window close event stops here
                    model.clear();
                } else if (answer.equals("no")){
                    model.clear();
                }
            } else {
                model.clear();
            }
        });
        fileLoad.setOnAction(actionEvent -> {
            if (model.save()){
                String answer = ConfirmBox.Display("Confirm", "you have unsaved curve, save before starting loading?");
                if (answer.equals("save")) {
                    filesave(model,stage);
                    fileload(model,stage);
                } else if (answer.equals("no")){
                    fileload(model,stage);
                }
            } else {
                fileload(model,stage);
            }
        });
        fileSave.setOnAction(actionEvent -> {
            if (model.save()) {
                filesave(model,stage);
            } else {
                String save = "nothing to save or already saved";
                OkDialog.display(save);
            }

        });
        fileQuit.setOnAction(actionEvent -> {
            if (model.save()) {
                String answer = ConfirmBox.Display("Confirm", "you have unsaved curve, save before exiting?");
                if (answer == "save") {
                    filesave(model, stage);// window close event stops here
                    System.exit(0);
                } else if (answer == "no") {
                    System.exit(0);
                }
            } else {
                System.exit(0);
            }
        });

        helpAbout.setOnAction(actionEvent -> {
            String about = "Bezier Curve  XinyuanFan 20652220";
            OkDialog.display(about);
        });

        // setup the scene
        Label instructions = new Label("This app demonstrates how to setup menus. ");
        BorderPane root = new BorderPane();
        root.setTop(menubar);
        root.setLeft(toolbar);
        root.setCenter(view2);
        Scene scene = new Scene(root);

        scene.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ESCAPE)){
                if(model.isDrawing()){
                    model.exitdrawing();
                } else if (model.isSelection()){
                    if (model.getcurve() != null){
                        model.unselectcurve();
                    }
                    model.exitselection();
                }
            } else if (event.getCode().equals(KeyCode.DELETE)){
                if (model.getcurve() != null){
                    model.deletecurve();
                    model.exiterase();
                    model.exitselection();
                }
            }
        });

        // setup and show the window
        stage.setTitle("Bezier curve");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setWidth(900);
        stage.setHeight(600);
        stage.setMinWidth(640);
        stage.setMinHeight(480);
        stage.setMaxWidth(1920);
        stage.setMaxHeight(1440);
        stage.show();
    }

    void filesave(Model model, Stage stage){
        ArrayList<ArrayList<Model.curvestring>> alllist = model.getAllcurvestring();
        try {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showSaveDialog(stage);
            if (file != null){
                Gson gson = new Gson();
                Writer writer = new FileWriter(file.getName());
                gson.toJson(alllist, writer);
                writer.close();
                model.updatesave(alllist);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void fileload (Model model, Stage stage){
        try {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null){
                Gson gson = new Gson();
                Reader reader = Files.newBufferedReader(Paths.get(String.valueOf(file)));
                ArrayList<ArrayList<Model.curvestring>> list;
                list =new Gson().fromJson(reader,new TypeToken<ArrayList<ArrayList<Model.curvestring>>>() {}.getType());
                model.load(list);
                reader.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}