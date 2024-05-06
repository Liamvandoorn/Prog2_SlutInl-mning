import java.io.Serializable;
public class Edge <T> implements Serializable{
    // T stands for object

    private final T destination;

    private int weight;

    private final String name;

    public Edge(T destination, int weight, String name){
        this.destination = destination;
        this.weight = weight;
        this.name = name;
    }

    // Getters

    public T getDestination(){
        return destination;
    }

    public int getWeight(){
        return weight;
    }

    public void setWeight(int weight){
        if(weight < 0){
            throw new IllegalArgumentException("Weight can't be less than 0");
        } else {
            this.weight = weight;
        }
    }

    public String getName(){
        return name;
    }

    public String toString(){
        //return "Edge = [Destination: "+ destination + ", Weight: " + weight + ", Name:" + name + "]";
        return "till " + destination + " med " + name +  " tar " + weight;
    }

}
