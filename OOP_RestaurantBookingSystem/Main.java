public class SearchCriteria {
    private String cuisine;
    private String location;
    private double maxBudget;
    private int minGuests;
    private int maxGuests;
    private double minRating;
    private String sortBy; // "budget", "rating", "name"
    
    public SearchCriteria() {
        this.cuisine = "";
        this.location = "";
        this.maxBudget = Double.MAX_VALUE;
        this.minGuests = 1;
        this.maxGuests = Integer.MAX_VALUE;
        this.minRating = 0.0;
        this.sortBy = "name";
    }
    
    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public double getMaxBudget() { return maxBudget; }
    public void setMaxBudget(double maxBudget) { this.maxBudget = maxBudget; }
    
    public int getMinGuests() { return minGuests; }
    public void setMinGuests(int minGuests) { this.minGuests = minGuests; }
    
    public int getMaxGuests() { return maxGuests; }
    public void setMaxGuests(int maxGuests) { this.maxGuests = maxGuests; }
    
    public double getMinRating() { return minRating; }
    public void setMinRating(double minRating) { this.minRating = minRating; }
    
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
}

import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class Restaurant {
    private int id;
    private String name;
    private String cuisine;
    private String location;
    private double averageBudget;
    private double rating;
    private List<Branch> branches;
    private Set<String> features; // WiFi, Parking, etc.
    private Set<String> searchKeywords; // For full-text search

    public Restaurant(int id, String name, String cuisine, String location, 
                     double averageBudget, double rating, List<Branch> branches) {
        this.id = id;
        this.name = name;
        this.cuisine = cuisine;
        this.location = location;
        this.averageBudget = averageBudget;
        this.rating = rating;
        this.branches = branches;
        this.features = new HashSet<>();
        this.searchKeywords = new HashSet<>();
        buildSearchKeywords();
    }
    
    private void buildSearchKeywords() {
        searchKeywords.add(name.toLowerCase());
        searchKeywords.add(cuisine.toLowerCase());
        searchKeywords.add(location.toLowerCase());
        // Add individual words for better matching
        for (String word : name.toLowerCase().split("\\s+")) {
            searchKeywords.add(word);
        }
    }
    
    public boolean matchesKeyword(String keyword) {
        return searchKeywords.stream()
               .anyMatch(k -> k.contains(keyword.toLowerCase()));
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
    
    public void addFeature(String feature) { features.add(feature); }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s (%s) Budget: $%.2f Rating: %.1f", 
                           id, name, cuisine, location, averageBudget, rating);
    }
}

// SearchIndex.java - Efficient indexing for fast searches
package com.example.restaurant;

import java.util.*;
import java.util.stream.Collectors;

public class SearchIndex {
    private Map<String, Set<Integer>> cuisineIndex = new HashMap<>();
    private Map<String, Set<Integer>> locationIndex = new HashMap<>();
    private Map<String, Set<Integer>> budgetIndex = new HashMap<>();
    private Map<String, Set<Integer>> capacityIndex = new HashMap<>();
    private Map<String, Set<Integer>> keywordIndex = new HashMap<>();
    
    public void indexRestaurant(Restaurant restaurant) {
        int id = restaurant.getId();
        
        // Index by cuisine
        cuisineIndex.computeIfAbsent(restaurant.getCuisine().toLowerCase(), 
                                   k -> new HashSet<>()).add(id);
        
        // Index by location
        locationIndex.computeIfAbsent(restaurant.getLocation().toLowerCase(), 
                                    k -> new HashSet<>()).add(id);
        
        // Index by budget range
        String budgetRange = getBudgetRange(restaurant.getAverageBudget());
        budgetIndex.computeIfAbsent(budgetRange, k -> new HashSet<>()).add(id);
        
        // Index by capacity
        int maxCapacity = restaurant.getBranches().stream()
                                  .flatMapToInt(b -> b.getTables().stream().mapToInt(Table::getSeats))
                                  .max().orElse(0);
        String capacityRange = getCapacityRange(maxCapacity);
        capacityIndex.computeIfAbsent(capacityRange, k -> new HashSet<>()).add(id);
        
        // Index keywords
        for (String keyword : restaurant.getSearchKeywords()) {
            keywordIndex.computeIfAbsent(keyword, k -> new HashSet<>()).add(id);
        }
    }
    
