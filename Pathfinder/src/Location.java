// PROG2 VT2024, Inlämningsuppgift, del 2
// Grupp 76
// Liam vandoorn liva4190
// Andreas Johansson anjo9645

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.Objects;

public class Location extends Circle{
    private final String name;
    private final double x, y;
    //private Shape shape;
    private final Label placeLabel;
    private static int nmbrOfRedCircles;

    public Location(String name, double x, double y){
        this.name = name;
        this.x = x;
        this.y = y;
        this.setRadius(10.0);
        this.setLayoutX(x);
        this.setLayoutY(y);

        this.setId(this.getName());
        this.setFill(Color.BLUE);

        // create a setOnMouseclick listener with custom eventhandler
        //shape.setOnMouseClicked(new LocationOnClickHandler());
        this.setOnMouseClicked(event -> {
            LocationOnClickHandler handler = new LocationOnClickHandler();
            handler.handle(event);
        });

        placeLabel = new Label(name);
        placeLabel.setLayoutX(x + 1);
        placeLabel.setLayoutY(y + 6);
        placeLabel.setFont(Font.font("", FontWeight.BOLD, 15));
        placeLabel.setId("placeLabel =[" + name +"]");
    }

    // metod eller eventhandler som hanterar on click
    private class LocationOnClickHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event){
            Location target = (Location) event.getTarget();
            Paint color = target.getFill();

            if (color.equals(Color.BLUE) && nmbrOfRedCircles < 2){
                target.setFill(Color.RED);
                nmbrOfRedCircles += 1;
            } else if (color.equals(Color.RED)) {
                target.setFill(Color.BLUE);
                nmbrOfRedCircles -= 1;
            }
        }
    }

    public void setNmbrOfRedCirclesZero() {
        nmbrOfRedCircles = 0;
    }

    //public Shape getShape() {return shape;}

    public ArrayList<Double> getCoordinates(){
        ArrayList<Double> xy = new ArrayList<Double>();
        xy.add(this.x);
        xy.add(this.y);
        return xy;
    }

    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        return this.hashCode() == object.hashCode();
    }

    public int hashCode() {
        return Objects.hash(name, x, y) * 7;
    }

    public Label getPlaceLabel(){
        return placeLabel;
    }

    public String getName(){
        return name;
    }

    public String toString(){
        return name + " = [" + x + ", " + y + "]";
    }
}
