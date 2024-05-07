package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static javafx.scene.paint.Color.RED;

public class HelloController {

    @FXML
    private ImageView imageView1;

    @FXML
    private AnchorPane drawPane;

    @FXML
    public void initialize() {
        // Setup the ImageView or other necessary components
        setupImageView();

        // Load and draw nodes from the corrected CSV file
        drawNodesFromCSV();
    }

    private void setupImageView() {
        // Any setup needed for the imageView1 (optional)
    }

    /**
     * Reads the CSV file and draws circles for each node based on coordinates.
     * Also adds a label next to each circle to show the node name.
     */
    private void drawNodesFromCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\jeffh\\IdeaProjects\\RouteFinderParis\\src\\main\\resources\\Nodes.csv"))) {
            String line;

            while ((line = br.readLine()) != null) {
                // Split the CSV line
                String[] parts = line.split(",");
                if (parts.length != 3) {
                    continue; // Skip malformed rows
                }

                String nodeName = parts[0].trim();
                double x = Double.parseDouble(parts[1].trim());
                double y = Double.parseDouble(parts[2].trim());

                // Create a small red circle at the node coordinates
                Circle circle = new Circle(5, RED);
                circle.setCenterX(x);
                circle.setCenterY(y);

                // Create a label with the node name and position it next to the circle
                Label label = new Label(nodeName);
                label.setLayoutX(x + 8);  // Slightly offset the label to the right of the circle
                label.setLayoutY(y - 10); // Offset above the circle for better visibility

                // Add the circle and label to the drawPane AnchorPane
                drawPane.getChildren().addAll(circle, label);
            }
        } catch (IOException e) {
            e.printStackTrace();  // Error handling for reading the file
        }
    }
}
