// PROG2 VT2024, Inlämningsuppgift, del 2
// Grupp 76
// Liam vandoorn liva4190
// Andreas Johansson anjo9645

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
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
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.shape.Circle;

import javax.imageio.ImageIO;

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
    private Pane outputArea = new Pane();
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

    // Instantiate stage nodes --------------

    public void start(Stage primaryStage){
        this.stage = primaryStage;
        VBox root = new VBox();
        outputArea = new Pane();
        outputArea.setId("outputArea");

        inializeFileMenu();
        initalizeBtnMenu();

        // add container boxes to root node
        outputArea.getChildren().add(imageView);

        root.getChildren().addAll(fileMenuBar, btnMenu, outputArea);

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
            CloseAppHandler handler = new CloseAppHandler();
            actionEvent.consume();

        });

        menuNewMap.setOnAction(actionEvent -> {
            disableButtons(false);
            controlSavedInstance(new NewMapHandler());
        });

        menuOpenFile.setOnAction(actionEvent -> {
            disableButtons(false);
            controlSavedInstance(new OpenMapHandler());
        });

        menuSave.setOnAction(new SaveHandler());

        menuSaveImage.setOnAction(actionEvent -> {
            SaveImageHandler handler = new SaveImageHandler();
            handler.handle(actionEvent);
        });

        menuExit.setOnAction(actionEvent -> {
            controlSavedInstance(new CloseAppHandler());
        });

        fileMenuBar.setId("menu");
        fileMenu.setId("menuFile");
        menuNewMap.setId("menuNewMap");
        menuOpenFile.setId("menuOpenFile");
        menuSave.setId("menuSaveFile");
        menuSaveImage.setId("menuSaveImage");
        menuExit.setId("menuExit");

        fileMenu.getItems().addAll(menuNewMap, menuOpenFile, menuSave, menuSaveImage, menuExit);
    }

    private void initalizeBtnMenu() {
        findPathBtn = new Button("Find path");
        showConnectionBtn = new Button("Show Connection");
        newPlaceBtn = new Button("New place");
        newConnectionBtn = new Button("New Connection");
        changeConnectionBtn = new Button("Change Connection");
        setButtonIds();
        disableButtons(true);
        instantiateMenuBtnListeners();

        btnMenu.getChildren().addAll(findPathBtn, showConnectionBtn, newPlaceBtn, newConnectionBtn, changeConnectionBtn);
        btnMenu.setAlignment(Pos.CENTER);
        btnMenu.setMinHeight(60);
    }

    // Instantiate Listeners------

    private void instantiateMenuBtnListeners() {
        findPathBtn.setOnAction(actionEvent -> {
            FindPathHandler handler = new FindPathHandler();
            handler.handle(actionEvent);
        });
        showConnectionBtn.setOnAction(actionEvent -> {
            ShowConnectionHandler handler = new ShowConnectionHandler();
            handler.handle(actionEvent);
        });
        newPlaceBtn.setOnAction(actionEvent -> {
            isMapLoaded();
            newPlaceBtn.setDisable(true);
            stage.getScene().setCursor(Cursor.CROSSHAIR);

            outputArea.setOnMouseClicked(mouseEvent -> {
                NewPlaceHandler handler = new NewPlaceHandler();
                handler.handle(mouseEvent);
                newPlaceBtn.setDisable(false);
                stage.getScene().setCursor(Cursor.DEFAULT);
            });
        });
        newConnectionBtn.setOnAction(actionEvent -> {
            NewConnectionHandler handler = new NewConnectionHandler();
            handler.handle(actionEvent);
        });
        changeConnectionBtn.setOnAction(actionEvent -> {
            ChangeConnectionHandler handler = new ChangeConnectionHandler();
            handler.handle(actionEvent);
        });

    }

    private void instantiateOutputAreaEventListener() {
        outputArea.setOnMouseClicked(event -> {
            if ( event.getTarget() instanceof Circle ) {
                for (Node node : outputArea.getChildren()) {
                    if (node instanceof Circle) {
                        if (((Circle) node).getFill().equals(Color.RED)) {
                            String nodeName = node.getId().split(" = ")[0];
                            Location currentLocation = graph.getNode(nodeName);
                            if (!selectedPlaces.contains(currentLocation)) {
                                selectedPlaces.add(graph.getNode(nodeName));
                            }
                        } else if (((Circle) node).getFill().equals(Color.BLUE)) {
                            String nodeName = node.getId().split(" = ")[0];
                            Location currentLocation = graph.getNode(nodeName);
                            if (selectedPlaces.contains(currentLocation)){
                                selectedPlaces.remove(currentLocation);
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

    private void controlSavedInstance(EventHandler<ActionEvent> event){
        if (!lastEditIsSaved) {
            showSaveAlert(event);
        } else {
            event.handle(new ActionEvent());
        }
    }

    // Buttons
    private void disableButtons(boolean bool){
        findPathBtn.setDisable(bool);
        showConnectionBtn.setDisable(bool);
        newPlaceBtn.setDisable(bool);
        newConnectionBtn.setDisable(bool);
        changeConnectionBtn.setDisable(bool);
    }

    private void setButtonIds() {
        btnMenu.setId("btnMenu");
        findPathBtn.setId("btnFindPath");
        showConnectionBtn.setId("btnShowConnection");
        newPlaceBtn.setId("btnNewPlace");
        newConnectionBtn.setId("btnNewConnection");
        changeConnectionBtn.setId("btnChangeConnection");
    }
    // checkers ------------------

    private boolean isTwoPlacesSelected() {
        return selectedPlaces.size() != 2;
    }

    private boolean isTwoPlacesConnected(Location place1, Location place2) {
        if (graph.getEdgeBetween(place1, place2) == null) {
            return false;
        }
        return true;
    }

    private boolean isMapLoaded () {
        if (imageView.getImage() != null) {
            return true;
        }
        return false;
    }

    // Getters -------------------

    private List<Edge<Location>> getPathListBetweenNodes(Location from, Location to){
        List<Edge<Location>> path = graph.getPath(from, to);
        if (path == null) {
            showErrorAlert("No Path Found Between " + from.getName() + " to " + to.getName());
            return null;
        }
        return path;

    }


    public boolean getLastEditIsSaved(){
        return lastEditIsSaved;
    }

    // Setters -------------------
    private void emptySelectedPlaces () {
        if (!(selectedPlaces.isEmpty())){
            Location tmp = selectedPlaces.get(0);
            tmp.setNmbrOfRedCirclesZero();
        }
        selectedPlaces = new ArrayList<Location>();
    }

    private void unSelectAllPlaces() {
        emptySelectedPlaces();
        for (Node shape : outputArea.getChildren()) {
            if (shape instanceof Circle) {
                ((Circle) shape).setFill(Color.BLUE);
            }
        }
    }

    // Setters: draw methods -----

    private void showNode(Location location) {
        try {
            // draw node on mapPane.
            outputArea.getChildren().addAll(location, location.getPlaceLabel());
        } catch (Exception e) {
        }
    }

    private void showAllNodes() {
        for (Location location : graph.getNodes()){
            showNode(location);
        }
    }

    private void drawConnection(Location from, Location to) {

        ArrayList<Double> fromXY = from.getCoordinates();
        ArrayList<Double> toXY = to.getCoordinates();

        Line line = new Line(fromXY.get(0), fromXY.get(1), toXY.get(0), toXY.get(1));
        line.setDisable(true);
        line.setMouseTransparent(true); // demo wants it to be shown above the circles but still clickable
        outputArea.getChildren().add(line);
    }

    private void drawAllConnections() {
        // for each node in graph
        try {
            for (Location location : graph.getNodes()) {
                // Get edges
                for (Edge edge : graph.getEdgesFrom(location)) {
                    drawConnection(location, (Location) edge.getDestination());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e + "hello im crashing");
        }
    }

    private void resetMapPane(){
        // check if listgraph is empty
        if (graph.getNodes().isEmpty()){
            List<Node> copyOfChildren = new ArrayList<Node>(outputArea.getChildren());

            for (Node node : copyOfChildren) {
                if (!(node instanceof ImageView)){
                    outputArea.getChildren().remove(node);
                }
            }
        } else {
            // removed out print here
        }
    }

    private void loadMap(String strPath){
        /*if (strPath.isEmpty()){
            //strPath = "europa.gif";
            strPath = "karta.png";
        }

         */
        Image image = new Image(strPath);
        imageView.setImage(image);
        stage.setWidth(image.getWidth());

        // if  is not more than say intial application height
        if ( stage.getHeight() <= 120 ) {
            stage.setHeight(image.getHeight() + stage.getHeight());
        }
    }

    private void saveMap() {
        try {
            FileWriter fw = new FileWriter("europa.graph");
            BufferedWriter bw = new BufferedWriter(fw);
            // write in image name from path
            String[] fullURL = imageView.getImage().getUrl().split("/");
            String imageURL = fullURL[fullURL.length-1];
            bw.write("file:"+ imageURL + "\n");

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

        }

    }

    // Setters: Graph methods ----

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
            String[] gifPath = mapPath.split(":");
            loadMap(gifPath[1]);

            // read all nodes
            String[] tmp = br.readLine().split(";");
            List<String> listOfLocationStrings = Arrays.asList(tmp);

            createLocationNodesFromList(listOfLocationStrings);
            connectLocationNodes(br);
            fr.close();
            br.close();
        } catch (FileNotFoundException e ){

        } catch (Exception e) {
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
                try {
                    String[] connectionParts = line.split(";");
                    Location from = nodeMap.get(connectionParts[0]);
                    Location to = nodeMap.get(connectionParts[1]);
                    String name = connectionParts[2];
                    int weight = Integer.parseInt(connectionParts[3]);
                    graph.connect(from, to, name, weight);
                } catch (Exception e) {
                    //System.out.println(e);
                }
            }
        } catch (IOException e) {
        }
    }

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

    // Setters: Alerts ------------

    void showErrorAlert(String title, String contentText) {
        Alert errorAlert = new Alert(AlertType.ERROR);
        errorAlert.setTitle(title);
        errorAlert.setContentText(contentText);
        errorAlert.showAndWait();
    }

    private void showErrorAlert(String headerText){
        Alert errorAlert = new Alert(AlertType.ERROR);
        errorAlert.setTitle("Error!");

        errorAlert.setHeaderText(headerText);
        errorAlert.showAndWait();
    }

    private void showSaveAlert(EventHandler<ActionEvent> event) {

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initModality(Modality.APPLICATION_MODAL); // weird modality test issue, added this because that's what the tests evaluates.
        alert.setTitle("Warning!");
       // alert.setHeaderText("Unsaved changes, continue anyway?");
        System.out.println("Alert modality " + alert.getModality());
        System.out.println("BEFORE Stage modality " + stage.getModality());

        alert.setContentText("Unsaved changes, continue anyway?");
        alert.showAndWait().ifPresent(response -> {

            String btnText = response.getText();
            //System.out.println(btnText);
            if (btnText.equals("OK")) {
                lastEditIsSaved = true;//FOR TESTING !!!!!!!
                event.handle(new ActionEvent());
            } else {
                alert.close();
            }
        });
    }

    private void showPathInformationAlert(List<Edge<Location>> path) {
        Location from = path.get(0).getDestination();
        Location to = path.get(path.size()-1).getDestination();

        // instantiate the grid and text area that contains path content
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText("The Path From " + from.getName() + " to " + to.getName());

        GridPane grid = new GridPane();
        TextArea textArea = new TextArea();
        textArea.setEditable(false);

        StringBuilder builder = new StringBuilder();
        int total = 0;
        for (Edge<Location> connection : path) {
            total += connection.getWeight();
            builder
                    .append("to ")
                    .append(connection.getDestination().getName())
                    .append(" by ")
                    .append(connection.getName())
                    .append(" takes ")
                    .append(connection.getWeight())
                    .append("\n");
        }

        builder
                .append("Total ")
                .append(total);

        textArea.setText(builder.toString());
        grid.add(textArea, 0,0);
        alert.getDialogPane().setContent(grid);
        alert.showAndWait();
    }
    // Setters: Dialogs ------------

    private void showConnectionDialog(Location from, Location to, boolean nameIsNotAdjustable, boolean weightIsNotAdjustable) {
        TextInputDialog tid = new TextInputDialog("");

        tid.setTitle("Connection");
        tid.setHeaderText("Connection from " + from.getName() + " to " + to.getName());
        tid.setContentText("Name:");
        tid.setContentText("Time:");

        tid.setHeight(230);
        tid.setWidth(350);

        GridPane grid = new GridPane();
        TextField name = new TextField();
        TextField time = new TextField();

        Edge connection = graph.getEdgeBetween(from, to);


        time.setDisable(weightIsNotAdjustable);
        name.setDisable(nameIsNotAdjustable);

        if (!weightIsNotAdjustable && !nameIsNotAdjustable) {
            name.setText("");
            time.setText("");
        } else {
            name.setText(connection.getName());
            time.setText(String.valueOf(connection.getWeight()));
        }

        grid.add(new Label("Name:  "), 0, 0);
        grid.add(new Label("Time:  "), 0, 1);

        grid.add(name, 1, 0);
        grid.add(time, 1, 1);
        grid.setAlignment(Pos.CENTER);

        tid.getDialogPane().setContent(grid);

        Optional<String> result = tid.showAndWait();
        if (result.isPresent()) {
            if (!name.getText().isEmpty()) {
                // Extract the name and time of the connection from the text fields
                try {
                    String nameOfConnection = name.getText();
                    int timeOfConnection = Integer.parseInt(time.getText());
                    // if weight is adjustable, we know we're suppose create a new connection, else we know we override existing.
                    if (weightIsNotAdjustable || nameIsNotAdjustable) {
                        graph.disconnect(from, to);
                        if (!(nameOfConnection.equals(connection.getName())) || !(timeOfConnection == connection.getWeight())) {
                            lastEditIsSaved = false;
                        }
                    }
                    graph.connect(from, to, nameOfConnection, timeOfConnection);
                    drawConnection(from, to);
                    //unSelectAllPlaces();
                } catch (NumberFormatException e) {
                    showErrorAlert("Time should be an integer");
                }
            } else {
                showErrorAlert("Blank name is not allowed");
            }
        }
    }

    // EventHandler Classes :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    // File Menu handlers ---------------

    class NewMapHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            if (imageView.getImage() == null){ // ändra refactor baserat på boolean sats
                loadMap("europa.gif");
            } else {
                resetGraph();
                resetMapPane();
                loadMap("europa.gif");
                emptySelectedPlaces();
            }
            lastEditIsSaved = false;
        }
    }

    class SaveHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event) {
            saveMap();
            lastEditIsSaved = true;
        }
    }

    class OpenMapHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            resetGraph(); // Check
            resetMapPane(); // Check
            String filePath = "europa.graph";
            loadLocationGraph(filePath); // Check
            showAllNodes();  // Check
            drawAllConnections();
            instantiateOutputAreaEventListener();
            emptySelectedPlaces();
        }
    }

    class SaveImageHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            try{
                WritableImage image = outputArea.snapshot(null, null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(bufferedImage, "png", new File("capture.png"));
            }catch (IOException e){
                showErrorAlert("IO-error "+e.getMessage());
            }
        }
    }

    class CloseAppHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            //Platform.exit();
            //stage.close();
            //stage.hide();
        }
    }

    // Button Menu Handlers --------------

    class FindPathHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (isTwoPlacesSelected()) {
                showErrorAlert("Error!", "Two places must be selected!");
                return;
            }

            Location fromNode = selectedPlaces.get(0);
            Location toNode = selectedPlaces.get(1);
            List<Edge<Location>> path = getPathListBetweenNodes(fromNode, toNode);

            if (path != null ){
                showPathInformationAlert(path);
            }
        }
    }

    class ShowConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (isTwoPlacesSelected()) {
                showErrorAlert("Error!", "Two places must be selected!");
                return;
            }

            Location fromNode = selectedPlaces.get(0);
            Location toNode = selectedPlaces.get(1);


            if (!isTwoPlacesConnected(fromNode, toNode)) {
                showErrorAlert("Error!", "Places have no connection");
                return;
            }

            showConnectionDialog(fromNode, toNode, true, true);
        }
    }

    class NewPlaceHandler implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent event){

            double xCoordinate = event.getX();
            double yCoordinate = event.getY();

            TextInputDialog tid = new TextInputDialog("");
            tid.setTitle("Name");
            tid.setHeaderText("");
            tid.setContentText("Name of place:");

            Optional<String> result = tid.showAndWait();
            if (result.isPresent()){
                if ( !result.get().isEmpty()){ // check for blankspaces
                    String locationName = result.get();
                    Location location = new Location(locationName, xCoordinate, yCoordinate);
                    graph.add(location);
                    showNode(location);
                    instantiateOutputAreaEventListener();

                    lastEditIsSaved = false;
                }
            }


        }
    }

    class NewConnectionHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if (isTwoPlacesSelected()) {
                showErrorAlert("Error!", "Two places must be selected!");
                return;
            }

            Location fromNode = selectedPlaces.get(0);
            Location toNode = selectedPlaces.get(1);

            if (isTwoPlacesConnected(fromNode, toNode)) {
                showErrorAlert("Error!", "Places already have a connection");
                return;
            }

            showConnectionDialog(fromNode, toNode, false, false);
        }
    }

    class ChangeConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (isTwoPlacesSelected()) {
                showErrorAlert("Error!", "Two places must be selected!");
                return;
            }

            Location fromNode = selectedPlaces.get(0);
            Location toNode = selectedPlaces.get(1);

            if (!isTwoPlacesConnected(fromNode, toNode)) {
                showErrorAlert("Error!", "Places have no connection");
                return;
            }

            showConnectionDialog(fromNode, toNode, true, false);
        }
    }

}



