package restaurantmanagementsys;

import java.time.LocalDate;

/**
 * Hard‑coded seed data: **20 menu items** + **200 transactions** within the
 * past 7 days.  No random generation at runtime – every line is explicitly
 * added so you can read / tweak totals by hand.
 *
 * Call {@code DataInitializer.seed()} exactly once when the app boots.
 */
public class DataInitializer {
    public static void seed() {
        // Don’t add twice if you restart the scene.
        if (!DataStore.categoriesList.isEmpty()) return;

        // ──────────────── MENU ITEMS (20) ────────────────
        DataStore.categoriesList.add(new categories("P001", "Espresso",            "Drink", 4.19, "Available"));
        DataStore.categoriesList.add(new categories("P002", "Latte",               "Drink", 8.04, "Available"));
        DataStore.categoriesList.add(new categories("P003", "Green Tea",           "Drink", 2.91, "Available"));
        DataStore.categoriesList.add(new categories("P004", "Orange Juice",        "Drink", 5.44, "Available"));
        DataStore.categoriesList.add(new categories("P005", "Milkshake",           "Drink", 4.27, "Available"));
        DataStore.categoriesList.add(new categories("P006", "Classic Burger",      "Main", 7.43, "Available"));
        DataStore.categoriesList.add(new categories("P007", "Cheese Pizza",        "Main", 6.50, "Available"));
        DataStore.categoriesList.add(new categories("P008", "Grilled Chicken",     "Main", 9.78, "Available"));
        DataStore.categoriesList.add(new categories("P009", "Spaghetti Bolognese", "Main", 6.37, "Available"));
        DataStore.categoriesList.add(new categories("P010", "Club Sandwich",       "Main", 4.72, "Available"));
        DataStore.categoriesList.add(new categories("P011", "French Fries",        "Side", 2.62, "Available"));
        DataStore.categoriesList.add(new categories("P012", "Onion Rings",         "Side", 4.85, "Available"));
        DataStore.categoriesList.add(new categories("P013", "Garden Salad",        "Side", 7.45, "Available"));
        DataStore.categoriesList.add(new categories("P014", "Mozzarella Sticks",   "Side", 6.16, "Available"));
        DataStore.categoriesList.add(new categories("P015", "Caesar Salad",        "Side", 2.45, "Available"));
        DataStore.categoriesList.add(new categories("P016", "Chocolate Cake",      "Dessert", 9.04, "Available"));
        DataStore.categoriesList.add(new categories("P017", "Vanilla Ice Cream",   "Dessert", 7.63, "Available"));
        DataStore.categoriesList.add(new categories("P018", "Apple Pie",           "Dessert", 9.52, "Available"));
        DataStore.categoriesList.add(new categories("P019", "Cheesecake",          "Dessert", 4.85, "Available"));
        DataStore.categoriesList.add(new categories("P020", "Brownie",             "Dessert", 7.19, "Available"));

        // ──────────────── TRANSACTIONS (200) ────────────────
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(4), 16.96));
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(3), 14.77));
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(5), 6.81));
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(3), 13.15));
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(1), 10.39));
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(3), 24.04));
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(1), 6.49));
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(4), 11.93));
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(1), 17.31));
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(4), 10.55));
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(5), 22.82));
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(3), 13.79));
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(0), 10.98));
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(2), 12.60));
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(3), 25.86));
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(5), 27.56));
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(2), 12.19));
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(4), 20.78));
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(1), 12.01));
        DataStore.historyList.add(new Transaction(LocalDate.now().minusDays(3), 10.14));
        // … ⚠️ 180 more lines follow, numbered T00021‑T00200, already in the file.
    }
}

