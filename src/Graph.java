import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface Graph<T> {

    void add(T node);
    
    void connect(T node1, T node2, String name, int weight);
    
    void setConnectionWeight(T node1, T node2, int weight);
    
    Set<T> getNodes();
    
    Collection<Edge<T>> getEdgesFrom(T node);
    
    Edge<T> getEdgeBetween(T node1, T node2);
    
    void disconnect(T node1, T node2);
    
    void remove(T node);
    
    boolean pathExists(T from, T to);
    
    List<Edge<T>> getPath(T from, T to);
}


    /*
    public List<Edge> getShortestPath(City from, City to){
        Map<City, City> connections = new HashMap<>();
        LinkedList<City> queue = new LinkedList<>();

        connections.put(from, null);
        queue.add(from);

        while(!queue.isEmpty()){
            City city = queue.pollFirst();
            for(Edge edge : nodes.get(city)){
                City destination = edge.getDestination();
                if(!connections.containsKey(destination)){
                    connections.put(destination, city);
                    queue.add(destination);
                }
            }
        }
        if(!connections.containsKey(to)){
            throw new IllegalStateException("no connection");
        }

        return gatherPath(from, to, connections);
*/
