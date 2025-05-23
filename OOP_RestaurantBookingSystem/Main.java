package com.example.restaurant.model;

public class SearchCriteria {
    private String cuisine = "";
    private String location = "";
    private double maxBudget = Double.MAX_VALUE;
    private int minGuests = 1;
    private int maxGuests = Integer.MAX_VALUE;
    private double minRating = 0.0;
    private SortOption sortBy = SortOption.NAME;

    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine != null ? cuisine : ""; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location != null ? location : ""; }

    public double getMaxBudget() { return maxBudget; }
    public void setMaxBudget(double maxBudget) { this.maxBudget = maxBudget; }

    public int getMinGuests() { return minGuests; }
    public void setMinGuests(int minGuests) { this.minGuests = minGuests; }

    public int getMaxGuests() { return maxGuests; }
    public void setMaxGuests(int maxGuests) { this.maxGuests = maxGuests; }

    public double getMinRating() { return minRating; }
    public void setMinRating(double minRating) { this.minRating = minRating; }

    public SortOption getSortBy() { return sortBy; }
    public void setSortBy(SortOption sortBy) { this.sortBy = sortBy != null ? sortBy : SortOption.NAME; }
}


public enum SortOption {
    NAME, BUDGET, RATING
}

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;


public class Restaurant {
    private final int id;
    private final String name;
    private final String cuisine;
    private final String location;
    private final double averageBudget;
    private final double rating;
    private final List<Branch> branches;
    private final Set<String> features = new HashSet<>();
    private final Set<String> searchKeywords = new HashSet<>();

    public Restaurant(int id,
                      String name,
                      String cuisine,
                      String location,
                      double averageBudget,
                      double rating,
                      List<Branch> branches) {
        this.id = id;
        this.name = name;
        this.cuisine = cuisine;
        this.location = location;
        this.averageBudget = averageBudget;
        this.rating = rating;
        this.branches = branches;
        buildSearchKeywords();
    }

    private void buildSearchKeywords() {
        Stream.of(name, cuisine, location)
              .map(String::toLowerCase)
              .forEach(searchKeywords::add);
        for (String word : name.toLowerCase().split("\\s+")) {
            searchKeywords.add(word);
        }
    }

    public boolean matchesKeyword(String keyword) {
        String k = keyword.toLowerCase();
        return searchKeywords.stream().anyMatch(s -> s.contains(k));
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCuisine() { return cuisine; }
    public String getLocation() { return location; }
    public double getAverageBudget() { return averageBudget; }
    public double getRating() { return rating; }
    public List<Branch> getBranches() { return branches; }
    public Set<String> getFeatures() { return features; }
    public Set<String> getSearchKeywords() { return searchKeywords; }

    public void addFeature(String feature) {
        if (feature != null && !feature.isBlank()) {
            features.add(feature);
        }
    }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s (%s) Budget: $%.2f Rating: %.1f",
                             id, name, cuisine, location, averageBudget, rating);
    }
}

package com.example.restaurant.model;

import java.util.List;

public class Branch {
    private final int id;
    private final String address;
    private final List<Table> tables;

    public Branch(int id, String address, List<Table> tables) {
        this.id = id;
        this.address = address;
        this.tables = tables;
    }

    public int getId() { return id; }
    public String getAddress() { return address; }
    public List<Table> getTables() { return tables; }
}

// ======= model/Table.java =======
package com.example.restaurant.model;

/**
 * Represents a single table in a branch.
 */
public class Table {
    private final int id;
    private final int seats;
    private boolean booked;

    public Table(int id, int seats) {
        this.id = id;
        this.seats = seats;
        this.booked = false;
    }

    public int getId() { return id; }
    public int getSeats() { return seats; }
    public boolean isBooked() { return booked; }
    public void setBooked(boolean booked) { this.booked = booked; }
}

package com.example.restaurant.model;

/**
 * Simple customer profile.
 */
