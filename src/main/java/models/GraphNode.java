package models;

import java.util.ArrayList;
import java.util.List;

public class GraphNode<T> {

    public T data;
    public int nodeValue= Integer.MAX_VALUE;

    public List<models.GraphLink> adjList = new ArrayList<>();

    public GraphNode(T data) {
        this.data=data;
    }

    public void connectToNodeDirected(GraphNode<T> destNode,int cost) {
        adjList.add( new models.GraphLink(destNode,cost) ); //Add new link object to source adjacency list
    }
    public void connectToNodeUndirected(GraphNode<T> destNode,int cost) {
        adjList.add( new models.GraphLink(destNode,cost) ); //Add new link object to source adjacency list
        destNode.adjList.add( new models.GraphLink(this,cost) ); //Add new link object to destination adjacency list
    }
}
