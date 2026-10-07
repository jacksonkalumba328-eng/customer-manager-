package app;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {

    private final ObservableList<Customer> customers = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {
        // 1. Form: name field + province list
        TextField nameField = new TextField();
        nameField.setPromptText("Customer name");

        ComboBox<String> provinceBox = new ComboBox<>(FXCollections.observableArrayList(
                "Central",
                "Copperbelt",
                "Eastern",
                "Luapula",
                "Lusaka",
                "Muchinga",
                "Northern",
                "North-Western",
                "Southern",
                "Western"));
        provinceBox.setPromptText("Select province");

        Label nameLabel = new Label("_Name:");           // Alt+N
        nameLabel.setMnemonicParsing(true);
        nameLabel.setLabelFor(nameField);
        Label provLabel = new Label("_Province:");       // Alt+P
        provLabel.setMnemonicParsing(true);
        provLabel.setLabelFor(provinceBox);

        Label error = new Label();
        error.setStyle("-fx-text-fill: red;");

        // 3. TableView with name and province columns
        TableView<Customer> table = new TableView<>(customers);
        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        TableColumn<Customer, String> provCol = new TableColumn<>("Province");
        provCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProvince()));
        table.getColumns().addAll(nameCol, provCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        Button addBtn = new Button("_Add");               // Alt+A, Enter
        addBtn.setMnemonicParsing(true);
        addBtn.setDefaultButton(true);
        Button deleteBtn = new Button("_Delete");         // Alt+D
        deleteBtn.setMnemonicParsing(true);
        deleteBtn.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());

        // 4. Validate, then add
        addBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String province = provinceBox.getValue();
            if (name.isEmpty()) {
                error.setText("Name is required.");
                nameField.requestFocus();
            } else if (province == null) {
                error.setText("Please select a province.");
                provinceBox.requestFocus();
            } else {
                customers.add(new Customer(name, province));
                nameField.clear();
                provinceBox.setValue(null);
                error.setText("");
                nameField.requestFocus();
            }
        });

        // 5. Confirm deletion
        deleteBtn.setOnAction(e -> {
            Customer selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete " + selected.getName() + "?", ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText("Confirm deletion");
            confirm.showAndWait().ifPresent(b -> {
                if (b == ButtonType.YES) customers.remove(selected);
            });
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.addRow(0, nameLabel, nameField);
        form.addRow(1, provLabel, provinceBox);
        form.add(new HBox(8, addBtn, deleteBtn), 1, 2);
        form.add(error, 1, 3);

        VBox root = new VBox(12, form, table);
        root.setPadding(new Insets(15));
        VBox.setVgrow(table, Priority.ALWAYS);

        stage.setTitle("Customer Manager");
        stage.setScene(new Scene(root, 480, 420));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
