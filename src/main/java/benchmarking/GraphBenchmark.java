package benchmarking;

import models.Graph;
import models.GraphNode;
import models.POI;
import models.CostOfPath;
import models.GraphLink;
import org.openjdk.jmh.annotations.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class GraphBenchmark {

    private List<GraphNode<POI>> roomNodes;
    private GraphNode<POI> startNode;
    private GraphNode<POI> targetNode;
    private String lookingFor;

    @Setup(Level.Trial)
    public void setUp() {
        roomNodes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            GraphNode<POI> node = new GraphNode<>(new POI("Node" + i, i, i, false));
            roomNodes.add(node);
        }
        // Add edges for the nodes (example setup)
        for (int i = 0; i < roomNodes.size(); i++) {
            for (int j = i + 1; j < roomNodes.size(); j++) {
                roomNodes.get(i).adjList.add(new GraphLink(roomNodes.get(j), i + j));
                roomNodes.get(j).adjList.add(new GraphLink(roomNodes.get(i), i + j));
            }
        }
        startNode = roomNodes.get(0);
        targetNode = roomNodes.get(roomNodes.size() - 1);
        lookingFor = "Node" + (roomNodes.size() - 1);
    }

    @Benchmark
    public int benchmarkGetCost() {
        return Graph.getCost(0, roomNodes.size() - 1, roomNodes);
    }

    @Benchmark
    public CostOfPath benchmarkFindCheapestPathDijkstra() {
        return Graph.findCheapestPathDijkstra(startNode, targetNode.data);
    }

    @Benchmark
    public CostOfPath benchmarkSearchGraphDepthFirstCheapestPath2() {
        return Graph.searchGraphDepthFirstCheapestPath2(startNode, new ArrayList<>(), 0, lookingFor);
    }
}