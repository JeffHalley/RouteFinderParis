package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import models.CostOfPath;
import models.Graph;
import models.GraphNode;
import models.POI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static javafx.scene.paint.Color.RED;

public class HelloController {

    @FXML
     ImageView imageView1;
    @FXML
    private AnchorPane drawPane;
    @FXML
     ComboBox<String> startBox; // Assumes String type for ComboBox items; adjust if necessary
    @FXML
     ComboBox<String> endBox; // Assumes String type for ComboBox items; adjust if necessary
    @FXML
     Button dijkstrasButton;
    @FXML
     Button DFSButton;
    @FXML
     Button ALLDFSButton;
    @FXML
     TreeView<String> DFSTreeView; // Assume this is already initialized in your FXML or somewhere in your code

    ////////////////////////////////////////////////////////////////////////

    private List<POI> pois = new ArrayList<>();

    private HashMap<String, GraphNode<POI>> poisHashMap = new HashMap<>();

    private List<String> names = new ArrayList<>();

    private List<String> landMarkNames = new ArrayList<>();

    private List<GraphNode<POI>> poiNodes = new ArrayList<>();

    private Color dijkstraColor, depthColor, allDepthColor;

    public GraphNode<POI> findGraphNode(String names) {
        return poisHashMap.get(names);
    }

    private List<CostOfPath> allPaths = new ArrayList<>();




    public void initialize() {
        loadDatabase();
        connectPois();
        dijkstraColor = RED;
        depthColor = Color.BLUE;
        allDepthColor = Color.PURPLE;
        List<String> filteredLandmarkNames = updateLandmarkNames(names);
        startBox.getItems().addAll(names);
        endBox.getItems().addAll(names);
    }



    /**
     * Reads the CSV file
     */

    private void loadDatabase() {
        try (BufferedReader br = new BufferedReader(new FileReader("src\\main\\resources\\Nodes.csv"))) {
            String line;

            while ((line = br.readLine()) != null) {
                // Split the CSV line and trim whitespace
                String[] parts = line.split(",");
                if (parts.length < 4) continue; // Ensure there are enough parts to avoid ArrayIndexOutOfBoundsException

                String name = parts[0].trim();
                int x = Integer.parseInt(parts[1].trim());
                int y = Integer.parseInt(parts[2].trim());
                boolean isLandmark = Boolean.parseBoolean(parts[3].trim());

                // Create and store POI object
                POI newPoi = new POI(name, x, y, isLandmark);
                GraphNode<POI> node = new GraphNode<>(newPoi);
                poiNodes.add(node);
                poisHashMap.put(name, node);
                names.add(name);
                pois.add(newPoi);

                // Create a small circle for the node's location
                Circle circle = new Circle(x, y, 5, isLandmark ? Color.GREEN : Color.RED);

                // Create a label to display the node's name
                Label label = new Label(name);
                label.setLayoutX(x + 7); // Slight offset for better positioning
                label.setLayoutY(y);
                label.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");

                // Add the circle and label to the drawPane
                drawPane.getChildren().addAll(circle, label);
            }
        } catch (IOException e) {
            e.printStackTrace();  // Error handling for reading the file
        }
    }


    public void connectNodes(String nodeA, String nodeB) {
        GraphNode<POI> PoiA = poisHashMap.get(nodeA);
        GraphNode<POI> PoiB = poisHashMap.get(nodeB);
        PoiA.connectToNodeUndirected(PoiB, 1);
    }