    private String getBudgetRange(double budget) {
        if (budget <= 15) return "budget";
        else if (budget <= 30) return "mid-range";
        else return "expensive";
    }
    
    private String getCapacityRange(int capacity) {
        if (capacity <= 2) return "small";
        else if (capacity <= 6) return "medium";
        else return "large";
    }
    
    public Set<Integer> searchByCuisine(String cuisine) {
        return cuisineIndex.getOrDefault(cuisine.toLowerCase(), new HashSet<>());
    }
    
    public Set<Integer> searchByLocation(String location) {
        return locationIndex.getOrDefault(location.toLowerCase(), new HashSet<>());
    }
    
    public Set<Integer> searchByKeyword(String keyword) {
        return keywordIndex.entrySet().stream()
               .filter(entry -> entry.getKey().contains(keyword.toLowerCase()))
               .flatMap(entry -> entry.getValue().stream())
               .collect(Collectors.toSet());
    }
    
    public Set<String> getAllCuisines() { return cuisineIndex.keySet(); }
    public Set<String> getAllLocations() { return locationIndex.keySet(); }
}

// EnhancedSystemManager.java
package com.example.restaurant;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SystemManager {
    private Map<Integer, Restaurant> restaurants = new HashMap<>();
    private Map<Integer, Customer> customers = new HashMap<>();
    private List<Booking> bookings = new ArrayList<>();
    private SearchIndex searchIndex = new SearchIndex();
    private int bookingSeq = 1;

    public SystemManager() {
        loadSampleData();
    }

    private void loadSampleData() {
        // Sample tables with different capacities
        List<Table> tables1 = Arrays.asList(
            new Table(1, 2), new Table(2, 4), new Table(3, 6), new Table(4, 8)
        );
        List<Table> tables2 = Arrays.asList(
            new Table(5, 2), new Table(6, 4), new Table(7, 6)
        );
        List<Table> tables3 = Arrays.asList(
            new Table(8, 2), new Table(9, 4), new Table(10, 10)
        );
        
        Branch b1 = new Branch(1, "123 Main St", tables1);
        Branch b2 = new Branch(2, "456 Elm St", tables2);
        Branch b3 = new Branch(3, "789 Oak Ave", tables3);
        
        Restaurant r1 = new Restaurant(1, "Pasta Palace", "Italian", "Downtown", 25.0, 4.5, Arrays.asList(b1));
        r1.addFeature("WiFi");
        r1.addFeature("Parking");
        
        Restaurant r2 = new Restaurant(2, "Curry Corner", "Indian", "Uptown", 20.0, 4.2, Arrays.asList(b2));
        r2.addFeature("WiFi");
        r2.addFeature("Vegetarian");
        
        Restaurant r3 = new Restaurant(3, "Sushi Zen", "Japanese", "Downtown", 35.0, 4.8, Arrays.asList(b3));
        r3.addFeature("WiFi");
        r3.addFeature("Parking");
        r3.addFeature("Private Dining");
        
        Restaurant r4 = new Restaurant(4, "Burger Barn", "American", "Suburb", 15.0, 3.9, Arrays.asList(b1));
        r4.addFeature("Drive-through");
        r4.addFeature("Kids Menu");
        
        // Add to system and index
        addRestaurant(r1);
        addRestaurant(r2);
        addRestaurant(r3);
        addRestaurant(r4);

        customers.put(1, new Customer(1, "Alice"));
        customers.put(2, new Customer(2, "Bob"));
        customers.put(3, new Customer(3, "Charlie"));
    }
    
    private void addRestaurant(Restaurant restaurant) {
        restaurants.put(restaurant.getId(), restaurant);
        searchIndex.indexRestaurant(restaurant);
    }

    // Enhanced search with multiple criteria
    public List<Restaurant> search(SearchCriteria criteria) {
        Set<Integer> candidateIds = new HashSet<>(restaurants.keySet());
        
        // Filter by cuisine if specified
        if (!criteria.getCuisine().isEmpty()) {
            Set<Integer> cuisineMatches = searchIndex.searchByCuisine(criteria.getCuisine());
            candidateIds.retainAll(cuisineMatches);
        }
        
        // Filter by location if specified
        if (!criteria.getLocation().isEmpty()) {
            Set<Integer> locationMatches = searchIndex.searchByLocation(criteria.getLocation());
            candidateIds.retainAll(locationMatches);
        }
        
        // Convert to restaurant objects and apply remaining filters
        List<Restaurant> results = candidateIds.stream()
            .map(restaurants::get)
            .filter(r -> r.getAverageBudget() <= criteria.getMaxBudget())
            .filter(r -> r.getRating() >= criteria.getMinRating())
            .filter(r -> hasAvailableTable(r, criteria.getMinGuests(), criteria.getMaxGuests()))
            .collect(Collectors.toList());
        
        // Sort results
        sortResults(results, criteria.getSortBy());
        
        return results;
    }
    
    // Keyword-based search
    public List<Restaurant> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(restaurants.values());
        }
        
        Set<Integer> matches = searchIndex.searchByKeyword(keyword.trim());
        return matches.stream()
               .map(restaurants::get)
               .sorted(Comparator.comparing(Restaurant::getName))
               .collect(Collectors.toList());
    }
    
    // Advanced filtering methods
    public List<Restaurant> getRestaurantsByBudgetRange(double minBudget, double maxBudget) {
        return restaurants.values().stream()
               .filter(r -> r.getAverageBudget() >= minBudget && r.getAverageBudget() <= maxBudget)
               .sorted(Comparator.comparing(Restaurant::getAverageBudget))
               .collect(Collectors.toList());
    }
    
    public List<Restaurant> getTopRatedRestaurants(int limit) {
        return restaurants.values().stream()
               .sorted(Comparator.comparing(Restaurant::getRating).reversed())
               .limit(limit)
               .collect(Collectors.toList());
    }
    
    public Map<String, Long> getCuisineStatistics() {
        return restaurants.values().stream()
               .collect(Collectors.groupingBy(Restaurant::getCuisine, Collectors.counting()));
    }
    
    public Map<String, Double> getAverageRatingByCuisine() {
        return restaurants.values().stream()
               .collect(Collectors.groupingBy(Restaurant::getCuisine,
                       Collectors.averagingDouble(Restaurant::getRating)));
    }
    
    private boolean hasAvailableTable(Restaurant restaurant, int minGuests, int maxGuests) {
        return restaurant.getBranches().stream()
               .flatMap(b -> b.getTables().stream())
               .anyMatch(t -> !t.isBooked() && t.getSeats() >= minGuests && t.getSeats() <= maxGuests);
    }
    
    private void sortResults(List<Restaurant> results, String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "budget":
                results.sort(Comparator.comparing(Restaurant::getAverageBudget));
                break;
            case "rating":
                results.sort(Comparator.comparing(Restaurant::getRating).reversed());
                break;
            case "name":
            default:
                results.sort(Comparator.comparing(Restaurant::getName));
                break;
        }
    }

    // Original booking method (kept for compatibility)
    public Booking book(int customerId, int restaurantId, int branchId, int tableId, LocalDateTime when) {
        Customer c = customers.getOrDefault(customerId, new Customer(customerId, "Guest"));
        Restaurant r = restaurants.get(restaurantId);
        if (r == null) return null;
        
        Branch br = r.getBranches().stream()
                    .filter(b -> b.getId() == branchId)
                    .findFirst().orElse(null);
        if (br == null) return null;
        
        Table t = br.getTables().stream()
                  .filter(tbl -> tbl.getId() == tableId && !tbl.isBooked())
                  .findFirst().orElse(null);
        if (t == null) return null;
        
        Booking bk = new Booking(bookingSeq++, c, t, when);
        bookings.add(bk);
        return bk;
    }
    
    // Enhanced booking with automatic table selection
    public Booking bookBestAvailableTable(int customerId, int restaurantId, int guests, LocalDateTime when) {
        Customer c = customers.getOrDefault(customerId, new Customer(customerId, "Guest"));
        Restaurant r = restaurants.get(restaurantId);
        if (r == null) return null;
        
        // Find best available table (closest to party size)
        Table bestTable = null;
        Branch bestBranch = null;
        int bestTableDiff = Integer.MAX_VALUE;
        
        for (Branch branch : r.getBranches()) {
            for (Table table : branch.getTables()) {
                if (!table.isBooked() && table.getSeats() >= guests) {
                    int diff = table.getSeats() - guests;
                    if (diff < bestTableDiff) {
                        bestTableDiff = diff;
                        bestTable = table;
                        bestBranch = branch;
                    }
                }
            }
        }
        
        if (bestTable != null) {
            Booking bk = new Booking(bookingSeq++, c, bestTable, when);
            bookings.add(bk);
            return bk;
        }
        
        return null;
    }

    // Getters for UI
    public List<Booking> getBookings() { return bookings; }
    public Collection<Restaurant> getAllRestaurants() { return restaurants.values(); }
    public Set<String> getAllCuisines() { return searchIndex.getAllCuisines(); }
    public Set<String> getAllLocations() { return searchIndex.getAllLocations(); }
    
    // Customer management
    public void addCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
    }
    
    public Customer getCustomer(int id) { return customers.get(id); }
}

