import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.stage.Stage;

public class PathFinder extends Application  {


    private ImageView imageView = new ImageView();
    private MenuBar fileMenuBar = new MenuBar();
    private HBox btnMenu = new HBox(20); // MN: pixel spacing between components within menu
    private Stage stage;

    private ListGraph graph = new ListGraph();

    // --------------------------------------------


    public void start(Stage primaryStage){
        this.stage = primaryStage;
        VBox root = new VBox();

        Menu fileMenu = new Menu("File");
        fileMenuBar.getMenus().addAll(fileMenu);

        MenuItem menuNewMap = new MenuItem("New Map");
        MenuItem menuOpenFile = new MenuItem("Open");
        MenuItem menuSave = new MenuItem("Save");
        MenuItem menuSaveImage = new MenuItem("Save Image");
        MenuItem menuExit = new MenuItem("Exit");

        menuNewMap.setOnAction(new NewMapHandler());
        menuExit.setOnAction(actionEvent -> Platform.exit());

        fileMenuBar.setId("menu");
        fileMenu.setId("menuFile");
        menuNewMap.setId("menuNewMap");
        menuOpenFile.setId("menuOpenFile");
        menuSave.setId("menuSave");
        menuSaveImage.setId("menuSaveImage");
        menuExit.setId("menuExit");

        fileMenu.getItems().addAll(menuNewMap, menuOpenFile, menuSave, menuSaveImage, menuExit);

        Button findPathBtn = new Button("Find path");
        Button showConnectionBtn = new Button("Show Connection");
        Button newPlaceBtn = new Button("New place");
        Button newConnectionBtn = new Button("New Connection");
        Button changeConnectionBtn = new Button("New place");

        btnMenu.setId("btnMenu");
        findPathBtn.setId("btnFindPath");
        showConnectionBtn.setId("btnShowConnection");
        newPlaceBtn.setId("btnNewPlace");
        newConnectionBtn.setId("btnNewConnection");
        changeConnectionBtn.setId("btnChangeConnection");

        btnMenu.getChildren().addAll(findPathBtn, showConnectionBtn, newPlaceBtn, newConnectionBtn, changeConnectionBtn);
        btnMenu.setAlignment(Pos.CENTER);
        btnMenu.setMinHeight(60);

        // add container boxes to root node
        root.getChildren().addAll(fileMenuBar, btnMenu, imageView);

        Scene scene = new Scene (root, 618, 80);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PathFinder");
        primaryStage.show();
    }

    class NewMapHandler implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event){
            if (imageView.getImage() == null){ // ändra refactor baserat på boolean sats
                Image image = new Image("europa.gif");
                imageView.setImage(image);
                stage.setWidth(image.getWidth());
                stage.setHeight(image.getHeight() + stage.getHeight());
            }// skapa else sats för att radera karta på en map som redan har data.
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}


