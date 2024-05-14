package models;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.input.PickResult;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import models.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Graph {
    public static int getCost(int nodeA, int nodeB, List<GraphNode<POI>> roomNodes) {
        int nodeAX = roomNodes.get(nodeA).data.getXCoord();
        int nodeAY = roomNodes.get(nodeA).data.getYCoord();
        int nodeBX = roomNodes.get(nodeB).data.getXCoord();
        int nodeBY = roomNodes.get(nodeB).data.getYCoord();

        return (int) Math.sqrt(nodeBX - nodeAX) * (nodeBX - nodeAX) + (nodeBY - nodeAY) * (nodeBY - nodeAY);
    }

    public static <T> CostOfPath findCheapestPathDijkstra(GraphNode<?> startNode, T lookingfor) {
        CostOfPath cp = new CostOfPath(); //Create result object for cheapest path
        List<GraphNode<?>> encountered = new ArrayList<>(), unencountered = new ArrayList<>(); //Create encountered/unencountered lists
        startNode.nodeValue = 0; //Set the starting node value to zero
        unencountered.add(startNode); //Add the start node as the only value in the unencountered list to start
        GraphNode<?> currentNode;


        do { //Loop until unencountered list is empty
            currentNode = unencountered.remove(0); //Get the first unencountered node (sorted list, so will have lowest value)
            encountered.add(currentNode); //Record current node in encountered list
            if (currentNode.data.equals(lookingfor)) { //Found goal - assemble path list back to start and return it
                cp.pathList.add(currentNode); //Add the current (goal) node to the result list (only element)
                cp.pathCost = currentNode.nodeValue; //The total cheapest path cost is the node value of the current/goal node
                while (currentNode != startNode) { //While we're not back to the start node...
                    boolean foundPrevPathNode = false; //Use a flag to identify when the previous path node is identified
                    for (GraphNode<?> n : encountered) { //For each node in the encountered list...
                        for (GraphLink e : n.adjList) //For each edge from that node...
                            if (e.destNode == currentNode && currentNode.nodeValue - e.cost == n.nodeValue) { //If that edge links to the
//current node and the difference in node values is the cost of the edge -> found path node!
                                cp.pathList.add(0, n); //Add the identified path node to the front of the result list
                                currentNode = n; //Move the currentNode reference back to the identified path node
                                foundPrevPathNode = true; //Set the flag to break the outer loop
                                break; //We've found the correct previous path node and moved the currentNode reference
//back to it so break the inner loop
                            }
                        if (foundPrevPathNode)
                            break; //We've identified the previous path node, so break the inner loop to continue
                    }
                }
//Reset the node values for all nodes to (effectively) infinity so we can search again (leave no footprint!)
                for (GraphNode<?> n : encountered) n.nodeValue = Integer.MAX_VALUE;
                for (GraphNode<?> n : unencountered) n.nodeValue = Integer.MAX_VALUE;
                return cp; //The costed (cheapest) path has been assembled, so return it!
            }
//We're not at the goal node yet, so...
            for (GraphLink e : currentNode.adjList) //For each edge/link from the current node...
                if (!encountered.contains(e.destNode)) { //If the node it leads to has not yet been encountered (i.e. processed)
                    e.destNode.nodeValue = Integer.min(e.destNode.nodeValue, currentNode.nodeValue + e.cost); //Update the node value at the end
                    //of the edge to the minimum of its current value or the total of the current node's value plus the cost of the edge
                    unencountered.add(e.destNode);
                }
            Collections.sort(unencountered, (n1, n2) -> n1.nodeValue - n2.nodeValue); //Sort in ascending node value order
        } while (!unencountered.isEmpty());
        return null; //No path found, so return null
    }



    public static CostOfPath searchGraphDepthFirstCheapestPath2(GraphNode<POI> from, List<GraphNode<POI>> encountered, int totalCost, String lookingfor) {
        // Diagnostic: Starting exploration from a node
        System.out.printf("Exploring from node: %s (current total cost: %d)%n", from.data.getname(), totalCost);

        // Base case: Found the target node
        if (from.data.getname().equals(lookingfor)) {
            CostOfPath cp = new CostOfPath();
            cp.pathList.add(from);
            cp.pathCost = totalCost;
            System.out.printf("Target node '%s' found. Path cost: %d%n", lookingfor, totalCost);
            return cp;
        }

        // Initialize encountered list if not already initialized
        if (encountered == null) encountered = new ArrayList<>();
        encountered.add(from);

        // Track all potential paths from this node
        List<CostOfPath> allPaths = new ArrayList<>();
        System.out.printf("Adjacent nodes for '%s': %s%n", from.data, from.adjList.stream().map(adj -> adj.destNode.data).toList());

        // Loop through each adjacent node (link)
        for (GraphLink adjLink : from.adjList) {
            if (!encountered.contains(adjLink.destNode)) {
                System.out.printf("Moving to adjacent node '%s' (link cost: %d)%n", adjLink.destNode.data, adjLink.cost);

                // Recursive call to explore further paths
                CostOfPath temp = searchGraphDepthFirstCheapestPath2((GraphNode<POI>) adjLink.destNode, new ArrayList<>(encountered), totalCost + adjLink.cost, lookingfor);

                if (temp == null) {
                    System.out.printf("No valid path found from node '%s'%n", adjLink.destNode.data);
                    continue;
                }

                // Add the current node to the beginning of the path list
                temp.pathList.add(0, from);
                allPaths.add(temp);
            } else {
                System.out.printf("Skipping already encountered node '%s'%n", adjLink.destNode.data);
            }
        }

        // Return the cheapest path found or null if no paths were found
        if (allPaths.isEmpty()) {
            System.out.printf("No paths found from node '%s'%n", from.data);
            return null;
        }

        CostOfPath cheapestPath = Collections.min(allPaths, Comparator.comparingInt(p -> p.pathCost));
        System.out.printf("Cheapest path from node '%s' costs %d%n", from.data.getname(), cheapestPath.pathCost);
        return cheapestPath;
    }


    public static List<CostOfPath> findAllPathsDepthFirst2(GraphNode<POI> from, List<GraphNode<POI>> encountered, int totalCost, String lookingfor) {
        // Diagnostic: Starting exploration from a node
        System.out.printf("Exploring from node: %s (current total cost: %d)%n", from.data.getname(), totalCost);

        List<CostOfPath> allPaths = new ArrayList<>();

        // Base case: Found the target node
        if (from.data.getname().equals(lookingfor)) {
            CostOfPath cp = new CostOfPath();
            cp.pathList = new ArrayList<>();
            cp.pathList.add(from);
            cp.pathCost = totalCost; // As this is the target, use the accumulated cost.
            allPaths.add(cp);
            return allPaths;
        }

        // Initialize encountered list if not already initialized
        if (encountered == null) encountered = new ArrayList<>();
        encountered.add(from);

        // Explore each adjacent node
        for (GraphLink adjLink : from.adjList) {
            if (!encountered.contains(adjLink.destNode)) {
                System.out.printf("Moving to adjacent node '%s' (link cost: %d)%n", adjLink.destNode.data.toString(), adjLink.cost);

                // Recursive call to explore further paths
                List<CostOfPath> pathsFromNode = findAllPathsDepthFirst2((GraphNode<POI>) adjLink.destNode, new ArrayList<>(encountered), totalCost + adjLink.cost, lookingfor);

                for (CostOfPath path : pathsFromNode) {
                    // Prepend the current node to the path returned from recursion
                    path.pathList.add(0, from);
                    allPaths.add(path);
                }
            } else {
                System.out.printf("Skipping already encountered node '%s'%n", adjLink.destNode.data.toString());
            }
        }

        return allPaths;
    }








        }