package com.example.restaurant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EnhancedBookingUI(new EnhancedSystemManager()));
    }
}

class BookingUI extends JFrame {
    private EnhancedSystemManager manager;
    private JTextField tfKeyword, tfMaxBudget, tfMinRating, tfGuests;
    private JComboBox<String> cbCuisine, cbLocation, cbSortBy;
    private DefaultListModel<Restaurant> listModel;
    private JList<Restaurant> list;
    private JButton btnSearch, btnKeywordSearch, btnBook, btnStats;
    private JTextArea taStats;

    public BookingUI(EnhancedSystemManager mgr) {
        super("Enhanced Restaurant Booking System");
        this.manager = mgr;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Search panel
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        // Results list
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        // Statistics panel
        taStats = new JTextArea(10, 30);
        taStats.setEditable(false);
        taStats.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane statsScroll = new JScrollPane(taStats);
        
        // Split pane for results and stats
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, statsScroll);
        splitPane.setDividerLocation(400);
        add(splitPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        btnSearch = new JButton("Advanced Search");
        btnKeywordSearch = new JButton("Keyword Search");
        btnBook = new JButton("Book Selected");
        btnStats = new JButton("Show Statistics");
        
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnKeywordSearch);
        buttonPanel.add(btnBook);
        buttonPanel.add(btnStats);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event listeners
        btnSearch.addActionListener(e -> onAdvancedSearch());
        btnKeywordSearch.addActionListener(e -> onKeywordSearch());
        btnBook.addActionListener(e -> onBook());
        btnStats.addActionListener(e -> showStatistics());

