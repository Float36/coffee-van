module com.yurii.coffeevan.coffeevan {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.yurii.coffeevan.coffeevan to javafx.fxml;
    opens com.yurii.coffeevan.coffeevan.model to javafx.base;

    exports com.yurii.coffeevan.coffeevan;
}