module ca2.routefinderparis {
    requires javafx.controls;
    requires javafx.fxml;


    exports main;
    opens main to javafx.fxml;
    exports controller;
    opens controller to javafx.fxml;
    exports other;
    opens other to javafx.fxml;
    exports models;
    opens models to javafx.fxml;
}