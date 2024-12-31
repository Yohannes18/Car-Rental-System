package com.example;
import java.sql.*;
import java.util.Scanner;
import java.util.UUID;

class CarRentalSystem {
    private final String DB_URL = "jdbc:sqlite:src/main/resources/car_rental.db";
    private final String ADMIN_PASSWORD = "admin123"; // Admin password
    private Connection conn;
    private String loggedInCustomerId = null;

    public CarRentalSystem() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            initializeDatabase();
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    private void initializeDatabase() {
        try (Statement stmt = conn.createStatement()) {
            String createCarsTable = "CREATE TABLE IF NOT EXISTS Cars (" +
                    "carId TEXT PRIMARY KEY," +
                    "brand TEXT NOT NULL," +
                    "model TEXT NOT NULL," +
                    "basePricePerDay REAL NOT NULL," +
                    "isAvailable INTEGER NOT NULL" +
                    ");";

            String createCustomersTable = "CREATE TABLE IF NOT EXISTS Customers (" +
                    "customerId TEXT PRIMARY KEY," +
                    "name TEXT NOT NULL" +
                    ");";

            stmt.execute(createCarsTable);
            stmt.execute(createCustomersTable);
        } catch (SQLException e) {
            System.out.println("Failed to initialize database: " + e.getMessage());
        }
    }

