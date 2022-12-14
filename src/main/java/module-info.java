module com.example.singleplatformer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;
    requires com.google.gson;

    opens com.example.singleplatformer to javafx.fxml;
    exports com.example.singleplatformer;
}