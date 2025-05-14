/*
Project: RestaurantBookingSystem
Package: com.example.restaurant

Structure:
 src/com/example/restaurant/
   - Restaurant.java
   - Branch.java
   - Table.java
   - Customer.java
   - Booking.java
   - SystemManager.java
   - Main.java
*/

// Restaurant.java
package com.example.restaurant;

import java.util.List;

public class Restaurant {
    private int id;
    private String name;
    private String cuisine;
    private String location;
    private double averageBudget;
    private List<Branch> branches;

    public Restaurant(int id, String name, String cuisine, String location, double averageBudget, List<Branch> branches) {
        this.id = id;
        this.name = name;
        this.cuisine = cuisine;
        this.location = location;
        this.averageBudget = averageBudget;
        this.branches = branches;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCuisine() { return cuisine; }
    public String getLocation() { return location; }
    public double getAverageBudget() { return averageBudget; }
    public List<Branch> getBranches() { return branches; }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s (%s) AvgBudget: $%.2f", id, name, cuisine, location, averageBudget);
    }
}

// Branch.java
package com.example.restaurant;

import java.util.List;

public class Branch {
    private int id;
    private String address;
    private List<Table> tables;

    public Branch(int id, String address, List<Table> tables) {
        this.id = id;
        this.address = address;
        this.tables = tables;
    }

    public int getId() { return id; }
    public String getAddress() { return address; }
    public List<Table> getTables() { return tables; }
}

// Table.java
package com.example.restaurant;

public class Table {
    private int id;
    private int seats;
    private boolean isBooked;

    public Table(int id, int seats) {
        this.id = id;
        this.seats = seats;
        this.isBooked = false;
    }

    public int getId() { return id; }
    public int getSeats() { return seats; }
    public boolean isBooked() { return isBooked; }
    public void setBooked(boolean booked) { isBooked = booked; }

    @Override
    public String toString() {
        return String.format("Table[%d] Seats:%d %s", id, seats, isBooked ? "(Booked)" : "");
    }
}

// Customer.java
package com.example.restaurant;

public class Customer {
    private int id;
    private String name;

    public Customer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
}

// Booking.java
package com.example.restaurant;

import java.time.LocalDateTime;

public class Booking {
    private int id;
    private Customer customer;
    private Table table;
    private LocalDateTime dateTime;

    public Booking(int id, Customer customer, Table table, LocalDateTime dateTime) {
        this.id = id;
        this.customer = customer;
        this.table = table;
        this.dateTime = dateTime;
        this.table.setBooked(true);
    }

    @Override
    public String toString() {
        return String.format("Booking[%d] %s -> %s at %s", id, customer.getName(), table, dateTime);
    }
}

// SystemManager.java
package com.example.restaurant;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SystemManager {
    private Map<Integer, Restaurant> restaurants = new HashMap<>();
    private Map<Integer, Customer> customers = new HashMap<>();
    private List<Booking> bookings = new ArrayList<>();
    private int bookingSeq = 1;

    public SystemManager() {
        loadSampleData();
    }

    private void loadSampleData() {
        // Sample tables
        List<Table> tables1 = Arrays.asList(new Table(1, 2), new Table(2, 4), new Table(3, 6));
        List<Table> tables2 = Arrays.asList(new Table(4, 2), new Table(5, 4));
        Branch b1 = new Branch(1, "123 Main St", tables1);
        Branch b2 = new Branch(2, "456 Elm St", tables2);
        Restaurant r1 = new Restaurant(1, "Pasta Palace", "Italian", "Downtown", 25.0, Arrays.asList(b1));
        Restaurant r2 = new Restaurant(2, "Curry Corner", "Indian", "Uptown", 20.0, Arrays.asList(b2));
        restaurants.put(r1.getId(), r1);
        restaurants.put(r2.getId(), r2);

        // Sample customer
        customers.put(1, new Customer(1, "Alice"));
    }

    public List<Restaurant> search(String cuisine, String location, double maxBudget, int guests) {
        return restaurants.values().stream()
            .filter(r -> (cuisine.isEmpty() || r.getCuisine().equalsIgnoreCase(cuisine)))
            .filter(r -> (location.isEmpty() || r.getLocation().equalsIgnoreCase(location)))
            .filter(r -> r.getAverageBudget() <= maxBudget)
            .filter(r -> r.getBranches().stream()
                            .flatMap(b -> b.getTables().stream())
                            .anyMatch(t -> !t.isBooked() && t.getSeats() >= guests)
            )
            .collect(Collectors.toList());
    }

    public Booking book(int customerId, int restaurantId, int branchId, int tableId, LocalDateTime when) {
        Customer c = customers.getOrDefault(customerId, new Customer(customerId, "Guest"));
        Restaurant r = restaurants.get(restaurantId);
        Branch br = r.getBranches().stream().filter(b -> b.getId() == branchId).findFirst().orElse(null);
        Table t = br.getTables().stream().filter(tbl -> tbl.getId() == tableId).findFirst().orElse(null);
        Booking bk = new Booking(bookingSeq++, c, t, when);
        bookings.add(bk);
        return bk;
    }

    public List<Booking> getBookings() {
        return bookings;
    }
}

// Main.java (Swing UI)
package com.example.restaurant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookingUI(new SystemManager()));
    }
}

class BookingUI extends JFrame {
    private SystemManager manager;
    private JTextField tfCuisine, tfLocation, tfBudget, tfGuests;
    private DefaultListModel<Restaurant> listModel;
    private JList<Restaurant> list;
    private JButton btnSearch, btnBook;

    public BookingUI(SystemManager mgr) {
        super("Restaurant Booking System");
        this.manager = mgr;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new GridLayout(2, 4, 5, 5));
        tfCuisine = new JTextField();
        tfLocation = new JTextField();
        tfBudget = new JTextField();
        tfGuests = new JTextField();
        top.add(new JLabel("Cuisine:")); top.add(tfCuisine);
        top.add(new JLabel("Location:")); top.add(tfLocation);
        top.add(new JLabel("Max Budget:")); top.add(tfBudget);
        top.add(new JLabel("Guests:")); top.add(tfGuests);
        add(top, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        btnSearch = new JButton("Search");
        btnBook = new JButton("Book Selected");
        bottom.add(btnSearch);
        bottom.add(btnBook);
        add(bottom, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> onSearch());
        btnBook.addActionListener(e -> onBook());

        setVisible(true);
    }

    private void onSearch() {
        String cuisine = tfCuisine.getText().trim();
        String location = tfLocation.getText().trim();
        double budget = Double.parseDouble(tfBudget.getText().trim());
        int guests = Integer.parseInt(tfGuests.getText().trim());
        List<Restaurant> results = manager.search(cuisine, location, budget, guests);
        listModel.clear();
        results.forEach(listModel::addElement);
    }

    private void onBook() {
        Restaurant sel = list.getSelectedValue();
        if (sel == null) return;
        Branch br = sel.getBranches().get(0); // choose first branch for demo
        Table tbl = br.getTables().stream().filter(t -> !t.isBooked() && t.getSeats() >= Integer.parseInt(tfGuests.getText().trim())).findFirst().orElse(null);
        if (tbl == null) {
            JOptionPane.showMessageDialog(this, "No available tables.");
            return;
        }
        Booking bk = manager.book(1, sel.getId(), br.getId(), tbl.getId(), LocalDateTime.now());
        JOptionPane.showMessageDialog(this, "Booked! " + bk);
    }
}
