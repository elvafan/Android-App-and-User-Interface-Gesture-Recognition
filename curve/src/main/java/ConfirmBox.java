import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

//This class is from cs349 02 Javafx 08 user_prompt

public class ConfirmBox {
    static String answer;

    public static String Display(String title, String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label(message);
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        Button yesButton= new Button("Save");
        yesButton.setOnAction(event -> {
            answer = "save";
            window.close();
        });
        Button noButton= new Button("No");
        noButton.setOnAction(event -> {
            answer = "no";
            window.close();
        });

        Button cancelButton= new Button("Cancel");
        cancelButton.setOnAction(event -> {
            answer = "cancel";
            window.close();
        });


        VBox layout = new VBox();
        HBox lay = new HBox();
        lay.setSpacing(6);
        lay.getChildren().addAll(yesButton, noButton,cancelButton);
        lay.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        layout.getChildren().addAll(label, lay);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 200);
        window.setScene(scene);
        window.showAndWait();
        return answer;
    }
}
