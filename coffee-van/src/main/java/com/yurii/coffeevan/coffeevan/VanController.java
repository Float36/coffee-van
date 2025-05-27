package com.yurii.coffeevan.coffeevan;

import com.yurii.coffeevan.coffeevan.model.*;
import com.yurii.coffeevan.coffeevan.db.DatabaseConnection;
import com.yurii.coffeevan.coffeevan.db.VanService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;

public class VanController {
    @FXML private TableView<Coffee> coffeeTable;
    @FXML private ComboBox<String> typeFilter;
    @FXML private ChoiceBox<String> qualityTypeChoice;
    @FXML private Label qualityLabel;
    @FXML private ChoiceBox<String> sortChoice;

    @FXML private TextField nameField;
    @FXML private ComboBox<String> typeField;
    @FXML private TextField volumeField;
    @FXML private TextField priceField;
    @FXML private TextField weightField;
    @FXML private Slider qualityField;
    @FXML private Label qualityValueLabel;
    @FXML private Slider coffeeQualitySlider;
    @FXML private Label coffeeQualityLabel;
    @FXML private Button addButton;
    @FXML private Button loadButton;
    @FXML private Button clearButton;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Label totalVolumeLabel;

    @FXML private TableColumn<Coffee, String> nameColumn;
    @FXML private TableColumn<Coffee, String> typeColumn;
    @FXML private TableColumn<Coffee, Integer> volumeColumn;
    @FXML private TableColumn<Coffee, Double> priceColumn;
    @FXML private TableColumn<Coffee, Integer> weightColumn;
    @FXML private TableColumn<Coffee, Integer> qualityColumn;
    @FXML private TableColumn<Coffee, String> qualityTypeColumn;
    @FXML private TableColumn<Coffee, Integer> quantityColumn;

    private Van van;
    private ObservableList<Coffee> allCoffee;
    private FilteredList<Coffee> filteredCoffee;
    private SortedList<Coffee> sortedCoffee;

    @FXML
    public void initialize() {
        // Ініціалізація фургона та списків
        van = new Van();
        allCoffee = FXCollections.observableArrayList();
        filteredCoffee = new FilteredList<>(allCoffee, p -> true);
        sortedCoffee = new SortedList<>(filteredCoffee);
        
        // Налаштування таблиці
        coffeeTable.setItems(sortedCoffee);
        
        // Налаштування колонок
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        qualityColumn.setCellValueFactory(new PropertyValueFactory<>("quality"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qualityTypeColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(getQualityType(data.getValue().getQuality())));

        // Налаштування ComboBox для типів кави
        typeField.setItems(FXCollections.observableArrayList(
            "Зернова", 
            "Мелена", 
            "Розчинна (банка)", 
            "Розчинна (пакетик)"
        ));

        // Налаштування фільтра типів
        typeFilter.setItems(FXCollections.observableArrayList(
            "Всі типи",
            "Зернова", 
            "Мелена", 
            "Розчинна (банка)", 
            "Розчинна (пакетик)"
        ));
        typeFilter.setValue("Всі типи");

        // Налаштування фільтра за типом якості
        qualityTypeChoice.setItems(FXCollections.observableArrayList(
            "Всі типи",
            "Низька якість",
            "Нижче середньої",
            "Середня",
            "Висока",
            "Преміум"
        ));
        qualityTypeChoice.setValue("Всі типи");

        // Налаштування вибору сортування
        sortChoice.setItems(FXCollections.observableArrayList(
            "Без сортування",
            "Ціна/вага (зростання)",
            "Ціна/вага (спадання)"
        ));
        sortChoice.setValue("Без сортування");

        // Налаштування слайдерів
        qualityField.setValue(1);
        coffeeQualitySlider.setValue(50);

        // Налаштування слухачів подій
        qualityField.valueProperty().addListener((obs, oldVal, newVal) ->
            qualityValueLabel.setText(String.valueOf(newVal.intValue())));

        coffeeQualitySlider.valueProperty().addListener((obs, oldVal, newVal) ->
            coffeeQualityLabel.setText(String.valueOf(newVal.intValue())));

        typeFilter.setOnAction(e -> applyFilter());
        qualityTypeChoice.setOnAction(e -> applyFilter());
        sortChoice.setOnAction(e -> applySorting());

        // Налаштування кнопок
        saveButton.setOnAction(event -> handleSaveVan());
        clearButton.setOnAction(event -> handleClearVan());
        loadButton.setOnAction(event -> handleShowVans());
        deleteButton.setDisable(true);

        // Додаємо слухач вибору рядка в таблиці
        coffeeTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> deleteButton.setDisable(newSelection == null)
        );

        // Ініціалізація бази даних
        DatabaseConnection.initializeDatabase();
    }

    private String getQualityType(int quality) {
        if (quality >= 1 && quality <= 20) return "Низька якість";
        if (quality >= 21 && quality <= 40) return "Нижче середньої";
        if (quality >= 41 && quality <= 60) return "Середня";
        if (quality >= 61 && quality <= 80) return "Висока";
        if (quality >= 81 && quality <= 100) return "Преміум";
        return "Невідома якість";
    }

    private void applyFilter() {
        String selectedType = typeFilter.getValue();
        String selectedQualityType = qualityTypeChoice.getValue();
        
        filteredCoffee.setPredicate(coffee -> {
            boolean typeMatch = selectedType == null || 
                              selectedType.equals("Всі типи") || 
                              coffee.getType().equals(selectedType);
            
            boolean qualityMatch = selectedQualityType == null || 
                                 selectedQualityType.equals("Всі типи") || 
                                 getQualityType(coffee.getQuality()).equals(selectedQualityType);
            
            return typeMatch && qualityMatch;
        });
        
        updateTotalVolume();
    }

