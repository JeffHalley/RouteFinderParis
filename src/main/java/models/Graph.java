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

    /**
     * @param nodeA     Starting Node
     * @param nodeB     End Node
     * @param roomNodes
     * @return the distance between both nodes
     */
    //https://www.khanacademy.org/math/geometry/hs-geo-analytic-geometry/hs-geo-distance-and-midpoints/v/distance-formula#:~:text=Learn%20how%20to%20find%20the,distance%20between%20any%20two%20points.
    //distance between two points formula
    public static int getCost(int nodeA, int nodeB, List<GraphNode<POI>> roomNodes) {
        int nodeAX = roomNodes.get(nodeA).data.getXCoord();
        int nodeAY = roomNodes.get(nodeA).data.getYCoord();
        int nodeBX = roomNodes.get(nodeB).data.getXCoord();
        int nodeBY = roomNodes.get(nodeB).data.getYCoord();

        return (int) Math.sqrt(nodeBX - nodeAX) * (nodeBX - nodeAX) + (nodeBY - nodeAY) * (nodeBY - nodeAY);
    }


    /**
     * BreadthFirstSearch taken from notes
     *
     * @param startNode
     * @param lookingfor
     * @param <T>
     * @return
     */
    //Interface method to allow just the starting node and the goal node data to match to be specified
    public static <T> List<GraphNode<?>> findPathBreadthFirstInterface(GraphNode<?> startNode, T lookingfor) {
        List<List<GraphNode<?>>> agenda = new ArrayList<>(); //Agenda comprised of path lists here!
        List<GraphNode<?>> firstAgendaPath = new ArrayList<>(), resultPath;
        firstAgendaPath.add(startNode);
        agenda.add(firstAgendaPath);
        resultPath = findPathBreadthFirst(agenda, null, lookingfor); //Get single BFS path (will be shortest)
        Collections.reverse(resultPath); //Reverse path (currently has the goal node as the first item)
        return resultPath;
    }

    //Agenda list based breadth-first graph search returning a single reversed path (tail recursive)
    public static <T> List<GraphNode<?>> findPathBreadthFirst(List<List<GraphNode<?>>> agenda,
                                                              List<GraphNode<?>> encountered, T lookingfor) {
        if (agenda.isEmpty()) return null; //Search failed
        List<GraphNode<?>> nextPath = agenda.remove(0); //Get first item (next path to consider) off agenda
        GraphNode<?> currentNode = nextPath.get(0); //The first item in the next path is the current node
        if (currentNode.data.equals(lookingfor))
            return nextPath; //If that's the goal, we've found our path (so return it)
        if (encountered == null)
            encountered = new ArrayList<>(); //First node considered in search so create new (empty)
        //encountered list
        if (!encountered.contains(currentNode)) {
            encountered.add(currentNode);//Record current node as encountered so it isn't revisited again
            for (GraphLink adjNode : currentNode.adjList) { //For each adjacent node
                //System.out.println("\t" + adjNode.destNode.data);
                if (!encountered.contains(adjNode)) { //If it hasn't already been encountered
                    List<GraphNode<?>> newPath = new ArrayList<>(nextPath); //Create a new path list as a copy of
                    //the current/next path
                    newPath.add(0, adjNode.destNode); //And add the adjacent node to the front of the new copy
                    agenda.add(newPath); //Add the new path to the end of agenda (end->BFS!)
                }
            }
        }
        return findPathBreadthFirst(agenda, encountered, lookingfor); //Tail call
    }

    public static <T> List<GraphNode<?>> findBreadPath(GraphNode<?> startNode, T lookingfor) {
        List<GraphNode<?>> encountered = new ArrayList<>();
        List<List<GraphNode<?>>> agenda = new ArrayList<>(); //Agenda comprised of path lists here!
        List<GraphNode<?>> firstAgendaPath = new ArrayList<>();
        List<GraphNode<?>> resultPath = new ArrayList<>();
        firstAgendaPath.add(startNode);
        agenda.add(firstAgendaPath);
        while (!agenda.isEmpty()) {
            List<GraphNode<?>> nextPath = agenda.remove(0);
            GraphNode<?> currentNode = nextPath.get(0);
            if (currentNode.data.equals(lookingfor)) {
                resultPath = nextPath;
                break;
            }

            if (encountered == null)
                encountered = new ArrayList<>(); //First node considered in search so create new (empty)
            //encountered list
            if (!encountered.contains(currentNode)) {
                encountered.add(currentNode);//Record current node as encountered so it isn't revisited again
//            System.out.println("=============");
//            System.out.println(currentNode.data);
                for (GraphLink adjNode : currentNode.adjList) { //For each adjacent node
                    //System.out.println("\t" + adjNode.destNode.data);
                    if (!encountered.contains(adjNode)) { //If it hasn't already been encountered
//                    System.out.println("\t" + "\t" + adjNode.destNode.data);
                        List<GraphNode<?>> newPath = new ArrayList<>(nextPath); //Create a new path list as a copy of
                        //the current/next path
                        newPath.add(0, adjNode.destNode); //And add the adjacent node to the front of the new copy
                        agenda.add(newPath); //Add the new path to the end of agenda (end->BFS!)
                    }
                }
            }
        }
        Collections.reverse(resultPath); //Reverse path (currently has the goal node as the first item)
        return resultPath;
    }

    //Recursive depth-first search of graph (all paths identified returned)
    public static <T> List<List<GraphNode<?>>> findAllPathsDepthFirst(GraphNode<?> from, List<GraphNode<?>> encountered, T lookingfor) {
        List<List<GraphNode<?>>> result = null, temp2;
        if (from.data.equals(lookingfor)) { //Found it
            List<GraphNode<?>> temp = new ArrayList<>(); //Create new single solution path list
            temp.add(from); //Add current node to the new single path list
            result = new ArrayList<>(); //Create new "list of lists" to store path permutations
            result.add(temp); //Add the new single path list to the path permutations list
            return result; //Return the path permutations list
        }
        if (encountered == null) encountered = new ArrayList<>(); //First node so create new (empty) encountered list
        if (!encountered.contains(from)) {
            encountered.add(from); //Add current node to encountered list
            for (GraphLink adjNode : from.adjList) {
                if (!encountered.contains(adjNode)) {
                    temp2 = findAllPathsDepthFirst(adjNode.destNode, new ArrayList<>(encountered), lookingfor); //Use clone of encountered list
//for recursive call!
                    if (temp2 != null) { //Result of the recursive call contains one or more paths to the solution node
                        for (List<GraphNode<?>> x : temp2) //For each partial path list returned
                            x.add(0, from); //Add the current node to the front of each path list
                        if (result == null)
                            result = temp2; //If this is the first set of solution paths found use it as the result
                        else result.addAll(temp2); //Otherwise append them to the previously found paths
                    }
                }
            }
        }
        return result;
    }

    //Retrieve cheapest path by expanding all paths recursively depth-first
    public static <T> CostOfPath searchGraphDepthFirstCheapestPath(GraphNode<?> from, List<GraphNode<?>> encountered,
                                                                   int totalCost, T lookingfor) {
        if (from.data.equals(lookingfor)) { //Found it - end of path
            CostOfPath cp = new CostOfPath(); //Create a new CostedPath object
            cp.pathList.add(from); //Add the current node to it - only (end of path) element
            cp.pathCost = totalCost; //Use the current total cost
            return cp; //Return the CostedPath object
        }
        if (encountered == null) encountered = new ArrayList<>(); //First node so create new (empty) encountered list
        encountered.add(from);
        List<CostOfPath> allPaths = new ArrayList<>(); //Collection for all candidate costed paths from this node
        for (GraphLink adjLink : from.adjList) //For every adjacent node
            if (!encountered.contains(adjLink.destNode)) //That has not yet been encountered
            {
                //Create a new CostedPath from this node to the searched for item (if a valid path exists)
                CostOfPath temp = searchGraphDepthFirstCheapestPath(adjLink.destNode, new ArrayList<>(encountered),
                        totalCost + adjLink.cost, lookingfor);
                if (temp == null) continue; //No path was found, so continue to the next iteration
                temp.pathList.add(0, from); //Add the current node to the front of the path list
                allPaths.add(temp); //Add the new candidate path to the list of all costed paths
            }
        //If no paths were found then return null. Otherwise, return the cheapest path (i.e. the one with min pathCost)
        return allPaths.isEmpty() ? null : Collections.min(allPaths, (p1, p2) -> p1.pathCost - p2.pathCost);

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







}
