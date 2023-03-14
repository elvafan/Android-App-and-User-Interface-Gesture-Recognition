import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

//This class is from cs349 06 Layout 04 dialog

public class OkDialog {

    public static void display(String message) {
        Stage stage = new Stage();
        TextArea text = new TextArea(message);
        text.setWrapText(true);
        text.setPrefWidth(280);
        text.setPrefHeight(125);
        text.relocate(10, 10);
        text.setEditable(false);

        Button ok = new Button("Ok");
        ok.setPrefWidth(75);
        ok.relocate(130, 155);
        ok.setOnAction(event -> {
            stage.close();
        });

        Scene scene = new Scene(new Pane(
                text, ok), 300, 200);
        stage.setScene(scene);
        stage.setTitle("About");
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.show();
    }
}
