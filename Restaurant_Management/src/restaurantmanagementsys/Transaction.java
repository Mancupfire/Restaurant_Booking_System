package restaurantmanagementsys;

import java.time.LocalDate;

/**
 * Transaction đóng gói 2 thông tin:
 *  - date: LocalDate khi user nhấn “Pay”
 *  - total: tổng tiền của giao dịch đó
 */
public class Transaction {
    private LocalDate date;
    private double total;

    public Transaction(LocalDate date, double total) {
        this.date = date;
        this.total = total;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getTotal() {
        return total;
    }
}