        // Load initial data
        loadAllRestaurants();
        showStatistics();
        
        setVisible(true);
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 1: Keyword search
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Keyword:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        tfKeyword = new JTextField(20);
        panel.add(tfKeyword, gbc);
        
        // Row 2: Filters
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Cuisine:"), gbc);
        gbc.gridx = 1;
        cbCuisine = new JComboBox<>();
        cbCuisine.addItem("Any");
        manager.getAllCuisines().forEach(cbCuisine::addItem);
        panel.add(cbCuisine, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 3;
        cbLocation = new JComboBox<>();
        cbLocation.addItem("Any");
        manager.getAllLocations().forEach(cbLocation::addItem);
        panel.add(cbLocation, gbc);
        
        // Row 3: Numeric filters
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Max Budget:"), gbc);
        gbc.gridx = 1;
        tfMaxBudget = new JTextField("100", 8);
        panel.add(tfMaxBudget, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Min Rating:"), gbc);
        gbc.gridx = 3;
        tfMinRating = new JTextField("0", 8);
        panel.add(tfMinRating, gbc);
        
        // Row 4: Guests and sorting
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Guests:"), gbc);
        gbc.gridx = 1;
        tfGuests = new JTextField("2", 8);
        panel.add(tfGuests, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Sort by:"), gbc);
        gbc.gridx = 3;
        cbSortBy = new JComboBox<>(new String[]{"name", "budget", "rating"});
        panel.add(cbSortBy, gbc);
        
        return panel;
    }
    
    private void loadAllRestaurants() {
        listModel.clear();
        manager.getAllRestaurants().forEach(listModel::addElement);
    }
    
    private void onAdvancedSearch() {
        try {
            SearchCriteria criteria = new SearchCriteria();
            
            String cuisine = (String) cbCuisine.getSelectedItem();
            if (!"Any".equals(cuisine)) {
                criteria.setCuisine(cuisine);
            }
            
            String location = (String) cbLocation.getSelectedItem();
            if (!"Any".equals(location)) {
                criteria.setLocation(location);
            }
            
            criteria.setMaxBudget(Double.parseDouble(tfMaxBudget.getText().trim()));
            criteria.setMinRating(Double.parseDouble(tfMinRating.getText().trim()));
            criteria.setMinGuests(Integer.parseInt(tfGuests.getText().trim()));
            criteria.setSortBy((String) cbSortBy.getSelectedItem());
            
            List<Restaurant> results = manager.search(criteria);
            listModel.clear();
            results.forEach(listModel::addElement);
            
            taStats.setText(String.format("Search Results: %d restaurants found", results.size()));
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for budget, rating, and guests.");
        }
    }
    
    private void onKeywordSearch() {
        String keyword = tfKeyword.getText().trim();
        List<Restaurant> results = manager.searchByKeyword(keyword);
        listModel.clear();
        results.forEach(listModel::addElement);
        
        taStats.setText(String.format("Keyword Search Results: %d restaurants found for '%s'", 
                                    results.size(), keyword));
    }
    
    private void onBook() {
        Restaurant selected = list.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a restaurant.");
            return;
        }
        
        try {
            int guests = Integer.parseInt(tfGuests.getText().trim());
            Booking booking = manager.bookBestAvailableTable(1, selected.getId(), guests, LocalDateTime.now());
            
            if (booking != null) {
                JOptionPane.showMessageDialog(this, "Booking successful!\n" + booking);
                // Refresh the list to show updated availability
                onAdvancedSearch();
            } else {
                JOptionPane.showMessageDialog(this, "No available tables for " + guests + " guests.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number of guests.");
        }
    }
    
    private void showStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== RESTAURANT STATISTICS ===\n\n");
        
        stats.append("Total Restaurants: ").append(manager.getAllRestaurants().size()).append("\n\n");
        
        stats.append("Cuisine Distribution:\n");
        manager.getCuisineStatistics().forEach((cuisine, count) -> 
            stats.append(String.format("  %s: %d restaurants\n", cuisine, count)));
        
        stats.append("\nAverage Rating by Cuisine:\n");
        manager.getAverageRatingByCuisine().forEach((cuisine, rating) ->
            stats.append(String.format("  %s: %.2f stars\n", cuisine, rating)));
        
        stats.append("\nTop Rated Restaurants:\n");
        manager.getTopRatedRestaurants(5).forEach(r ->
            stats.append(String.format("  %s: %.1f stars\n", r.getName(), r.getRating())));
        
        stats.append(String.format("\nTotal Bookings: %d\n", manager.getBookings().size()));
        
        taStats.setText(stats.toString());
    }
}