public class Customer {
    private final int id;
    private final String name;

    public Customer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
}

// ======= model/Booking.java =======
package com.example.restaurant.model;

import java.time.LocalDateTime;

/**
 * Records a booking of a table by a customer at a given time.
 */
public class Booking {
    private final int id;
    private final Customer customer;
    private final Table table;
    private final LocalDateTime when;

    public Booking(int id, Customer customer, Table table, LocalDateTime when) {
        this.id = id;
        this.customer = customer;
        this.table = table;
        this.when = when;
        this.table.setBooked(true);
    }

    public int getId() { return id; }
    public Customer getCustomer() { return customer; }
    public Table getTable() { return table; }
    public LocalDateTime getWhen() { return when; }

    @Override
    public String toString() {
        return String.format("Booking #%d: %s at %s on %s",
                             id, customer.getName(), table.getId(), when);
    }
}

// ======= index/SearchIndex.java =======
package com.example.restaurant.index;

import com.example.restaurant.model.Restaurant;
import com.example.restaurant.model.Branch;
import com.example.restaurant.model.Table;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory inverted indices for fast lookup by various attributes.
 */
public class SearchIndex {
    private final Map<String, Set<Integer>> cuisineIndex = new HashMap<>();
    private final Map<String, Set<Integer>> locationIndex = new HashMap<>();
    private final Map<String, Set<Integer>> budgetIndex = new HashMap<>();
    private final Map<String, Set<Integer>> capacityIndex = new HashMap<>();
    private final Map<String, Set<Integer>> keywordIndex = new HashMap<>();

    public void indexRestaurant(Restaurant r) {
        int id = r.getId();
        indexField(cuisineIndex, r.getCuisine(), id);
        indexField(locationIndex, r.getLocation(), id);

        String budgetRange = categorizeBudget(r.getAverageBudget());
        indexField(budgetIndex, budgetRange, id);

        int maxCapacity = r.getBranches().stream()
                              .flatMap(b -> b.getTables().stream())
                              .mapToInt(Table::getSeats)
                              .max()
                              .orElse(0);
        indexField(capacityIndex, categorizeCapacity(maxCapacity), id);

        r.getSearchKeywords().forEach(k -> indexField(keywordIndex, k, id));
    }

    private void indexField(Map<String, Set<Integer>> index, String key, int id) {
        index.computeIfAbsent(key.toLowerCase(), k -> new HashSet<>()).add(id);
    }

    private String categorizeBudget(double b) {
        if (b <= 15) return "budget";
        if (b <= 30) return "mid-range";
        return "expensive";
    }

    private String categorizeCapacity(int c) {
        if (c <= 2) return "small";
        if (c <= 6) return "medium";
        return "large";
    }

    public Set<Integer> searchByCuisine(String cuisine) {
        return cuisineIndex.getOrDefault(cuisine.toLowerCase(), Set.of());
    }

    public Set<Integer> searchByLocation(String location) {
        return locationIndex.getOrDefault(location.toLowerCase(), Set.of());
    }

    public Set<Integer> searchByKeyword(String keyword) {
        String k = keyword.toLowerCase();
        return keywordIndex.entrySet().stream()
                .filter(e -> e.getKey().contains(k))
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toSet());
    }

    public Set<String> getAllCuisines() {
        return cuisineIndex.keySet();
    }

    public Set<String> getAllLocations() {
        return locationIndex.keySet();
    }
}

// ======= service/SystemManager.java =======
package com.example.restaurant.service;

import com.example.restaurant.index.SearchIndex;
import com.example.restaurant.model.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Core business logic: loading data, searching, booking, and statistics.
 */
public class SystemManager {
    private final Map<Integer, Restaurant> restaurants = new HashMap<>();
    private final Map<Integer, Customer> customers = new HashMap<>();
    private final List<Booking> bookings = new ArrayList<>();
    private final SearchIndex index = new SearchIndex();
    private int nextBookingId = 1;

