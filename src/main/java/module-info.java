module ca2.routefinderparis {
    requires javafx.controls;
    requires javafx.fxml;
    requires junit;
    requires org.junit.jupiter.api;
    requires jmh.core;

    exports benchmarking.jmh_generated to jmh.core;
    exports testclass;
    exports main;
    opens main to javafx.fxml;
    exports controller;
    opens controller to javafx.fxml;
    exports other;
    opens other to javafx.fxml;
    exports models;
    opens models to javafx.fxml;
}
