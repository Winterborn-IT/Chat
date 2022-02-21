module level2.lesson4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens level2.lesson4 to javafx.fxml;
    exports level2.lesson4;
}