    public SystemManager() {
        loadSampleData();
    }

    private void loadSampleData() {
        // define branches, tables, restaurants...
        // addRestaurant(...);
        // add some customers
        customers.put(1, new Customer(1, "Alice"));
        customers.put(2, new Customer(2, "Bob"));
    }

    public void addRestaurant(Restaurant r) {
        restaurants.put(r.getId(), r);
        index.indexRestaurant(r);
    }

    public List<Restaurant> search(SearchCriteria c) {
        Set<Integer> ids = new HashSet<>(restaurants.keySet());
        if (!c.getCuisine().isBlank()) {
            ids.retainAll(index.searchByCuisine(c.getCuisine()));
        }
        if (!c.getLocation().isBlank()) {
            ids.retainAll(index.searchByLocation(c.getLocation()));
        }
        return ids.stream()
                  .map(restaurants::get)
                  .filter(r -> r.getAverageBudget() <= c.getMaxBudget())
                  .filter(r -> r.getRating() >= c.getMinRating())
                  .filter(r -> hasTable(r, c.getMinGuests(), c.getMaxGuests()))
                  .sorted(getComparator(c.getSortBy()))
                  .collect(Collectors.toList());
    }

    private Comparator<Restaurant> getComparator(SortOption o) {
        return switch (o) {
            case BUDGET -> Comparator.comparing(Restaurant::getAverageBudget);
            case RATING -> Comparator.comparing(Restaurant::getRating).reversed();
            default -> Comparator.comparing(Restaurant::getName);
        };
    }

    private boolean hasTable(Restaurant r, int min, int max) {
        return r.getBranches().stream()
                .flatMap(b -> b.getTables().stream())
                .anyMatch(t -> !t.isBooked() && t.getSeats() >= min && t.getSeats() <= max);
    }

    public Booking bookBestTable(int customerId, int restaurantId, int guests, LocalDateTime when) {
        Restaurant r = restaurants.get(restaurantId);
        Customer cust = customers.getOrDefault(customerId, new Customer(customerId, "Guest"));
        if (r == null) return null;
        Table best = r.getBranches().stream()
                .flatMap(b -> b.getTables().stream())
                .filter(t -> !t.isBooked() && t.getSeats() >= guests)
                .min(Comparator.comparingInt(t -> t.getSeats() - guests))
                .orElse(null);
        if (best == null) return null;
        Booking b = new Booking(nextBookingId++, cust, best, when);
        bookings.add(b);
        return b;
    }

    // ... other methods: searchByKeyword, stats, customers, etc.

    public Collection<Restaurant> getAllRestaurants() { return restaurants.values(); }
    public Map<String, Long> cuisineStats() {
        return restaurants.values().stream()
                .collect(Collectors.groupingBy(Restaurant::getCuisine, Collectors.counting()));
    }
    public Map<String, Double> avgRatingByCuisine() {
        return restaurants.values().stream()
                .collect(Collectors.groupingBy(Restaurant::getCuisine,
                         Collectors.averagingDouble(Restaurant::getRating)));
    }
    public List<Booking> getBookings() { return bookings; }
}

// ======= ui/Main.java =======
package com.example.restaurant.ui;

import com.example.restaurant.service.SystemManager;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookingUI(new SystemManager()));
    }
}

// ======= ui/BookingUI.java =======
package com.example.restaurant.ui;

import com.example.restaurant.model.*;
import com.example.restaurant.service.SystemManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Swing-based UI for searching and booking restaurants.
 */
public class BookingUI extends JFrame {
    // Fields omitted for brevity...
    private final SystemManager mgr;
    public BookingUI(SystemManager mgr) {
        super("Restaurant Booking");
        this.mgr = mgr;
        // build UI components, event handlers, etc.
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setVisible(true);
    }

    private void onSearch() { /* ... */ }
    private void onBook() { /* ... */ }
    private void showStats() { /* ... */ }
}
