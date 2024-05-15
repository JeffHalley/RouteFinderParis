package testclass;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.ArrayList;
import java.util.List;
import models.CostOfPath;
import models.Graph;
import models.GraphLink;
import models.GraphNode;
import models.POI;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;


public class GraphTest {

    @Test
    public void testGraphInitialization() {
        Graph graph = new Graph();
        Assertions.assertNotNull(graph);
    }

    @Test
    public void testGraphNodeInitialization() {
        POI poi = new POI("Eiffel Tower", 182, 161, true);
        GraphNode<POI> node = new GraphNode(poi);
        Assertions.assertNotNull(node);
        Assertions.assertEquals(poi, node.data);
    }

    @Test
    public void testGetCost() {
        List<GraphNode<POI>> roomNodes = new ArrayList();
        roomNodes.add(new GraphNode(new POI("Node1", 0, 0, false)));
        roomNodes.add(new GraphNode(new POI("Node2", 3, 4, false)));
        int cost = Graph.getCost(0, 1, roomNodes);
        Assertions.assertEquals(19, cost);
    }

    @Test
    public void testFindCheapestPathDijkstra() {
        GraphNode<POI> startNode = new GraphNode(new POI("Start", 0, 0, false));
        CostOfPath costOfPath = Graph.findCheapestPathDijkstra(startNode, "End");
        Assertions.assertNull(costOfPath);
    }

    @Test
    public void testConnectToNodeUndirected() {
        GraphNode<String> node1 = new GraphNode("Node 1");
        GraphNode<String> node2 = new GraphNode("Node 2");
        node1.connectToNodeUndirected(node2, 10);
        Assertions.assertEquals(1, node1.adjList.size());
        Assertions.assertEquals(node2, ((GraphLink)node1.adjList.get(0)).destNode);
        Assertions.assertEquals(10, ((GraphLink)node1.adjList.get(0)).cost);
        Assertions.assertEquals(1, node2.adjList.size());
        Assertions.assertEquals(node1, ((GraphLink)node2.adjList.get(0)).destNode);
        Assertions.assertEquals(10, ((GraphLink)node2.adjList.get(0)).cost);
    }
}
