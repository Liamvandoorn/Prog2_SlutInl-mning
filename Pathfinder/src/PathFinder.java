import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.Node;


import java.awt.*;
import java.io.*;
import java.sql.Array;
import java.util.*;
import java.util.List;

import javafx.stage.Stage;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Circle;


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

    private final MenuBar fileMenuBar = new MenuBar();
    private final HBox btnMenu = new HBox(20); // MN: pixel spacing between components within menu
    private final ImageView imageView = new ImageView();
    private Pane mapPane = new Pane();
    private Stage stage;

    private ListGraph<Location> graph = new ListGraph<Location>();
    private ArrayList<Location> selectedPlaces = new ArrayList<Location>();
    private boolean lastEditIsSaved = true;

    private Button findPathBtn = new Button();
    private Button showConnectionBtn = new Button();
    private Button newPlaceBtn = new Button();
    private Button newConnectionBtn = new Button();
    private Button changeConnectionBtn = new Button();

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage){
        this.stage = primaryStage;
        VBox root = new VBox();

        inializeFileMenu();
        initalizeBtnMenu();

        // add container boxes to root node
        mapPane.getChildren().add(imageView);
        instantiateMapPaneEventListener();

        root.getChildren().addAll(fileMenuBar, btnMenu, mapPane);

        Scene scene = new Scene (root, 618, 80);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PathFinder");
        primaryStage.show();
    }

    private void inializeFileMenu() {
        Menu fileMenu = new Menu("File");
        fileMenuBar.getMenus().addAll(fileMenu);

        MenuItem menuNewMap = new MenuItem("New Map");
        MenuItem menuOpenFile = new MenuItem("Open");
        MenuItem menuSave = new MenuItem("Save");
        MenuItem menuSaveImage = new MenuItem("Save Image");
        MenuItem menuExit = new MenuItem("Exit");

        //top corner close button
        stage.setOnCloseRequest(actionEvent -> {
            controlSavedInstance(new CloseAppHandler());
            actionEvent.consume();
            //Platform.exit();
        });

        menuNewMap.setOnAction(actionEvent -> {
            enableButtons();
            controlSavedInstance(new NewMapHandler());
            instantiateMapPaneEventListener();
        });

        menuOpenFile.setOnAction(actionEvent -> {
            enableButtons();
            controlSavedInstance(new OpenMapHandler());
        });

        menuExit.setOnAction(actionEvent -> {
            controlSavedInstance(new CloseAppHandler());
        });

        menuSave.setOnAction(new SaveHandler());

        fileMenuBar.setId("menu");
        fileMenu.setId("menuFile");
        menuNewMap.setId("menuNewMap");
        menuOpenFile.setId("menuOpenFile");
        menuSave.setId("menuSave");
        menuSaveImage.setId("menuSaveImage");
        menuExit.setId("menuExit");

        fileMenu.getItems().addAll(menuNewMap, menuOpenFile, menuSave, menuSaveImage, menuExit);
    }

    private void initalizeBtnMenu() {
        findPathBtn = new Button("Find path");
        showConnectionBtn = new Button("Show Connection");
        newPlaceBtn = new Button("New place");
        newConnectionBtn = new Button("New Connection");
        changeConnectionBtn = new Button("New place");

        findPathBtn.setDisable(true);
        showConnectionBtn.setDisable(true);
        newPlaceBtn.setDisable(true);
        newConnectionBtn.setDisable(true);
        changeConnectionBtn.setDisable(true);

        newPlaceBtn.setOnAction(actionEvent -> {
            isMapLoaded();
            newPlaceBtn.setDisable(true);
            stage.getScene().setCursor(Cursor.CROSSHAIR);

        mapPane.setOnMouseClicked(Event -> {
                NewPlaceHandler handler = new NewPlaceHandler();
                handler.handle(Event);
                newPlaceBtn.setDisable(false);
                stage.getScene().setCursor(Cursor.DEFAULT);
                mapPane.setOnMouseClicked(null);
            });
        });

        newConnectionBtn.setOnAction(actionEvent -> {
            NewConnectionHandler handler = new NewConnectionHandler();
            handler.handle(actionEvent);
        });

        btnMenu.setId("btnMenu");
        findPathBtn.setId("btnFindPath");
        showConnectionBtn.setId("btnShowConnection");
        newPlaceBtn.setId("btnNewPlace");
        newConnectionBtn.setId("btnNewConnection");
        changeConnectionBtn.setId("btnChangeConnection");

        btnMenu.getChildren().addAll(findPathBtn, showConnectionBtn, newPlaceBtn, newConnectionBtn, changeConnectionBtn);
        btnMenu.setAlignment(Pos.CENTER);
        btnMenu.setMinHeight(60);
    }

    private void instantiateMapPaneEventListener() {
        System.out.println("instatiated new listener");
        mapPane.setOnMouseClicked(event -> {
            System.out.println("Event Is Reacting");
            if ( event.getTarget() instanceof Circle ) {
                System.out.println(event);
                for (Node node : mapPane.getChildren()) {
                    if (node instanceof Circle) {
                        if (((Circle) node).getFill().equals(Color.RED)) {
                            String nodeName = node.getId().split(" = ")[0];
                            // if graph is empty, create a new location object and add to graph.
                            Location currentLocation = graph.getNode(nodeName);
                            System.out.println(currentLocation);
                            selectedPlaces.add(graph.getNode(nodeName));
                            System.out.println("added Location to arraylist: " + currentLocation);

                        } else if (((Circle) node).getFill().equals(Color.BLUE)) {
                            String nodeName = node.getId().split(" = ")[0];
                            Location currentLocation = graph.getNode(nodeName);
                            if (selectedPlaces.contains(currentLocation)){
                                selectedPlaces.remove(currentLocation);
                                System.out.println("removed Location to arraylist: " + currentLocation);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Application specific method for checking if data is saved, should only be used if data has already been loaded.
     * If lastEditIsSaved is false, it will show alert specific for saving instance of application
     */
    private void enableButtons(){
        findPathBtn.setDisable(false);
        showConnectionBtn.setDisable(false);
        newPlaceBtn.setDisable(false);
        newConnectionBtn.setDisable(false);
        changeConnectionBtn.setDisable(false);
    }

    private void controlSavedInstance(EventHandler<ActionEvent> event){
        if (!lastEditIsSaved) {
            showSaveAlert(event);
        } else {
            event.handle(new ActionEvent());
        }
    }

    /**
     * Helper function for checking if a map is loaded
     * @return
     */

    private boolean isMapLoaded () {
        System.out.println(imageView.getImage());
        if (imageView.getImage() != null) {
            return true;
        }
        return false;
    }

    // draw methods -----

    public boolean getLastEditIsSaved(){
        return lastEditIsSaved;
    }

    private void showSaveAlert(EventHandler<ActionEvent> event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Warning!");
        alert.setHeaderText("Unsaved changes, continue anyway?");
        alert.showAndWait().ifPresent(response -> {

            String btnText = response.getText();
            System.out.println(btnText);
            if (btnText.equals("OK")) {
                lastEditIsSaved = true;//FOR TESTING !!!!!!!
                event.handle(new ActionEvent());
            } else {
                alert.close();
            }
        });
    }

    private void showErrorAlert(String headerText){
        Alert errorAlert = new Alert(AlertType.ERROR);
        errorAlert.setTitle("Error!");

        errorAlert.setHeaderText(headerText);
        errorAlert.showAndWait();
    }

    private void showNode(Location location) {
        try {
            // draw node on mapPane.
            mapPane.getChildren().add(location.getShape());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void showAllNodes() {
        for (Location location : graph.getNodes()){
            showNode(location);
        }
    }

    private void drawConnection(Location from, Location to) {
        // issue, they are above the circle, blocking the clickable area, maybe adjust it somehow
        ArrayList<Double> fromXY = from.getCoordinates();
        ArrayList<Double> toXY = to.getCoordinates();

        Line line = new Line(fromXY.get(0), fromXY.get(1), toXY.get(0), toXY.get(1));
        line.setMouseTransparent(true); // demo wants it to be shown above the circles but still clickable
        mapPane.getChildren().add(line);
    }

    private void drawAllConnections() {
        // for each node in graph
        for (Location location : graph.getNodes()) {
            // Get edges
            for (Edge edge : graph.getEdgesFrom(location)) {
                drawConnection(location, (Location) edge.getDestination());
            }
        }
    }

    /**
     * Helper method, should only be used if graph has been saved or emptied
     */

    private void resetMapPane(){
        // check if listgraph is empty
        if (graph.getNodes().isEmpty()){
            List<Node> copyOfChildren = new ArrayList<Node>(mapPane.getChildren());

            for (Node node : copyOfChildren) {
                if (node instanceof Circle) {
                    mapPane.getChildren().remove(node);
                } else if (node instanceof Line) {
                    mapPane.getChildren().remove(node);
                }
            }
        } else {
            System.out.println("Can't reset map since there's unsaved data");
        }
    }

    private ArrayList<Location> getSelectedNodesFromMapMapPane(){
        ArrayList<Location> namesOfSelectNodes = new ArrayList<>();
        for (Node node : mapPane.getChildren()) {
            if (node instanceof Circle) {
                if (((Circle) node).getFill().equals(Color.RED)) {
                    String nodeName = node.getId().split(" = ")[0];
                    namesOfSelectNodes.add(graph.getNode(nodeName));
                }
            }
        }
        return namesOfSelectNodes;
    }

    private void loadMap(String strPath){
        if (strPath.isEmpty()){
            strPath = "europa.gif";
        }
        Image image = new Image(strPath);
        imageView.setImage(image);
        stage.setWidth(image.getWidth());
        stage.setHeight(image.getHeight() + stage.getHeight());
        System.out.println("mapPane Dimension" + mapPane.getHeight() + " : " + mapPane.getWidth());
    }

    /**
     * Save method that consist of three parts
     * {@code 1. write in gif path }
     * {@code 2. write in the nodes }
     * {@code 3. write in the connections }
     * Should never be used if a data hasn't been loaded first
     */
    private void saveMap() {
        try {
            FileWriter fw = new FileWriter("./PathFinder/src/europa.graph");
            BufferedWriter bw = new BufferedWriter(fw);
            // write in gif path
            bw.write("file:europa.gif\n");

            Set<Location> nodes = graph.getNodes();
            Iterator<Location> setIterator = nodes.iterator();

            while (setIterator.hasNext()){
                Location currentNode = setIterator.next();
                String[] parts = currentNode.toString().split("\\s=\\s\\[|]|,\\s");

                bw.write(
                        parts[0] + ";" +
                                parts[1] + ";" +
                                parts[2] );
                // last node never has ; in prinout so we put it within a if check, also why a whle iterator was prefered over a for each.
                if (setIterator.hasNext()) {
                    bw.write(";");
                }
            }

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

    // Graph methods ----

    private void resetGraph(){
        graph = new ListGraph<>();
    }

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

    // Handler Classes :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    class NewMapHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            if (imageView.getImage() == null){ // ändra refactor baserat på boolean sats
                loadMap("");
            } else {
                resetGraph();
                resetMapPane();
                loadMap("");
            }
        }
    }

    class SaveHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event) {
            //saveMap();
            lastEditIsSaved = false; //FOR TESTING !!!!!
        }
    }

    class OpenMapHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            resetGraph();
            resetMapPane();
            String filePath = "./src/europa.graph";
            loadLocationGraph(filePath);
            showAllNodes();
            drawAllConnections();
        }
    }

    class CloseAppHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            Platform.exit();
        }
    }

    class NewPlaceHandler implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent event){

            double xCoordinate = event.getX();
            double yCoordinate = event.getY();
            System.out.println(xCoordinate + ":" + yCoordinate);

            TextInputDialog tid = new TextInputDialog("");
            tid.setTitle("Name");
            tid.setHeaderText("");
            tid.setContentText("Name of place:");

            Optional<String> result = tid.showAndWait();
            System.out.println(result);
            if (result.isPresent()){
                System.out.println(result);
                if ( !result.get().isEmpty()){ // check for blankspaces
                    String locationName = result.get();
                    System.out.println(locationName);
                    Location location = new Location(locationName, xCoordinate, yCoordinate);
                    graph.add(location);
                    System.out.println("Current state of graph:" + graph);
                    showNode(location);
                    lastEditIsSaved = false;
                }
            }
            mapPane.setOnMouseClicked(null);
        }
    }

    class NewConnectionHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            //check if there is two nodes with red color
            ArrayList<Location> selectedNodes = getSelectedNodesFromMapMapPane();
            //ArrayList<Location> selectedNodes = selectedPlaces;
            Location fromNode = selectedNodes.get(0);
            Location toNode = selectedNodes.get(1);

            // check if there exists two nodes

            if (selectedNodes.size() != 2) {
                // throw alert
                Alert errorAlert = new Alert(AlertType.ERROR);
                errorAlert.setTitle("Error!");
                errorAlert.setContentText("Two places must be selected!");
                return;
            }
            // check if connection already exist
            if (!(graph.getEdgeBetween(fromNode, toNode) == null)) {
                Alert errorAlert = new Alert(AlertType.ERROR);
                errorAlert.setTitle("Error!");
                errorAlert.setContentText("Places already have a connection");
                return;
            }


            // ------------------------

            TextInputDialog tid = new TextInputDialog("");
            tid.setTitle("Connection");
            tid.setHeaderText("Connection from " + fromNode.getName() + " to " + toNode.getName());
            tid.setContentText("Name:");
            tid.setContentText("Time:");

            tid.setHeight(230);
            tid.setWidth(350);

            GridPane grid = new GridPane();
            TextField name = new TextField();
            TextField time = new TextField();


            grid.add(new Label("Name:  "), 0, 0);
            grid.add(new Label("Time:  "), 0, 1);

            grid.add(name, 1, 0);
            grid.add(time, 1, 1);
            grid.setAlignment(Pos.CENTER);

            tid.getDialogPane().setContent(grid);



            Optional<String> result = tid.showAndWait();
            if (result.isPresent()) {
                //if name textfield is not left blank and time textfield is an integer, create new connection and draw on map
                if (!name.getText().isEmpty()) {

                    String nameOfConnection = name.getText();
                    try {
                        int timeOfConnection = Integer.parseInt(time.getText());


                        graph.connect(fromNode, toNode, nameOfConnection, timeOfConnection);
                        drawConnection(fromNode, toNode);
                        graph.getNode(fromNode.getName()).getShape().setFill(Color.BLUE);
                        graph.getNode(toNode.getName()).getShape().setFill(Color.BLUE);


                    } catch (NumberFormatException e) {
                        showErrorAlert("Time should be an integer");

                    }
                } else {
                    showErrorAlert("Blank name is not allowed");

                }

            }

        }
    }
}