    private void connectPois() {

        String line = "";
        try {
            File file = new File("src/main/resources/Connections.csv");
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                connectNodes(parts[0], parts[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to filter and return names of landmarks only

    public List<String> updateLandmarkNames(List<String> names) {
        // Clear the list before updating, in case it has existing data
        landMarkNames.clear();

        // Iterate through each name in the names list
        for (String name : names) {
            // Check if the name does not contain "Node"
            if (!name.contains("Node")) {
                landMarkNames.add(name);
            }
        }
        return landMarkNames;
    }

    public void drawSinglePath(List<GraphNode<?>> pathList, Color c) {
        drawPane.getChildren().removeIf(node -> node instanceof Line);
        for (int i = 0; i < pathList.size(); i++) {
            GraphNode<POI> node = (GraphNode<POI>) pathList.get(i);

            if (i + 1 < pathList.size()) {
                GraphNode<POI> nextNode = (GraphNode<POI>) pathList.get(i + 1);
                Line l = new Line(node.data.getXCoord(), node.data.getYCoord(), nextNode.data.getXCoord(), nextNode.data.getYCoord());
                l.setFill(c);
                l.setStroke(c);
                l.setStrokeWidth(5);
                drawPane.getChildren().add(l);
            }

        }

    }

    @FXML
    public void findPathDij(ActionEvent actionEvent) {

        List<GraphNode<?>> pathList = new ArrayList<>();

            CostOfPath cp = Graph.findCheapestPathDijkstra(findGraphNode(startBox.getValue()), findGraphNode(endBox.getValue()).data);

            pathList = cp.pathList;


            drawSinglePath(pathList, dijkstraColor);

    }




    @FXML
    public void findDFSPath(ActionEvent actionEvent) {
        GraphNode<POI> startNode = findGraphNode(startBox.getValue());
        if (startNode == null) {
            System.out.println("Start node not found.");
            return;
        }
        String endValue = endBox.getValue();
        CostOfPath result = Graph.searchGraphDepthFirstCheapestPath2(startNode, new ArrayList<>(), 0, endValue);

        if (result != null && result.pathList != null && !result.pathList.isEmpty()) {
            drawSinglePath(result.pathList, depthColor);
        } else {
            System.out.println("No path found or an error occurred.");
        }
    }





    @FXML
    public void findAllDFSPaths(ActionEvent actionEvent) {
        GraphNode<POI> startNode = findGraphNode(startBox.getValue());
        allPaths = Graph.findAllPathsDepthFirst2(startNode, new ArrayList<>(), 0, endBox.getValue());

        TreeItem<String> rootItem = new TreeItem<>("All Paths");
        rootItem.setExpanded(true);

        if (allPaths != null && !allPaths.isEmpty()) {
            for (CostOfPath costPath : allPaths) {
                String pathLabel = "Path with Cost: " + costPath.pathCost;
                TreeItem<String> pathItem = new TreeItem<>(pathLabel);
                for (GraphNode<?> node : costPath.pathList) {
                    TreeItem<String> nodeItem = new TreeItem<>(node.data.toString());
                    pathItem.getChildren().add(nodeItem);
                }
                rootItem.getChildren().add(pathItem);
            }
            DFSTreeView.setRoot(rootItem);
        } else {
            DFSTreeView.setRoot(new TreeItem<>("No paths found."));
            System.out.println("No paths found.");
        }
    }




    @FXML
    public void drawPathFromTreeViewSelection() {
        TreeItem<String> selectedItem = DFSTreeView.getSelectionModel().getSelectedItem();

        // Check if the selected item is actually meant to represent a path
        if (selectedItem == null || selectedItem.getParent() == null || !selectedItem.getParent().getValue().equals("All Paths")) {
            System.out.println("No valid path selected.");
            return;
        }

        List<GraphNode<?>> pathNodes = new ArrayList<>();
        String pathCostLabel = selectedItem.getValue();

        boolean pathFound = false;
        for (CostOfPath costPath : allPaths) {
            String pathLabel = "Path with Cost: " + costPath.pathCost;
            if (pathLabel.equals(pathCostLabel)) {
                pathNodes.addAll(costPath.pathList);
                pathFound = true;
                break;
            }
        }

        if (pathFound && !pathNodes.isEmpty()) {
            drawSinglePath(pathNodes, depthColor);
        } else {
            System.out.println("Path data could not be found.");
        }
    }










}









