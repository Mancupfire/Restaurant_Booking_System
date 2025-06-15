package restaurantmanagementsys;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class dashboardController implements Initializable {

    // ── FXML ANCHORS & CONTROLS ─────────────────────────────────────────────────
    @FXML private AnchorPane main_form;

    @FXML private Button close;
    @FXML private Button minimize;
    @FXML private Label username;

    @FXML private Button dashboard_btn;
    @FXML private Button avaialbeFD_btn;
    @FXML private Button order_btn;
    @FXML private Button logout;

    @FXML private AnchorPane dashboard_form;
    @FXML private Label dashboard_NC;       // Numbers of Customers (tổng số giao dịch)
    @FXML private Label dashboard_TI;       // Today’s Income
    @FXML private Label dashboard_TIncome;  // Total Income (tổng tất cả lịch sử)

    // Chart vẽ theo “hóa đơn” (historyList) nhóm theo ngày
    @FXML private BarChart<String, Number>  dashboard_NOCChart;
    @FXML private AreaChart<String, Number> dashboard_ICChart;

    // Nút để hiển thị ngày hiện tại
    @FXML private Button showDateBtn;
    @FXML private Label currentDateLabel;   // hiển thị ngày ngay dưới nút, nếu cần

    // ── “Available Foods/Drinks” FORM ────────────────────────────────────────────
    @FXML private AnchorPane availableFD_form;
    @FXML private TextField availableFD_productID;
    @FXML private TextField availableFD_productName;
    @FXML private ComboBox<String> availableFD_productType;
    @FXML private TextField availableFD_productPrice;
    @FXML private ComboBox<String> availableFD_productStatus;

    @FXML private Button availableFD_addBtn;
    @FXML private Button availableFD_updateBtn;
    @FXML private Button availableFD_clearBtn;
    @FXML private Button availableFD_deleteBtn;

    @FXML private TextField availableFD_search;
    @FXML private TableView<categories> availableFD_tableView;
    @FXML private TableColumn<categories, String> availableFD_col_productID;
    @FXML private TableColumn<categories, String> availableFD_col_productName;
    @FXML private TableColumn<categories, String> availableFD_col_type;
    @FXML private TableColumn<categories, String> availableFD_col_price;
    @FXML private TableColumn<categories, String> availableFD_col_status;

    // ── “Order” FORM ─────────────────────────────────────────────────────────────
    @FXML private AnchorPane order_form;
    @FXML private TableView<product> order_tableView;
    @FXML private TableColumn<product, String> order_col_productID;
    @FXML private TableColumn<product, String> order_col_productName;
    @FXML private TableColumn<product, String> order_col_tyoe;
    @FXML private TableColumn<product, String> order_col_price;
    @FXML private TableColumn<product, String> order_col_quantity;

    @FXML private ComboBox<String> order_productID;
    @FXML private ComboBox<String> order_productName;
    @FXML private Spinner<Integer> order_quantity;
    @FXML private Button order_addBtn;
    @FXML private Label order_total;
    @FXML private TextField order_amount;
    @FXML private Label order_balance;
    @FXML private Button order_payBtn;
    @FXML private Button order_receiptBtn;
    @FXML private Button order_removeBtn;

    // ── INTERNAL STATE ───────────────────────────────────────────────────────────
    private ObservableList<categories> availableFDList;
    private FilteredList<categories> filteredList;
    private SortedList<categories>   sortedList;

    private ObservableList<product>     orderData;
    private SpinnerValueFactory<Integer> spinner;
    private int qty = 0;
    private double totalP = 0;     // Tổng tiền hiện tại của cart
    private double amount = 0;     // Số tiền khách đưa
    private double balance = 0;    // Tiền thối
    private int selectedOrderItemId = 0;

    // ── Dragging support: class‐level fields ─────────────────────────────────────
    private double x = 0;
    private double y = 0;

    // ──────────────────────────────────────────────────────────────────────────────
    // 1) DASHBOARD LABELS & CHARTS: dựa trên DataStore.historyList (danh sách lịch sử thanh toán)
    // ──────────────────────────────────────────────────────────────────────────────

    /** Cập nhật “Numbers of Customers” = số lượng transaction (historyList.size()). **/
    private void dashboardNC() {
        int nc = DataStore.historyList.size();
        dashboard_NC.setText(String.valueOf(nc));
    }

    /** Cập nhật “Today’s Income” = tổng tất cả các transaction có date == hôm nay. **/
    private void dashboardTI() {
        LocalDate today = LocalDate.now();
        double sumToday = 0;
        for (Transaction t : DataStore.historyList) {
            if (t.getDate().equals(today)) {
                sumToday += t.getTotal();
            }
        }
        dashboard_TI.setText("$" + String.format("%.2f", sumToday));
    }

    /** Cập nhật “Total Income” = tổng tất cả transaction trong historyList. **/
    private void dashboardTIncome() {
        double sumAll = 0;
        for (Transaction t : DataStore.historyList) {
            sumAll += t.getTotal();
        }
        dashboard_TIncome.setText("$" + String.format("%.2f", sumAll));
    }

    /**
     * Cập nhật BarChart “Numbers of Orders Chart” theo ngày:
     *  - Trục X = ngày (định dạng yyyy-MM-dd)
     *  - Trục Y = số transaction trong ngày đó
     */
    private void dashboardNOCCChart() {
        dashboard_NOCChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<LocalDate, Integer> countByDate = new HashMap<>();

        for (Transaction t : DataStore.historyList) {
            LocalDate d = t.getDate();
            countByDate.put(d, countByDate.getOrDefault(d, 0) + 1);
        }
        for (LocalDate date : countByDate.keySet()) {
            series.getData().add(new XYChart.Data<>(date.toString(), countByDate.get(date)));
        }
        dashboard_NOCChart.getData().add(series);
    }

    /**
     * Cập nhật AreaChart “Income Chart” theo ngày:
     *  - Trục X = ngày (yyyy-MM-dd)
     *  - Trục Y = tổng tiền của các transaction trong ngày
     */
    private void dashboardICC() {
        dashboard_ICChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<LocalDate, Double> incomeByDate = new HashMap<>();

        for (Transaction t : DataStore.historyList) {
            LocalDate d = t.getDate();
            incomeByDate.put(d, incomeByDate.getOrDefault(d, 0.0) + t.getTotal());
        }
        for (LocalDate date : incomeByDate.keySet()) {
            series.getData().add(new XYChart.Data<>(date.toString(), incomeByDate.get(date)));
        }
        dashboard_ICChart.getData().add(series);
    }

    /** Khi bấm nút Show Date, hiển thị ngày hiện tại (LocalDate.now()). **/
    @FXML
    public void showCurrentDate(ActionEvent event) {
        LocalDate today = LocalDate.now();
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Current Date");
        alert.setHeaderText(null);
        alert.setContentText("Ngày hôm nay: " + today.toString());
        alert.showAndWait();

        // Nếu bạn muốn gán luôn vào 1 label:
        if (currentDateLabel != null) {
            currentDateLabel.setText("Hôm nay: " + today.toString());
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    // 2) “Available Foods/Drinks” Handlers: CRUD trên DataStore.categoriesList
    // ──────────────────────────────────────────────────────────────────────────────
    public void availableFDAdd() {
        String id     = availableFD_productID.getText().trim();
        String name   = availableFD_productName.getText().trim();
        String type   = availableFD_productType.getValue();
        String priceS = availableFD_productPrice.getText().trim();
        String status = availableFD_productStatus.getValue();

        Alert alert;
        if (id.isEmpty() || name.isEmpty() || type == null || priceS.isEmpty() || status == null) {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
            return;
        }

        for (categories c : DataStore.categoriesList) {
            if (c.getProductId().equals(id)) {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Product ID: " + id + " already exists!");
                alert.showAndWait();
                return;
            }
        }

        double price;
        try {
            price = Double.parseDouble(priceS);
        } catch (NumberFormatException e) {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Price must be a valid number");
            alert.showAndWait();
            return;
        }

        categories newCat = new categories(id, name, type, price, status);
        DataStore.categoriesList.add(newCat);

        alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Message");
        alert.setHeaderText(null);
        alert.setContentText("Successfully Added!");
        alert.showAndWait();

        availableFDShowData();
        availableFDClear();
    }

    public void availableFDUpdate() {
        String id     = availableFD_productID.getText().trim();
        String name   = availableFD_productName.getText().trim();
        String type   = availableFD_productType.getValue();
        String priceS = availableFD_productPrice.getText().trim();
        String status = availableFD_productStatus.getValue();

        Alert alert;
        if (id.isEmpty() || name.isEmpty() || type == null || priceS.isEmpty() || status == null) {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceS);
        } catch (NumberFormatException e) {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Price must be a valid number");
            alert.showAndWait();
            return;
        }

        categories found = null;
        for (categories c : DataStore.categoriesList) {
            if (c.getProductId().equals(id)) {
                found = c;
                break;
            }
        }
        if (found == null) {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("No such Product ID to update");
            alert.showAndWait();
            return;
        }

        alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Message");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to UPDATE Product ID: " + id + "?");
        Optional<ButtonType> option = alert.showAndWait();
        if (!option.get().equals(ButtonType.OK)) {
            return;
        }

        found.setName(name);
        found.setType(type);
        found.setPrice(price);
        found.setStatus(status);

        alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Message");
        alert.setHeaderText(null);
        alert.setContentText("Successfully Updated!");
        alert.showAndWait();

        availableFDShowData();
        availableFDClear();
    }

    public void availableFDDelete() {
        String id   = availableFD_productID.getText().trim();
        String name = availableFD_productName.getText().trim();
        String type = availableFD_productType.getValue();
        String price= availableFD_productPrice.getText().trim();
        String status = availableFD_productStatus.getValue();

        Alert alert;
        if (id.isEmpty() || name.isEmpty() || type == null || price.isEmpty() || status == null) {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
            return;
        }

        categories toRemove = null;
        for (categories c : DataStore.categoriesList) {
            if (c.getProductId().equals(id)) {
                toRemove = c;
                break;
            }
        }
        if (toRemove == null) {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("No such Product ID to delete");
            alert.showAndWait();
            return;
        }

        alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Message");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to DELETE Product ID: " + id + "?");
        Optional<ButtonType> option = alert.showAndWait();
        if (!option.get().equals(ButtonType.OK)) {
            return;
        }

        DataStore.categoriesList.remove(toRemove);
        alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Message");
        alert.setHeaderText(null);
        alert.setContentText("Successfully Deleted!");
        alert.showAndWait();

        availableFDShowData();
        availableFDClear();
    }

    public void availableFDClear() {
        availableFD_productID.clear();
        availableFD_productName.clear();
        availableFD_productType.getSelectionModel().clearSelection();
        availableFD_productPrice.clear();
        availableFD_productStatus.getSelectionModel().clearSelection();
    }

    public ObservableList<categories> availableFDListData() {
        return DataStore.categoriesList;
    }

    public void availableFDSearch() { }
    public void availableFDShowData() { availableFD_tableView.refresh(); }


    public void availableFDSelect() {
        categories catData = availableFD_tableView.getSelectionModel().getSelectedItem();
        if (catData == null) return;
        availableFD_productID.setText(catData.getProductId());
        availableFD_productName.setText(catData.getName());
        availableFD_productPrice.setText(String.valueOf(catData.getPrice()));
        availableFD_productType.setValue(catData.getType());
        availableFD_productStatus.setValue(catData.getStatus());
    }

    // Fill the “Type” ComboBox with simple values
    private final String[] categoriesArr = {"Meals", "Drinks"};
    public void availableFDType() {
        ObservableList<String> listData = FXCollections.observableArrayList(categoriesArr);
        availableFD_productType.setItems(listData);
    }

    private final String[] statusArr = {"Available", "Not Available"};
    public void availableFDStatus() {
        ObservableList<String> listStatus = FXCollections.observableArrayList(statusArr);
        availableFD_productStatus.setItems(listStatus);
    }

    // ──────────────────────────────────────────────────────────────────────────────
    // 3) “Order” Handlers: manipulate DataStore.orderList
    // ──────────────────────────────────────────────────────────────────────────────

    public void orderAdd() {
        String selId   = order_productID.getValue();
        String selName = order_productName.getValue();
        if (selId == null || selName == null) return;

        categories foundCat = null;
        for (categories c : DataStore.categoriesList) {
            if (c.getProductId().equals(selId)) {
                foundCat = c;
                break;
            }
        }
        if (foundCat == null) return;

        String orderType  = foundCat.getType();
        double orderPrice = foundCat.getPrice();
        int quantity      = qty;
        if (quantity <= 0) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Quantity must be > 0");
            alert.showAndWait();
            return;
        }

        double totalPrice = orderPrice * quantity;
        product newItem = new product(
            DataStore.nextOrderItemId++,
            selId,
            selName,
            orderType,
            totalPrice,
            quantity
        );
        DataStore.orderList.add(newItem);

        orderDisplayTotal();
        orderDisplayData();
    }

    public void orderPay() {
        // 1) Tính tổng
        orderTotal();
        if (balance < 0) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Insufficient funds!");
            alert.showAndWait();
            return;
        }

        // 2) Xác nhận thanh toán
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation Message");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to pay $" + String.format("%.2f", totalP) + "?");
        Optional<ButtonType> option = confirm.showAndWait();
        if (!option.isPresent() || !option.get().equals(ButtonType.OK)) {
            return;
        }

        // 3) In hoá đơn (receipt)
        orderReceipt();

        // 4) Lưu vào historyList (dùng để vẽ chart “theo ngày”)
        LocalDate today = LocalDate.now();
        DataStore.historyList.add(new Transaction(today, totalP));

        // 5) Clear cart sau khi in xong
        DataStore.orderList.clear();
        order_total.setText("$0.00");
        order_balance.setText("$0.00");
        order_amount.clear();
        orderDisplayData();

        // 6) Cập nhật lại Dashboard (label + chart)
        dashboardNC();
        dashboardTI();
        dashboardTIncome();
        dashboardNOCCChart();
        dashboardICC();

        // 7) Tự động chuyển về tab Dashboard để nhìn thấy chart
        dashboard_btn.fire();

        // 8) Thông báo thanh toán thành công
        Alert info = new Alert(AlertType.INFORMATION);
        info.setTitle("Information Message");
        info.setHeaderText(null);
        info.setContentText("Payment successful!");
        info.showAndWait();
    }

    public void orderTotal() {
        totalP = 0;
        for (product p : DataStore.orderList) {
            totalP += p.getPrice();
        }
    }

    public void orderAmount(ActionEvent event) {
        orderTotal();
        String amtS = order_amount.getText().trim();
        Alert alert;
        if (amtS.isEmpty()) {
            alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please type the amount!");
            alert.showAndWait();
            return;
        }
        try {
            amount = Double.parseDouble(amtS);
        } catch (NumberFormatException e) {
            alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Invalid amount!");
            alert.showAndWait();
            return;
        }
        balance = amount - totalP;
        if (balance < 0) {
            order_amount.clear();
            return;
        }
        order_balance.setText("$" + String.format("%.2f", balance));
    }

    public void orderDisplayTotal() {
        orderTotal();
        order_total.setText("$" + String.format("%.2f", totalP));
    }

    public ObservableList<product> orderListData() {
        return DataStore.orderList;
    }

    public void orderDisplayData() {
        orderData = orderListData();
        order_col_productID.setCellValueFactory(new PropertyValueFactory<>("productId"));
        order_col_productName.setCellValueFactory(new PropertyValueFactory<>("name"));
        order_col_tyoe.setCellValueFactory(new PropertyValueFactory<>("type"));
        order_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        order_col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        order_tableView.setItems(orderData);
    }

    public void orderReceipt() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Receipt");
        alert.setHeaderText("Receipt (in-memory)");

        // Tính lại tổng
        orderTotal();

        StringBuilder sb = new StringBuilder();
        sb.append("Items:\n");
        for (product p : DataStore.orderList) {
            sb.append(String.format("%s x%d = $%.2f\n",
                p.getName(), p.getQuantity(), p.getPrice()));
        }
        sb.append("\nTotal: $" + String.format("%.2f", totalP));
        sb.append("\nPaid: $" + String.format("%.2f", amount));
        sb.append("\nBalance: $" + String.format("%.2f", balance));

        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    public void orderRemove() {
        if (selectedOrderItemId == 0) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please select an item first");
            alert.showAndWait();
            return;
        }
        product toRemove = null;
        for (product p : DataStore.orderList) {
            if (p.getId() == selectedOrderItemId) {
                toRemove = p;
                break;
            }
        }
        if (toRemove == null) return;

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Message");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to remove Item ID: " + selectedOrderItemId + "?");
        Optional<ButtonType> option = alert.showAndWait();
        if (!option.get().equals(ButtonType.OK)) {
            return;
        }

        DataStore.orderList.remove(toRemove);
        selectedOrderItemId = 0;
        orderDisplayData();
        orderDisplayTotal();
        order_amount.clear();
        order_balance.setText("$0.00");

        dashboardNC();
        dashboardTI();
        dashboardTIncome();
        dashboardNOCCChart();
        dashboardICC();
    }

    public void orderSelectData() {
        product prod = order_tableView.getSelectionModel().getSelectedItem();
        if (prod != null) {
            selectedOrderItemId = prod.getId();
        }
    }

    public void orderProductId() {
        ObservableList<String> listData = FXCollections.observableArrayList();
        for (categories c : DataStore.categoriesList) {
            if (c.getStatus().equalsIgnoreCase("Available")) {
                listData.add(c.getProductId());
            }
        }
        order_productID.setItems(listData);
        order_productName.getItems().clear();
    }

    public void orderProductName() {
        String selId = order_productID.getValue();
        if (selId == null) return;
        ObservableList<String> listData = FXCollections.observableArrayList();
        for (categories c : DataStore.categoriesList) {
            if (c.getProductId().equals(selId)) {
                listData.add(c.getName());
                break;
            }
        }
        order_productName.setItems(listData);
    }

    public void orderSpinner() {
        spinner = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50, 0);
        order_quantity.setValueFactory(spinner);
    }

    public void orderQuantity() {
        qty = order_quantity.getValue();
    }

    // ──────────────────────────────────────────────────────────────────────────────
    // 4) SWITCH VIEWS (Dashboard / AvailableFD / Order)
    // ──────────────────────────────────────────────────────────────────────────────
    public void switchForm(ActionEvent event) {
        if (event.getSource() == dashboard_btn) {
            dashboard_form.setVisible(true); availableFD_form.setVisible(false); order_form.setVisible(false);
            styleActive(dashboard_btn);
            dashboardNC(); dashboardTI(); dashboardTIncome();
            dashboardNOCCChart(); dashboardICC();
        } else if (event.getSource() == avaialbeFD_btn) {
            dashboard_form.setVisible(false); availableFD_form.setVisible(true); order_form.setVisible(false);
            styleActive(avaialbeFD_btn);
            // no showData or search
        } else if (event.getSource() == order_btn) {
            dashboard_form.setVisible(false); availableFD_form.setVisible(false); order_form.setVisible(true);
            styleActive(order_btn);
            orderProductId(); orderProductName(); orderSpinner(); orderDisplayData(); orderDisplayTotal();
        }
    }



    public void logout(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Message");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");
        Optional<ButtonType> option = alert.showAndWait();
        if (!option.get().equals(ButtonType.OK)) {
            return;
        }

        logout.getScene().getWindow().hide();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root);

            // Dragging support
            root.setOnMousePressed((MouseEvent e) -> {
                x = e.getSceneX();
                y = e.getSceneY();
            });
            root.setOnMouseDragged((MouseEvent e) -> {
                stage.setX(e.getScreenX() - x);
                stage.setY(e.getScreenY() - y);
                stage.setOpacity(.8f);
            });
            root.setOnMouseReleased((MouseEvent e) -> {
                stage.setOpacity(1);
            });

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayUsername() {
        String user = data.username;
        if (user == null) user = "Guest";
        user = user.substring(0, 1).toUpperCase() + user.substring(1);
        username.setText(user);
    }

    public void close(ActionEvent event) {
        System.exit(0);
    }

    public void minimize(ActionEvent event) {
        Stage stage = (Stage) main_form.getScene().getWindow();
        stage.setIconified(true);
    }
    
    private void styleActive(Button btn) {
        dashboard_btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #000;");
        avaialbeFD_btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #000;");
        order_btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #000;");
        btn.setStyle("-fx-background-color: #3796a7; -fx-text-fill: #fff;");
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Dashboard
        dashboardNC(); dashboardTI(); dashboardTIncome();
        dashboardNOCCChart(); dashboardICC(); displayUsername();

        // AvailableFD bind + filter
        availableFDList = DataStore.categoriesList;
        filteredList = new FilteredList<>(availableFDList, p -> true);
        sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(availableFD_tableView.comparatorProperty());
        availableFD_tableView.setItems(sortedList);
        availableFD_col_productID.setCellValueFactory(new PropertyValueFactory<>("productId"));
        availableFD_col_productName.setCellValueFactory(new PropertyValueFactory<>("name"));
        availableFD_col_type.setCellValueFactory(new PropertyValueFactory<>("type"));
        availableFD_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        availableFD_col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        availableFD_search.textProperty().addListener((obs, oldV, newV) -> {
            String key = (newV == null ? "" : newV.trim().toLowerCase());
            filteredList.setPredicate(cat -> {
                if (key.isEmpty()) return true;
                return cat.getProductId().toLowerCase().contains(key)
                    || cat.getName().toLowerCase().contains(key);
            });
        });
        availableFDStatus(); availableFDType();

        // Order init
        orderProductId(); orderProductName(); orderSpinner(); orderDisplayData(); orderDisplayTotal();
        if (currentDateLabel != null) currentDateLabel.setText("");
    }

}
