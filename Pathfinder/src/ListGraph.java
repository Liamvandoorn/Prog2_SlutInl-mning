import java.io.Serializable;
import java.util.*;

/*
           ___,
          '._.'\
       _____/'-.\
      |    / |
      |~~~/~~|
      \ ()   /         Enojoy our code!
       '.__.'               // Liam & Andreas <3
         ||
        _||_
       `----`
*/

public class ListGraph<T> implements Graph<T>, Serializable { // T stands for objects used as Nodes.

    // Attributes :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // Node : list[Edges, Edges, Edges] //
    private HashMap<T, ArrayList<Edge<T>>> connectionMap;

    // Constructors :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    public ListGraph() {
        this.connectionMap = new HashMap<>();
    }

    // Checkers ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    public boolean pathExists(T from, T to){
        if (!connectionMap.containsKey(from) || !connectionMap.containsKey(to)) {
            return false;
        }
        Set<T> visited = new HashSet<>();
        return dfs(from, to, visited);
    }

    // getters :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    public Set<T> getNodes(){
        return new HashSet<>(connectionMap.keySet());
    }

    public T getNode(String nodeName){
        for (T key : connectionMap.keySet()) {
            if (key instanceof Location) {
                if (((Location) key).getName().equals(nodeName)){
                    return key;
                }
            }
        }
        return null;
    }

    public Collection<Edge<T>> getEdgesFrom(T node){
        validateNodeExistance(node);
        return new HashSet<>(connectionMap.get(node));
    }

    public Edge<T> getEdgeBetween(T node1, T node2){
        validateNodeExistance(node1);
        validateNodeExistance(node2);

        for (Edge<T> edge : connectionMap.get(node1)){
            if (edge.getDestination().equals(node2)){
                return edge;
            }
        }

        return null;
    }

    public List<Edge<T>> getPath(T from, T to){
        Map<T, T> connections = new HashMap<>();
        Queue<T> queue = new LinkedList<>();

        // start with node 1 as current node
        // place node 1 in the queue
        connections.put(from, null);
        queue.add(from);

        // as long as the que is not empty
        while (!queue.isEmpty()) {
            // take next node in queue from stack
            T currentNode = queue.poll();

            // get node neighbors
            // add in all of the nodes neighbors that's already visitet
            ArrayList<Edge<T>> currentNodeNeighbors = new ArrayList<>(connectionMap.get(currentNode));
            for (Edge<T> edge : currentNodeNeighbors) {
                T destinatedNode = edge.getDestination();
                if (!connections.containsKey(destinatedNode)) {
                    connections.put(destinatedNode, currentNode);
                    queue.add(destinatedNode);
                }
            }
        }
        if (!connections.containsKey(to)) {
            return null;
        }
        return buildPathFromMap(connections, from, to);
    }

    private List<Edge<T>> buildPathFromMap(Map<T,T> map, T from, T to) {
        ArrayList<Edge<T>> path = new ArrayList<>();
        T current = to;

        // iterate through map
        while (!current.equals(from)) {
            T next = map.get(current);
            Edge<T> edge = getEdgeBetween(current, next);
            path.add(0, edge);
            //path.addFirst(edge);
            current = next;
        }
/*        System.out.println("::::::::::::" + path.getLast());
        Location lastLocation = (Location) path.getLast().getDestination();
        T lastLocationInPath = this.getNode(lastLocation.getName());
        Edge<T> lastConnection = this.getEdgeBetween(lastLocationInPath, to);
        path.addLast(lastConnection);
        path.remove(0);
*/
        return path;
    }


    // Setters :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    private void validateNodeExistance(T node){
        if (!connectionMap.containsKey(node)) {
            throw new NoSuchElementException("Given node doesn't exist in ListGraph: " + node.toString());
        }
    }

    private void validateConnectionBetweenNodes(T node1, T node2){
        if(getEdgeBetween(node1, node2) == null){
            throw new IllegalStateException("Connections doesn't exists between nodes");
        }
    }

    public void add(T node){
        connectionMap.putIfAbsent(node, new ArrayList<>());
    }

    public void remove(T node) {
        // does node exist in graph?
        validateNodeExistance(node);

        // for all edges connected to the node, get the destination node of the edge and disconnect the two nodes.
        for (Edge<T> edge : connectionMap.get(node)) {
            disconnect(node, edge.getDestination());
        }

        // remove node from connectionMap
        connectionMap.remove(node);
    }

    public void connect(T node1, T node2, String name, int weight){
        //check if nodes exists in graph else throw exception
        validateNodeExistance(node1);
        validateNodeExistance(node2);

        //check if weight is positive else throw exception
        if (weight < 0 ) {
            throw new IllegalArgumentException("Negative weight not allowed");
        }
        //check if connection doesn't already exist between nodes else throw exception
        if(!(getEdgeBetween(node1, node2) == null)){
            throw new IllegalStateException("Connection does already exist between nodes");
        }

        // create and add edges to nodes in graph
        connectionMap.get(node1).add(new Edge<>(node2, weight, name));
        connectionMap.get(node2).add(new Edge<>(node1, weight, name));
    }

    public void disconnect(T node1, T node2){
        validateNodeExistance(node1);
        validateNodeExistance(node2);

        //check if connection exists between node else throw illegalStateException
        validateConnectionBetweenNodes(node1, node2);


        // Hämta alla connections som nod1 har med nod2 och Rensa nod1 och nod2 connection baserat på hämtade connections (edges)
        connectionMap.get(node1).remove(getEdgeBetween(node1, node2));
        connectionMap.get(node2).remove(getEdgeBetween(node2, node1));
    }

    public void setConnectionWeight(T node1, T node2, int weight){
        validateNodeExistance(node1);
        validateNodeExistance(node2);

        //check if weight is positive else throw exception
        if (weight < 0 ) {
            throw new IllegalArgumentException("Negative weight not allowed");
        }

        //check if connection exist between nodes else throw exception
        validateConnectionBetweenNodes(node1, node2);

        // get the edge between node1 and node2
        Edge<T> edgeBetween1to2 = getEdgeBetween(node1, node2);
        Edge<T> edgeBetween2to1 = getEdgeBetween(node2, node1);

        // setWeight on edge
        edgeBetween1to2.setWeight(weight);
        edgeBetween2to1.setWeight(weight);
    }

    public boolean dfs (T startNode, T endNode, Set<T> visited) {
        //Börja med noden "start" som nuvarande nod, notera att vi besökt "start"
        visited.add(startNode);
        //Är den nuvarande noden den vi söker?
        //Ja: rapportera att vi hittat en väg
        if (startNode == endNode) {
            return true;
        } else {
            ArrayList<Edge<T>> listOfEdges = connectionMap.get(startNode);

            // traverse through neighboring edges
            for (Edge<T> edge : listOfEdges){
                if(!visited.contains(edge.getDestination())){
                    if (dfs(edge.getDestination(), endNode, visited))
                        return true;
                }
            }
        }

        return false;  // in case we never find the endNode
    }

    // ToString
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("ListGraph = {");
        for (Map.Entry<T , ArrayList<Edge<T>>> kv : connectionMap.entrySet()) {
            // appenda {key : value} och sätt ihop det med resten av strängen
            sb.append(" ").append(kv.getKey()).append(" : ").append(kv.getValue()).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
