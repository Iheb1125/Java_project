import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private InventoryManager inventoryManager;
    private List<Transaction> transactions;
    private SalesReportGenerator salesReportGenerator;
    private List<User> users;

    public Main() {
        this.inventoryManager = new InventoryManager();
        this.transactions = new ArrayList<>();
        this.salesReportGenerator = new SalesReportGenerator(transactions);
        this.users = new ArrayList<>();
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void addProduct(Product product) {
        inventoryManager.addProduct(product);
    }

    public void updateProduct(Product product) {
        inventoryManager.updateProduct(product);
    }

    public void removeProduct(int productId) {
        inventoryManager.removeProduct(productId);
    }

    public void recordTransaction(Transaction transaction) {
        transactions.add(transaction);
        updateProductQuantity(transaction.getProduct(), transaction.getQuantity(), transaction.getType());
    }

    private void updateProductQuantity(Product product, int quantity, TransactionType type) {
        int currentQuantity = product.getQuantityInStock();
        if (type == TransactionType.SALE) {
            product.setQuantityInStock(currentQuantity - quantity);
        } else {
            product.setQuantityInStock(currentQuantity + quantity);
        }
    }

    public void displayInventory() {
        List<Product> products = inventoryManager.getAllProducts();
        System.out.println("Inventory:");
        for (Product product : products) {
            System.out.println("Product ID: " + product.getProductId());
            System.out.println("Product Name: " + product.getProductName());
            System.out.println("Quantity in Stock: " + product.getQuantityInStock());
            System.out.println("Price: " + product.getPrice());
            System.out.println("Category: " + product.getCategory());
            System.out.println("------------------------------");
        }
    }

    public void generateSalesReport() {
        salesReportGenerator.generateSalesReport();
    }

    public boolean authenticateUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public void saveInventoryToFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            List<Product> products = inventoryManager.getAllProducts();
            for (Product product : products) {
                writer.write(
                        product.getProductId() + ","
                                + product.getProductName() + ","
                                + product.getQuantityInStock() + ","
                                + product.getPrice() + ","
                                + product.getCategory() + "\n"
                );
            }
            System.out.println("Inventory saved to file: " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving inventory to file: " + e.getMessage());
        }
    }

    public void loadInventoryFromFile(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                String[] parts = line.split(",");
                int productId = Integer.parseInt(parts[0]);
                String productName = parts[1];
                int quantityInStock = Integer.parseInt(parts[2]);
                double price = Double.parseDouble(parts[3]);
                String category = parts[4];
                Product product = new Product(productName, quantityInStock, price, category);
                inventoryManager.addProduct(product);
            }
            System.out.println("Inventory loaded from file: " + filePath);
        } catch (IOException e) {
            System.err.println("Error loading inventory from file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Main ims = new Main();
        Scanner scanner = new Scanner(System.in);

        // Example: Adding a user
        User adminUser = new User("admin", "admin123", UserRole.ADMIN);
        User adminUser1 = new User("Iheb", "javajava", UserRole.ADMIN);
        User adminUser2 = new User("Dorra", "poyo", UserRole.ADMIN);
        User adminUser3 = new User("Tbs", "2023", UserRole.ADMIN);
        ims.addUser(adminUser);
        ims.addUser(adminUser1);
        ims.addUser(adminUser2);
        ims.addUser(adminUser3);

        // Example: Authenticating a user
        System.out.println("Enter username:");
        String enteredUsername = scanner.nextLine();
        System.out.println("Enter password:");
        String enteredPassword = scanner.nextLine();

        if (ims.authenticateUser(enteredUsername, enteredPassword)) {
            System.out.println("Authentication successful.");

            // Continue with other operations, e.g., adding products, recording transactions
            Product laptop = new Product("Laptop", 10, 350, "Electronics");
            Product phone = new Product("Iphone", 10, 800, "Electronics");
            Product headphones = new Product("Airpods", 10, 290, "Electronics");
            ims.addProduct(laptop);
            ims.addProduct(phone);
            ims.addProduct(headphones);

            Transaction saleTransaction = new Transaction(laptop, 2, LocalDate.now(), TransactionType.SALE);
            Transaction saleTransaction1 = new Transaction(phone, 8, LocalDate.now(), TransactionType.SALE);
            Transaction saleTransaction2 = new Transaction(headphones, 9, LocalDate.now(), TransactionType.SALE);
            ims.recordTransaction(saleTransaction);
            ims.recordTransaction(saleTransaction1);
            ims.recordTransaction(saleTransaction2);

            // Display inventory and generate sales report
            ims.displayInventory();
            ims.generateSalesReport();

            // Save and load inventory from a file
            ims.saveInventoryToFile("inventory.txt");
            ims.loadInventoryFromFile("inventory.txt");
        } else {
            System.out.println("Authentication failed. Exiting program.");
        }
    }
}

class Product {
    private static int nextProductId = 1;

    private int productId;
    private String productName;
    private int quantityInStock;
    private double price;
    private String category;

    public Product(String productName, int quantityInStock, double price, String category) {
        this.productId = nextProductId++;
        this.productName = productName;
        this.quantityInStock = quantityInStock;
        this.price = price;
        this.category = category;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

class InventoryManager {
    private List<Product> products;

    public InventoryManager() {
        this.products = new ArrayList<>();
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void updateProduct(Product product) {
        Product existingProduct = getProductById(product.getProductId());
        if (existingProduct != null) {
            existingProduct.setProductName(product.getProductName());
            existingProduct.setQuantityInStock(product.getQuantityInStock());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setCategory(product.getCategory());
        }
    }

    public void removeProduct(int productId) {
        products.removeIf(product -> product.getProductId() == productId);
    }

    public Product getProductById(int productId) {
        return products.stream()
                .filter(product -> product.getProductId() == productId)
                .findFirst()
                .orElse(null);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }
    public Product getProductByName(String productName) {
        return products.stream()
                .filter(product -> product.getProductName().equals(productName))
                .findFirst()
                .orElse(null);
    }
}

class Transaction {
    private static int nextTransactionId = 1;

    private int transactionId;
    private Product product;
    private int quantity;
    private LocalDate date;
    private TransactionType type;

    public Transaction(Product product, int quantity, LocalDate date, TransactionType type) {
        this.transactionId = nextTransactionId++;
        this.product = product;
        this.quantity = quantity;
        this.date = date;
        this.type = type;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getDate() {
        return date;
    }

    public TransactionType getType() {
        return type;
    }
}

enum TransactionType {
    SALE, PURCHASE
}

class User {
    private static int nextUserId = 1;

    private int userId;
    private String username;
    private String password;
    private UserRole role;

    public User(String username, String password, UserRole role) {
        this.userId = nextUserId++;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }
}

enum UserRole {
    ADMIN, REGULAR
}

class SalesReportGenerator {
    private List<Transaction> transactions;

    public SalesReportGenerator(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void generateSalesReport() {
        System.out.println("Sales Report:");
        for (Transaction transaction : transactions) {
            System.out.println("Transaction ID: " + transaction.getTransactionId());
            System.out.println("Product: " + transaction.getProduct().getProductName());
            System.out.println("Quantity: " + transaction.getQuantity());
            System.out.println("Date: " + transaction.getDate());
            System.out.println("Transaction Type: " + transaction.getType());
            System.out.println("------------------------------");
        }
    }
}