    private String generateCustomerId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public void registerCustomer(String name) {
        String customerId = generateCustomerId();
        String sql = "INSERT INTO Customers (customerId, name) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
            System.out.println("Customer registered successfully. Your Customer ID is: " + customerId);
            loggedInCustomerId = customerId;
        } catch (SQLException e) {
            System.out.println("Failed to register customer: " + e.getMessage());
        }
    }

    public void loginCustomer(String customerId) {
        String sql = "SELECT * FROM Customers WHERE customerId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerId.trim());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                loggedInCustomerId = customerId;
                System.out.println("Welcome, " + rs.getString("name") + "!");
            } else {
                System.out.println("Customer ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to login customer: " + e.getMessage());
        }
    }

    public void listCars() {
        String sql = "SELECT * FROM Cars";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nAvailable Cars:");
            System.out.println("ID\tBrand\tModel\tPrice/Day\tAvailable");
            while (rs.next()) {
                String carId = rs.getString("carId");
                String brand = rs.getString("brand");
                String model = rs.getString("model");
                double price = rs.getDouble("basePricePerDay");
                boolean isAvailable = rs.getInt("isAvailable") == 1;
                System.out.printf("%s\t%s\t%s\t%.2f\t%s%n", carId, brand, model, price, isAvailable ? "Yes" : "No");
            }
        } catch (SQLException e) {
            System.out.println("Failed to list cars: " + e.getMessage());
        }
    }

    public void adminMenu(Scanner scanner) {
        System.out.print("Enter admin password: ");
        String password = scanner.nextLine().trim();

        if (!ADMIN_PASSWORD.equals(password)) {
            System.out.println("Incorrect password. Access denied.");
            return;
        }

        while (true) {
            System.out.println("\n===== Admin Menu =====");
            System.out.println("1. Add Car");
            System.out.println("2. Remove Car");
            System.out.println("3. Update Car Details");
            System.out.println("4. List Cars");
            System.out.println("5. List Customers");
            System.out.println("6. Logout");

            try {
                System.out.print("Enter your choice: ");
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter Car ID: ");
                        String carId = scanner.nextLine().trim();
                        System.out.print("Enter Brand: ");
                        String brand = scanner.nextLine().trim();
                        System.out.print("Enter Model: ");
                        String model = scanner.nextLine().trim();
                        System.out.print("Enter Base Price Per Day: ");
                        double price = Double.parseDouble(scanner.nextLine().trim());
                        addCar(carId, brand, model, price);
                    }
                    case 2 -> {
                        System.out.print("Enter Car ID: ");
                        String carId = scanner.nextLine().trim();
                        removeCar(carId);
                    }
                    case 3 -> {
                        System.out.print("Enter Car ID: ");
                        String carId = scanner.nextLine().trim();
                        System.out.print("Enter New Brand: ");
                        String brand = scanner.nextLine().trim();
                        System.out.print("Enter New Model: ");
                        String model = scanner.nextLine().trim();
                        System.out.print("Enter New Base Price Per Day: ");
                        double price = Double.parseDouble(scanner.nextLine().trim());
                        updateCarDetails(carId, brand, model, price);
                    }
                    case 4 -> listCars();
                    case 5 -> listCustomers();
                    case 6 -> {
                        System.out.println("Logged out successfully.");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public void customerMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n===== Customer Menu =====");
            System.out.println("1. Rent a Car");
            System.out.println("2. Return a Car");
            System.out.println("3. List Cars");
            System.out.println("4. Logout");

            try {
                System.out.print("Enter your choice: ");
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter Car ID: ");
                        String carId = scanner.nextLine().trim();
                        System.out.print("Enter Number of Days: ");
                        int days = Integer.parseInt(scanner.nextLine().trim());
                        rentCar(carId, days);
                    }
                    case 2 -> {
                        System.out.print("Enter Car ID: ");
                        String carId = scanner.nextLine().trim();
                        returnCar(carId);
                    }
                    case 3 -> listCars();
                    case 4 -> {
                        loggedInCustomerId = null;
                        System.out.println("Logged out successfully.");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public void mainMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Welcome to Car Rental System =====");
            System.out.println("1. Customer");
            System.out.println("2. Admin");
            System.out.println("3. Exit");

            try {
                System.out.print("Enter your choice: ");
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1 -> {
                        System.out.println("\n===== Welcome to Customer ===== ");
                        System.out.println("1. Login");
                        System.out.println("2. Signup");
                        System.out.println("3. Exit");
                        System.out.print("Enter your choice: ");
                        int customerChoice = Integer.parseInt(scanner.nextLine().trim());
                    switch (customerChoice) {
                        case 1 -> {
                            System.out.print("Enter Customer ID: ");
                            String customerId = scanner.nextLine().trim();
                            loginCustomer(customerId);
                            if (loggedInCustomerId != null) customerMenu(scanner);
                        }
                        case 2 -> {
                            System.out.print("Enter Name: ");
                            String name = scanner.nextLine().trim();
                            registerCustomer(name);
                            customerMenu(scanner);
                        }
                        case 3 -> {
                            return;
                        }
                        default -> System.out.println("Invalid choice. Try again.");
                    }
                    }
                    case 2 -> adminMenu(scanner);
                    case 3 -> {
                        System.out.println("Goodbye!");
                        scanner.close();
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void addCar(String carId, String brand, String model, double basePricePerDay) {
        String sql = "INSERT INTO Cars (carId, brand, model, basePricePerDay, isAvailable) VALUES (?, ?, ?, ?, 1)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, carId);
            pstmt.setString(2, brand);
            pstmt.setString(3, model);
            pstmt.setDouble(4, basePricePerDay);
            pstmt.executeUpdate();
            System.out.println("Car added successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to add car: " + e.getMessage());
        }
    }

    private void removeCar(String carId) {
        String sql = "DELETE FROM Cars WHERE carId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, carId.trim());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Car removed successfully.");
            } else {
                System.out.println("Car ID not found!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to remove car: " + e.getMessage());
        }
    }

    private void updateCarDetails(String carId, String newBrand, String newModel, double newBasePricePerDay) {
        String sql = "UPDATE Cars SET brand = ?, model = ?, basePricePerDay = ? WHERE carId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newBrand);
            pstmt.setString(2, newModel);
            pstmt.setDouble(3, newBasePricePerDay);
            pstmt.setString(4, carId.trim());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Car details updated successfully.");
            } else {
                System.out.println("Car ID not found!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to update car details: " + e.getMessage());
        }
    }

    private void rentCar(String carId, int days) {
        if (loggedInCustomerId == null) {
            System.out.println("Please log in first.");
            return;
        }

        String sql = "SELECT * FROM Cars WHERE carId = ? AND isAvailable = 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, carId.trim());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double price = rs.getDouble("basePricePerDay") * days;
                System.out.printf("Car rented successfully! Total cost: $%.2f%n", price);

                String updateSQL = "UPDATE Cars SET isAvailable = 0 WHERE carId = ?";
                try (PreparedStatement updatePstmt = conn.prepareStatement(updateSQL)) {
                    updatePstmt.setString(1, carId.trim());
                    updatePstmt.executeUpdate();
                }
            } else {
                System.out.println("Car not found or not available!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to rent car: " + e.getMessage());
        }
    }

    private void returnCar(String carId) {
        if (loggedInCustomerId == null) {
            System.out.println("Please log in first.");
            return;
        }

        String sql = "UPDATE Cars SET isAvailable = 1 WHERE carId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, carId.trim());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Car returned successfully!");
            } else {
                System.out.println("Car ID not found!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to return car: " + e.getMessage());
        }
    }

    private void listCustomers() {
        String sql = "SELECT * FROM Customers";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nRegistered Customers:");
            System.out.println("ID\tName");
            while (rs.next()) {
                String customerId = rs.getString("customerId");
                String name = rs.getString("name");
                System.out.printf("%s\t%s%n", customerId, name);
            }
        } catch (SQLException e) {
            System.out.println("Failed to list customers: " + e.getMessage());
        }
    }
}

public class App {
    public static void main(String[] args) {
        CarRentalSystem rentalSystem = new CarRentalSystem();
        rentalSystem.mainMenu();
    }
}
