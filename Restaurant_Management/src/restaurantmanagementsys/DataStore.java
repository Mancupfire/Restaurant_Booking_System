package restaurantmanagementsys;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * DataStore giữ các danh sách “in‐memory”:
 *  - categoriesList: danh sách món ăn/đồ uống hiện có
 *  - orderList: danh sách các item đang được order (chưa thanh toán)
 *  - historyList: danh sách các lần thanh toán (mỗi lần lưu ngày + tổng tiền)
 *  - nextOrderItemId: auto‐increment ID cho mỗi item trong orderList
 */
public class DataStore {
    // Danh sách món ăn/đồ uống (Available Foods/Drinks)
    public static ObservableList<categories> categoriesList = FXCollections.observableArrayList();

    // Danh sách các item đang được đặt (chưa thanh toán)
    public static ObservableList<product> orderList = FXCollections.observableArrayList();

    // Số tự động tăng cho mỗi item khi thêm vào orderList
    public static int nextOrderItemId = 1;

    // ★ Danh sách lịch sử thanh toán (mỗi Transaction gồm date + tổng tiền)
    public static ObservableList<Transaction> historyList = FXCollections.observableArrayList();
}
