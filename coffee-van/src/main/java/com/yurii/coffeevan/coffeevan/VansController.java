package com.yurii.coffeevan.coffeevan;

import com.yurii.coffeevan.coffeevan.model.Van;
import com.yurii.coffeevan.coffeevan.db.VanService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VansController {
    
    @FXML
    private TableView<VanEntry> vansTable;
    
    @FXML
    private TableColumn<VanEntry, Integer> idColumn;
    
    @FXML
    private TableColumn<VanEntry, Integer> volumeColumn;
    
    @FXML
    private TableColumn<VanEntry, Timestamp> dateColumn;
    
    @FXML
    private Button showCoffeeButton;
    
    @FXML
    private Button closeButton;

    @FXML
    private Button deleteButton;
    
    private ObservableList<VanEntry> vans = FXCollections.observableArrayList();
    private VanService vanService;
    
    @FXML
    public void initialize() {
        vanService = new VanService();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("totalVolume"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        
        // Format date column
        dateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString().replace(".0", ""));
                }
            }
        });
        
        vansTable.setItems(vans);

        // Додаємо слухач вибору рядка для активації/деактивації кнопок
        vansTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean hasSelection = newSelection != null;
                deleteButton.setDisable(!hasSelection);
                showCoffeeButton.setDisable(!hasSelection);
            }
        );
        
        loadVans();
    }

    @FXML
    private void handleDeleteVan() {
        VanEntry selectedVan = vansTable.getSelectionModel().getSelectedItem();
        if (selectedVan != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Підтвердження видалення");
            confirmDialog.setHeaderText(null);
            confirmDialog.setContentText("Ви впевнені, що хочете видалити фургон #" + selectedVan.getId() + 
                                      " та всю його каву? Цю дію неможливо відмінити.");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    // Видаляємо фургон з бази даних
                    vanService.deleteVan(selectedVan.getId());
                    // Видаляємо фургон з таблиці
                    vans.remove(selectedVan);
                    showAlert(Alert.AlertType.INFORMATION, "Видалено", 
                            "Фургон #" + selectedVan.getId() + " успішно видалено!");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Помилка", 
                            "Не вдалося видалити фургон: " + e.getMessage());
                }
            }
        }
    }
    
    private void loadVans() {
        try {
            List<VanService.VanEntry> serviceEntries = vanService.loadAllVans();
            List<VanEntry> controllerEntries = serviceEntries.stream()
                .map(e -> new VanEntry(e.getId(), e.getTotalVolume(), e.getCreatedAt()))
                .collect(Collectors.toList());
            vans.setAll(controllerEntries);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося завантажити дані фургонів: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleShowCoffee() {
        VanEntry selectedVan = vansTable.getSelectionModel().getSelectedItem();
        if (selectedVan == null) {
            showAlert(Alert.AlertType.WARNING, "Попередження", "Будь ласка, виберіть фургон зі списку");
            return;
        }
        
        try {
            // Load coffee contents window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("van-coffee-view.fxml"));
            Parent root = loader.load();
            
            // Get controller and set data
            VanCoffeeController controller = loader.getController();
            controller.setVanInfo(selectedVan);
            controller.setCoffeeList(FXCollections.observableArrayList(vanService.loadVan(selectedVan.getId()).getAllCoffee()));
            
            // Show window
            Stage stage = new Stage();
            stage.setTitle("Вміст фургону #" + selectedVan.getId());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося відкрити вікно фургонів: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClose() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Inner class to represent van entries in the table
    public static class VanEntry {
        private final int id;
        private final int totalVolume;
        private final Timestamp createdAt;
        
        public VanEntry(int id, int totalVolume, Timestamp createdAt) {
            this.id = id;
            this.totalVolume = totalVolume;
            this.createdAt = createdAt;
        }
        
        public int getId() { return id; }
        public int getTotalVolume() { return totalVolume; }
        public Timestamp getCreatedAt() { return createdAt; }
    }
} 