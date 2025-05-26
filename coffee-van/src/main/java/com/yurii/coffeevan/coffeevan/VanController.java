package com.yurii.coffeevan.coffeevan;

import com.yurii.coffeevan.coffeevan.model.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

// JavaFX контролери
//public class VanController {
//    @FXML private TableView<Coffee> coffeeTable;
//    @FXML private ComboBox<String> typeFilter;
//    @FXML private Slider qualitySlider;
//    @FXML private Label qualityLabel;
//    @FXML private ChoiceBox<String> sortChoice;
//
//    @FXML private TextField nameField;
//    @FXML private ComboBox<String> typeField;
//    @FXML private TextField volumeField;
//    @FXML private TextField priceField;
//    @FXML private TextField weightField;
//    @FXML private Slider qualityField;
//    @FXML private Label qualityValueLabel;
//    @FXML private Button addButton;
//
//    @FXML private TableColumn<Coffee, String> nameColumn;
//    @FXML private TableColumn<Coffee, String> typeColumn;
//    @FXML private TableColumn<Coffee, Integer> volumeColumn;
//    @FXML private TableColumn<Coffee, Double> priceColumn;
//    @FXML private TableColumn<Coffee, Integer> weightColumn;
//    @FXML private TableColumn<Coffee, Integer> qualityColumn;
//
//    private ObservableList<Coffee> coffeeList = FXCollections.observableArrayList();
//
//    @FXML
//    public void initialize() {
//        // 1. Ініціалізація типів кави
//        typeField.setItems(FXCollections.observableArrayList("Зернова", "Мелена", "Розчинна (банка)", "Розчинна (пакетик)"));
//        typeFilter.setItems(typeField.getItems());
//
//        // 2. Ініціалізація таблиці
//        initTableColumns();
//        coffeeTable.setItems(coffeeList);
//
//        // 3. Slider якість (додавання значення в Label)
//        qualityField.valueProperty().addListener((obs, oldVal, newVal) ->
//                qualityValueLabel.setText(String.valueOf(newVal.intValue()))
//        );
//
//        qualitySlider.valueProperty().addListener((obs, oldVal, newVal) ->
//                qualityLabel.setText(String.valueOf(newVal.intValue()))
//        );
//
//        // 4. Ініціалізація сортування
//        sortChoice.setItems(FXCollections.observableArrayList("Ціна / вага", "Якість"));
//    }
//
//    private void initTableColumns() {
//        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
//        typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
//        volumeColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getVolume()).asObject());
//        priceColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrice()).asObject());
//        weightColumn.setCellValueFactory(data -> new SimpleIntegerProperty((int) data.getValue().getWeight()).asObject());
//        qualityColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuality()).asObject());
//    }
//
//
//    @FXML
//    public void handleAddCoffee() {
//        try {
//            String name = nameField.getText();
//            String type = typeField.getValue();
//            int volume = Integer.parseInt(volumeField.getText());
//            double price = Double.parseDouble(priceField.getText());
//            int weight = Integer.parseInt(weightField.getText());
//            int quality = (int) qualityField.getValue();
//
//            Coffee coffee = new Coffee(name, type, volume, price, weight, quality);
//            coffeeList.add(coffee);
//
//            clearForm();
//        } catch (Exception e) {
//            showAlert("Помилка вводу", "Будь ласка, заповніть усі поля правильно.");
//        }
//    }
//
//    private void clearForm() {
//        nameField.clear();
//        typeField.setValue(null);
//        volumeField.clear();
//        priceField.clear();
//        weightField.clear();
//        qualityField.setValue(0);
//    }
//
//    private void showAlert(String title, String content) {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle(title);
//        alert.setContentText(content);
//        alert.showAndWait();
//    }
//}

public class VanController {

    @FXML
    private TableView<Coffee> coffeeTable;
    @FXML
    private ComboBox<String> typeFilter;
    @FXML
    private Slider qualitySlider;
    @FXML
    private Label qualityLabel;
    @FXML
    private ChoiceBox<String> sortChoice;

    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> typeField;
    @FXML
    private TextField volumeField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField weightField;
    @FXML
    private Slider qualityField;
    @FXML
    private Label qualityValueLabel;

    @FXML
    private TableColumn<Coffee, String> nameColumn;
    @FXML
    private TableColumn<Coffee, String> typeColumn;
    @FXML
    private TableColumn<Coffee, Integer> volumeColumn;
    @FXML
    private TableColumn<Coffee, Double> priceColumn;
    @FXML
    private TableColumn<Coffee, Integer> weightColumn;
    @FXML
    private TableColumn<Coffee, Integer> qualityColumn;
    @FXML
    private TableColumn<Coffee, Integer> quantityColumn; // показує кількість

    @FXML
    private Label totalVolumeLabel;

    private Van van;
    private ObservableList<Coffee> coffeeList;

    @FXML
    public void initialize() {
        van = new Van();
        coffeeList = FXCollections.observableArrayList();
        coffeeTable.setItems(coffeeList);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        qualityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity")); // або "quantity" якщо перейменував


        // Ініціалізація типів кави для ComboBox
        typeField.getItems().addAll("Зернова", "Мелена", "Розчинна в банках", "Розчинна в пакетиках");
        typeFilter.getItems().addAll("Зернова", "Мелена", "Розчинна в банках", "Розчинна в пакетиках");

        qualityField.valueProperty().addListener((obs, oldVal, newVal) ->
                qualityValueLabel.setText(String.valueOf(newVal.intValue())));

        qualitySlider.valueProperty().addListener((obs, oldVal, newVal) ->
                qualityLabel.setText(String.valueOf(newVal.intValue())));
    }

    @FXML
    private void handleAddCoffee() {
        try {
            String name = nameField.getText();
            String type = typeField.getValue();
            int volume = Integer.parseInt(volumeField.getText());
            double price = Double.parseDouble(priceField.getText());
            int weight = Integer.parseInt(weightField.getText());
            int quantity  = (int) qualityField.getValue();
            int quality  = 0;


            Coffee coffee = switch (type) {
                case "Зернова" -> new BeanCoffee(name, volume, price, weight, quality, quantity);
                case "Мелена" -> new GroundCoffee(name, volume, price, weight, quality, quantity);
                case "Розчинна в банках" -> new InstantJarCoffee(name, volume, price, weight, quality, quantity);
                case "Розчинна в пакетиках" -> new InstantPacketCoffee(name, volume, price, weight, quality, quantity);
                default -> throw new IllegalArgumentException("Невідомий тип кави");
            };

            if (van.addCoffee(coffee)) {
                coffeeList.setAll(van.getAllCoffee());
                updateTotalVolume();
                clearInputFields();
            } else {
                showAlert("Перевищено обʼєм фургона", "Неможливо додати каву. Фургон переповнений.");

            }
        } catch (Exception e) {
            showAlert("Помилка", "Перевірте правильність введених даних.");
        }
    }

    private void updateTotalVolume() {
        int total = van.getTotalVolume();
        totalVolumeLabel.setText(total + " мл");
    }

    private void clearInputFields() {
        nameField.clear();
        volumeField.clear();
        priceField.clear();
        weightField.clear();
        qualityField.setValue(0);
        typeField.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}