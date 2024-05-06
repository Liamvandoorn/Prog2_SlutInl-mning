import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;

public class Location extends Circle {
    private final String name;
    private final double x, y;
    private Shape shape;

    public Location(String name, double x, double y){
        this.name = name;
        this.x = x;
        this.y = y;

        shape = new Circle (x,y, 10.0);
        shape.setFill(Color.BLUE);
        // create a setOnMouseclick listener with custom eventhandler
        shape.setOnMouseClicked(new LocationOnClickHandler());
    }

    // metod eller eventhandler som hanterar on click
    private class LocationOnClickHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event){
            System.out.println("Location clicked: " + name);
            Paint color = shape.getFill();

            if (color.equals(Color.BLUE)){
                shape.setFill(Color.RED);
            } else if (color.equals(Color.RED)) {
                shape.setFill(Color.BLUE);
            }
        }

    }

    public Shape getShape() {return shape;}

    public ArrayList<Double> getCoordinates(){
        ArrayList<Double> xy = new ArrayList<Double>();
        xy.add(this.x);
        xy.add(this.y);
        return xy;
    }

    public String getName(){
        return name;
    }

    public String toString(){
        return name + " = [" + x + ", " + y + "]";
    }
}
