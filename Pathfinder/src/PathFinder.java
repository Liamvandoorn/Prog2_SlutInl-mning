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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.*;
import java.util.*;

import javafx.stage.Stage;


/**
 * Application app
 * Contains:
 * @main – Launch code
 * @Checkers
 * @Getters
 * @Setters
 * @start – Method that builds the whole application
 */

public class PathFinder extends Application  {

    private final ImageView imageView = new ImageView();
    private final MenuBar fileMenuBar = new MenuBar();
    private final HBox btnMenu = new HBox(20); // MN: pixel spacing between components within menu
    private Stage stage;

    private ListGraph<Location> graph = new ListGraph<Location>();

    private boolean lastEditIsSaved = true;

    // CHECKERS :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::.


    // GETTERS :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::.

    public boolean getLastEditIsSaved(){
        return lastEditIsSaved;
    }

    // SETTER :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::.

    public static void main(String[] args) {
        launch(args);
    }

    private void loadMap(String strPath){
        if (strPath.isEmpty()){
            strPath = "europa.gif";
        }
        Image image = new Image(strPath);
        imageView.setImage(image);
        stage.setWidth(image.getWidth());
        stage.setHeight(image.getHeight() + stage.getHeight());
    }

    // Location -----------------------------------


    // Location/Map Save -----------------------------------

    /**
     * Helper method that consist of three parts
     * {@code 1. write in gif path }
     * {@code 2. write in the nodes }
     * {@code 3. write in the connections }
     */
    private void saveMap() {
        try {
            FileWriter fw = new FileWriter("./PathFinder/src/europa.graph");
            BufferedWriter bw = new BufferedWriter(fw);
            // write in gif path
            bw.write("file:europa.gif\n");

            // write in the nodes
            for (Location location : graph.getNodes()) {
                String[] parts = location.toString().split("\\s=\\s\\[|]|,\\s"); // https://regex101.com/
                //System.out.println(Arrays.toString(parts));
                bw.write(
                        parts[0] + ";" +
                            parts[1] + ";" +
                            parts[2] + ";");
            }
            // POSSIBLE VPL ISSUE: appends ; at the end, i don't know if they check for that or if that's allowed.
            bw.write("\n");

            // write in the connections
            for (Location location : graph.getNodes()) {
                for (Edge<Location> connections : graph.getEdgesFrom(location)){
                    bw.write(
                            location.getName() + ";"
                                    + connections.getDestination().getName() + ";"
                                    + connections.getName() + ";"
                                    + connections.getWeight() + "\n");
                }
            }
            bw.flush();
            fw.close();
            bw.close();
        } catch (IOException e){
            System.out.println(e);
        }

    }

    // Location Load -----------------------------------

    public void loadLocationGraph(String fileName) {
        try {
            //read lines from textfile and split to retrieve information and create node
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            // load first line into list
            String mapPath = br.readLine();
            String[] a = mapPath.split(":");
            loadMap(a[1]);
            String[] tmp = br.readLine().split(";");
            List<String> listOfLocationStrings = Arrays.asList(tmp);
            createLocationNodesFromList(listOfLocationStrings);
            connectLocationNodes(br);

            fr.close();
            br.close();
        } catch (FileNotFoundException e ){
            //return;
            System.out.println("File not found: " + e);
        } catch (Exception e) {
            //return;
            System.out.println("Exception:" + e);
        }
    }

    /**
     * Helper method
     */
    private void connectLocationNodes(BufferedReader br){
        try {
            Set<Location> allNodes  = graph.getNodes();
            HashMap<String, Location> nodeMap = new HashMap<>();
            for (Location node : allNodes) {
                nodeMap.put(node.getName(), node);
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] connectionParts = line.split(";");

                Location from = nodeMap.get(connectionParts[0]);
                Location to = nodeMap.get(connectionParts[1]);
                String name = connectionParts[2];
                int weight = Integer.parseInt(connectionParts[3]);

                graph.connect(from, to, name, weight);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Void Helper method. Takes in a List of String that uses the index 0,1,2 to create what is needed to instantiate
     * The location node
     * @param listOfLocationStrings
     */
    private void createLocationNodesFromList(List<String> listOfLocationStrings) {
        try {
            String locationName = "";
            double x = 99999;
            double y = 99999;
            int count = 0;
            for (String str : listOfLocationStrings) {
                if (count == 0) {
                    locationName = str;
                    count += 1;
                } else if (count == 1) {
                    x = Double.parseDouble(str);
                    count += 1;
                } else if (count == 2) {
                    y = Double.parseDouble(str);
                    Location locationNode = new Location(locationName, x, y);
                    graph.add(locationNode);
                    count = 0;
                }
            }
        } catch (Error e) {
            //System.out.println("Error occured: " + e);
        }
    }


    // START :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
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
        menuOpenFile.setOnAction(new OpenMapHandler());
        menuExit.setOnAction(actionEvent -> Platform.exit());
        menuSave.setOnAction(new SaveHandler());


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

        /*

        */

        // add container boxes to root node
        root.getChildren().addAll(fileMenuBar, btnMenu, imageView);

        Scene scene = new Scene (root, 618, 80);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PathFinder");
        primaryStage.show();
    }

    // Handler Classes :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::


    class NewMapHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            if (imageView.getImage() == null){ // ändra refactor baserat på boolean sats
                loadMap("");
            } else {
                // spara sats om ny data har lagts till. ATT GÖRA:
                if (!lastEditIsSaved){
                    showAlert(AlertType.CONFIRMATION, "Warning!", "Unsaved changes, continue anyway?");
                }
                System.out.println("BEFORE" + graph.toString());
                graph  = new ListGraph<Location>();
                System.out.println("AFTER" + graph.toString());
                loadMap("");
            }
        }
    }

    class SaveHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event) {
            saveMap();
        }
    }

    private static void showAlert(AlertType alertType, String title, String headerText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }

    class OpenMapHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            String filePath = "./PathFinder/src/europa.graph";
            loadLocationGraph(filePath);

        }
    }

    // :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
}



