import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class SayHi extends Application {
    TextField nameField;
    Label greetLabel;
    @Override public void start(Stage primaryStage){
        Pane root = new FlowPane();
        root.setStyle("-fx-font-size:18");
        Label nameLabel = new Label("Namn:");
        nameField = new TextField();
        Button greetButton = new Button("Hälsa");

        greetButton.setOnAction(new Handler());

        greetLabel = new Label("Hej");
        root.getChildren().addAll(nameLabel, nameField, greetButton, greetLabel);

        Scene scene = new Scene(root, 450, 50); //bredd, höjd
        primaryStage.setScene(scene);
        primaryStage.show();
    } // start
    class Handler implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event){
            String name = nameField.getText();
            greetLabel.setText("Hej " + name);
        }
    }
 } // SayHi