    private void applySorting() {
        String sortType = sortChoice.getValue();
        if (sortType == null) return;

        coffeeTable.getSortOrder().clear();
        
        switch (sortType) {
            case "Ціна/вага (зростання)" -> {
                sortedCoffee.setComparator((c1, c2) -> 
                    Double.compare(c1.getPrice() / c1.getWeight(), c2.getPrice() / c2.getWeight()));
            }
            case "Ціна/вага (спадання)" -> {
                sortedCoffee.setComparator((c1, c2) -> 
                    Double.compare(c2.getPrice() / c2.getWeight(), c1.getPrice() / c1.getWeight()));
            }
            case "Якість (зростання)" -> {
                sortedCoffee.setComparator((c1, c2) -> 
                    Integer.compare(c1.getQuality(), c2.getQuality()));
            }
            case "Якість (спадання)" -> {
                sortedCoffee.setComparator((c1, c2) -> 
                    Integer.compare(c2.getQuality(), c1.getQuality()));
            }
            default -> sortedCoffee.setComparator(null);
        }
    }

    private void updateTotalVolume() {
        int totalVolume = filteredCoffee.stream()
                .mapToInt(coffee -> coffee.getVolume() * coffee.getQuantity())
                .sum();
        totalVolumeLabel.setText(String.format("%d мл", totalVolume));
    }

    @FXML
    private void handleAddCoffee() {
        try {
            // Валідація введених даних
            if (nameField.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Попередження", "Введіть назву кави");
                return;
            }
            
            if (typeField.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Попередження", "Виберіть тип кави");
                return;
            }

            if (volumeField.getText().isEmpty() || priceField.getText().isEmpty() || weightField.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Попередження", "Заповніть всі поля");
                return;
            }

            if (qualityField == null || coffeeQualitySlider == null) {
                showAlert(Alert.AlertType.ERROR, "Помилка", "Помилка ініціалізації слайдерів");
                return;
            }

            // Зчитування даних
            String name = nameField.getText();
            String type = typeField.getValue();
            int volume = Integer.parseInt(volumeField.getText());
            double price = Double.parseDouble(priceField.getText());
            int weight = Integer.parseInt(weightField.getText());
            int quantity = (int) qualityField.getValue();
            int quality = (int) coffeeQualitySlider.getValue();

            // Створення об'єкту кави
            Coffee coffee = switch (type) {
                case "Зернова" -> new BeanCoffee(name, volume, price, weight, quality, quantity);
                case "Мелена" -> new GroundCoffee(name, volume, price, weight, quality, quantity);
                case "Розчинна (банка)" -> new InstantJarCoffee(name, volume, price, weight, quality, quantity);
                case "Розчинна (пакетик)" -> new InstantPacketCoffee(name, volume, price, weight, quality, quantity);
                default -> throw new IllegalArgumentException("Невідомий тип кави");
            };

            // Перевірка, чи не перевищить загальний об'єм максимальну місткість фургона
            int newTotalVolume = van.getTotalVolume() + (coffee.getVolume() * coffee.getQuantity());
            if (newTotalVolume > van.getMaxCapacity()) {
                showAlert(Alert.AlertType.WARNING, "Перевищено обʼєм фургона", 
                    String.format("Неможливо додати каву. Новий загальний об'єм (%d мл) перевищить місткість фургона (%d мл).",
                        newTotalVolume, (int)van.getMaxCapacity()));
                return;
            }

            // Додавання кави до фургону
            van.addCoffee(coffee);
            allCoffee.setAll(van.getAllCoffee());
            applyFilter();
            clearInputFields();
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Перевірте правильність числових значень");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Виникла помилка при додаванні кави: " + e.getMessage());
        }
    }

    private void clearInputFields() {
        nameField.clear();
        typeField.setValue(null);
        volumeField.clear();
        priceField.clear();
        weightField.clear();
        qualityField.setValue(1);  // Reset quantity slider to minimum
        coffeeQualitySlider.setValue(50);  // Reset quality slider to default value
        qualityValueLabel.setText("1");
        coffeeQualityLabel.setText("50");
    }

    private void handleSaveVan() {
        try {
            int vanId = VanService.saveVan(van);
            showAlert(Alert.AlertType.INFORMATION, "Успіх", "Фургон успішно збережено в базі даних!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося зберегти фургон: " + e.getMessage());
        }
    }

    private void handleClearVan() {
        van = new Van();
        allCoffee.clear();
        updateTotalVolume();
        showAlert(Alert.AlertType.INFORMATION, "Очищено", "Фургон успішно очищено!");
    }

    private void handleShowVans() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("vans-view.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Фургони з завантаженою кавою");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося відкрити вікно фургонів: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteSelected() {
        Coffee selectedCoffee = coffeeTable.getSelectionModel().getSelectedItem();
        if (selectedCoffee != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Підтвердження видалення");
            confirmDialog.setHeaderText(null);
            confirmDialog.setContentText("Ви впевнені, що хочете видалити вибрану каву?");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Видаляємо з фургона
                van.removeCoffee(selectedCoffee);
                // Оновлюємо список кави в таблиці
                allCoffee.setAll(van.getAllCoffee());
                updateTotalVolume();
                showAlert(Alert.AlertType.INFORMATION, "Видалено", "Каву успішно видалено з фургону!");
            }
        }
    }

    // Update showAlert method to accept AlertType
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}