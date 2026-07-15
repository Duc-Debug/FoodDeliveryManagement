package com.delivery.ui;

import java.util.List;
import java.util.Scanner;

import com.delivery.exception.NotFoundException;
import com.delivery.exception.ValidationException;
import com.delivery.model.Cart;
import com.delivery.model.Drink;
import com.delivery.model.Food;
import com.delivery.model.MenuItem;
import com.delivery.model.Order;
import com.delivery.model.OrderItem;
import com.delivery.model.OrderState;
import com.delivery.model.User;
import com.delivery.services.IMenuService;
import com.delivery.services.IOrderService;
import com.delivery.services.IUserService;
import com.delivery.services.strategy.PercentageDiscount;

public class AppConsole {
    private final IOrderService orderService;
    private final IMenuService menuService;
    private final Scanner scanner;
    private final IUserService userService;

    private User currentUser;

    public AppConsole(IOrderService orderService, IUserService userService, IMenuService menuService) {
        this.orderService = orderService;
        this.scanner = new Scanner(System.in);
        this.userService = userService;
        this.menuService = menuService;
        currentUser = null;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n=========================================");
            System.out.println("          FOOD DELIVERY MANAGEMENT SYSTEM          ");
            System.out.println("=========================================");
            System.out.println("1. Access CUSTOMER subsystem");
            System.out.println("2. Access Restaurant Admin subsystem");
            System.out.println("3. Create a new account");
            System.out.println("0. Exit system");
            newLine();

            int choice = readIntInput();

            switch (choice) {
                case 1:
                    handleCustomer();
                    break;
                case 2:
                    handleAdmin();
                    break;
                case 3:
                    handleCreateUser();
                    break;
                case 0:
                    System.out.println("\n[SYSTEM] Bye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }
    }

    private void handleCustomer() {
        while (true) {
            try {
                newLine();
                System.out.println("1. Get all Customer");
                System.out.println("2. Login");
                System.out.println("0. Back to MainView");
                System.out.print("Please login: ");
                int input = readIntInput();
                System.out.println("---------------------");
                switch (input) {
                    case 1:
                        List<User> users = userService.getAllUser();
                        for (User user : users) {
                            System.out.println(user.getDetails());
                        }
                        break;
                    case 2:
                        String userId = readStringInput("Enter User id: ");
                        currentUser = userService.getUserById(userId);
                        if (currentUser == null) {
                            System.out.println("Not found user");
                            break;
                        }
                        customerView();
                    case 0:
                        return;
                    default:
                        break;
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

        }
    }

    private void customerView() {
        String userId = currentUser.getId();
        while (true) {
            try {
                System.out.println(currentUser.getDetails());
                System.out.println("1. Deposit money into wallet");
                System.out.println("2. Add food to draft cart");
                System.out.println("3. View cart & Proceed to checkout");
                System.out.println("4. Write a review / Rate an order");
                System.out.println("5. Order history");
                System.out.println("6. Log out (Return to main menu)");
                newLine();
                int input = readIntInput();
                switch (input) {
                    case 1:
                        double amount = readNonDoubleInput("Enter amount money to deposit: ");
                        userService.depositMoney(userId, amount);
                        break;
                    case 2:
                        addFoodToCart();
                        break;
                    case 3:
                        performCheckout();
                        break;
                    case 4:
                        reviewOrder();
                        break;
                    case 5:
                        viewOrderHistory();
                        break;
                    case 6:
                        currentUser = null;
                        return;
                    default:
                        break;
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void addFoodToCart() {
        System.out.println("======MENU======");
        var allItems = menuService.getAllMenuItem();
        if (allItems.isEmpty()) {
            System.out.println("No items added yet");
            newLine();
            return;
        }

        boolean addFood = true;
        Cart cart = new Cart();
        while (addFood) {
            try {
                String endOrder = readStringInput("Complete order(Yes to break)?");
                if (endOrder.toLowerCase() == "yes" || endOrder.toLowerCase() == "y") {
                    addFood = false;
                    currentUser.setCart(cart);
                    continue;
                }
                String itemId = readStringInput("Enter menu item id to add: ");
                MenuItem item = menuService.getMenuItemById(itemId);
                int quantity = readNonIntInput("Enter quantity: ");

                cart.addItem(item, quantity);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            newLine();
        }
    }

    private void performCheckout() {
        try {
            var cart = currentUser.getCart();
            if (cart.getItems().isEmpty()) {
                System.out.println("Cart is empty!");
                return;
            }
            double subTotal = 0;
            for (OrderItem item : cart.getItems()) {
                double itemPrice = item.calculateItemPrice();
                subTotal += itemPrice;
                System.out.printf("+ %s x %d = %,.0f VNĐ\n", item.getMenuItem().getName(), item.getQuantity(),
                        itemPrice);
            }
            double shippingFee = 15000; // Mặc định phí ship hệ thống
            System.out.printf("Subtotal: %,.0f VND | Shipping fee: %,.0f VND\n", subTotal, shippingFee);

            String confirm = readStringInput(
                    "Do you want to confirm and pay? (Y/Yes or press any key to cancel): ");
            if (!"Y".equalsIgnoreCase(confirm.toUpperCase()) || !"YES".equalsIgnoreCase(confirm.toUpperCase())) {
                System.out.println("Checkout process canceled. Your cart remains unchanged.");
                return;
            }

            String orderId = "ORD" + (System.currentTimeMillis() % 100000);

            // TODO: handle to apply discount
            var promotion = new PercentageDiscount(0.1, 10000);
            Order order = orderService.checkout(orderId, currentUser.getId(), currentUser.getCart(), promotion,
                    shippingFee);

            System.out.println("\n=========================================");
            System.out.println("   🎉 ORDER PLACED SUCCESSFULLY (CHECKOUT OK)  ");
            System.out.println("=========================================");
            System.out.println("Your Order ID: " + order.getOrderId());
            System.out.printf("Discount applied: -%,.0f VND\n", order.getDiscount());
            System.out.printf("TOTAL AMOUNT DEDUCTED FROM WALLET: %,.0f VND\n", order.getTotalPrice());
            System.out.println("Current Status: " + order.getState().name());
            newLine();

        } catch (ValidationException e) {
            System.out.println("\n[TRANSACTION FAILED] System processing error: " + e.getLocalizedMessage()); // Hoặc
                                                                                                              // e.getMessage()
        }
    }

    private void reviewOrder() {
        System.out.println("--- RATE & REVIEW ORDER ---");
        String orderId = readStringInput("Enter Order id input to review: ");
        try {
            Order order = orderService.getOrderById(orderId);
            if (order.getState() != OrderState.DELIVERED) {
                System.out.println("[ERROR] Only successfully delivered orders can be reviewed!");
                return;
            }
            int rating = readNonIntInput("Rate this order (Enter 1 to 5 Stars): ");
            while (rating > 5 || rating <= 0) {
                System.out.println("Invalid rating!");
                rating = readNonIntInput("Please enter a rating (1-5): ");
            }
            String comment = readStringInput("Write your comment/review: ");
            orderService.submitReview(orderId, rating, comment);
            System.out.println("[OK] Thank you for your feedback! The restaurant's rating has been updated.");
        } catch (NotFoundException e) {
            System.out.println("[ERROR] This Order ID could not be found in the system!");
        } catch (ValidationException e) {
            System.out.println("[ERROR] Review submission failed: " + e.getMessage());
        }
        newLine();
    }

    private void viewOrderHistory() {
        System.out.println("\n--- YOUR ORDER HISTORY ---");
        var allOrders = orderService.getAllOrders();
        boolean hasOrder = false;

        for (Order order : allOrders) {
            // Filter by the current customer's ID
            if (order.getCustomer().getId().equals(currentUser.getId())) {
                hasOrder = true;
                System.out.printf("Order: %-8s | Total Paid: %, -10.0f VND | Status: %-10s | Rating: %d Stars\n",
                        order.getOrderId(), order.getTotalPrice(), order.getState().name(), order.getRating());
            }
        }
        if (!hasOrder) {
            System.out.println("You have not placed any orders yet.");
        }
        newLine();
    }

    // ================================================
    private void handleAdmin() {
        while (true) {
            try {
                newLine();
                System.out.println("--- ADMIN LOGIN ---");
                String username = readStringInput("Enter Admin Username (or '0' to go back): ");
                if ("0".equals(username)) {
                    return;
                }
                String password = readStringInput("Enter Admin Password: ");

                if (com.delivery.config.StoreConfig.authenticate(username, password)) {
                    System.out.println("Login successful! Welcome Admin.");
                    newLine();
                    adminView();
                    return;
                } else {
                    System.out.println("Invalid Username or Password! Please try again.");
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void adminView() {
        while (true) {
            try {
                newLine();
                System.out.println("===== ADMIN CONTROL PANEL =====");
                System.out.println("1. View Store Revenue (Xem doanh thu)");
                System.out.println("2. Manage Menu ");
                System.out.println("3. Manage Orders ");
                System.out.println("4. Manage Promotions ");
                System.out.println("0. Logout (Đăng xuất)");
                System.out.print("Enter your choice: ");
                int choice = readIntInput();
                newLine();
                switch (choice) {
                    case 1:
                        viewRevenue();
                        break;
                    case 2:
                        manageMenu();
                        break;
                    case 3:
                        manageOrders();
                        break;
                    case 4:
                        // TODO
                        // managePromotions();
                        break;
                    case 0:
                        System.out.println("Logging out Admin account...");
                        return; // Thoát ra ngoài handleAdmin()
                    default:
                        System.out.println("Invalid choice!");
                        break;
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void viewRevenue() {
        newLine();
        System.out.println("===== STORE FINANCIAL REPORT =====");
        System.out.printf("TOTAL REVENUE TILL DATE: %,.0f VNĐ\n", com.delivery.config.StoreConfig.getStoreBalance());
        newLine();
    }

    private void manageMenu() {
        while (true) {
            try {
                newLine();
                System.out.println("--- MANAGE MENU ---");
                System.out.println("1. Show Current Menu");
                System.out.println("2. Add New Food (Đồ ăn)");
                System.out.println("3. Add New Drink (Đồ uống)");
                System.out.println("4. Delete Menu Item");
                System.out.println("0. Back to Panel");
                System.out.print("Enter choice: ");
                int choice = readIntInput();
                System.out.println("---------------------");

                switch (choice) {
                    case 1:
                        currentMenu();
                        break;
                    case 2:
                        addNewFood();
                        break;
                    case 3:
                        addNewDrink();
                        break;
                    case 4:
                        try {
                            String deleteId = readStringInput("Enter MenuItem ID to delete: ");
                            menuService.deleteMenuItem(deleteId);
                            System.out.println("[OK] Item deleted successfully!");
                        } catch (Exception ex) {
                            System.out.println("Delete fail! " + ex.getMessage());
                        }
                        break;
                    case 0:
                        return;
                    default:
                        break;
                }
            } catch (Exception ex) {
                System.out.println("[ERROR] " + ex.getMessage());
            }
        }
    }

    private void currentMenu() {
        List<MenuItem> items = menuService.getAllMenuItem();
        if (items.isEmpty()) {
            System.out.println("Menu is empty!");
        } else {
            for (MenuItem item : items) {
                System.out.println(
                        item.getId() + " | " + item.getName() + " | " + item.getBasePrice() + " VNĐ");
            }
        }
        newLine();
    }

    private void addNewFood() {
        while (true) {
            try {
                String foodId = readStringInput("Enter FoodId(Or Press 0 to exit): ").trim();
                if (foodId.trim().equals("0")) {
                    return;
                }
                String foodName = readStringInput("Enter Food Name: ").trim();
                double foodPrice = readNonDoubleInput("Enter Base Price: ");
                String foodDesc = readStringInput("Enter Description: ");
                String portionSize = readStringInput("Enter Portion Size(e.g. Suat don, Combo): ");
                System.out.println("Is vegetarian?(true/false");
                boolean isVegearian = Boolean.parseBoolean(readStringInput(""));
                MenuItem newFood = new Food(foodId, foodName, foodPrice, foodDesc, portionSize, isVegearian);
                menuService.createMenu(newFood);
                System.out.println("[OK] Food created successfully!");
                newLine();
            } catch (Exception ex) {
                System.out.println("[Failed] Not created Food");
            }
        }
    }

    private void addNewDrink() {
        while (true) {
            try {
                String DrinkId = readStringInput("Enter DrinkId(Or Press 0 to exit): ");
                if (DrinkId.equals(0)) {
                    return;
                }
                String DrinkName = readStringInput("Enter Drink Name: ");
                double DrinkPrice = readNonDoubleInput("Enter Base Price: ");
                String DrinkDesc = readStringInput("Enter Description: ");
                String size = readStringInput("Enter Cup Size (S/M/L): ");
                int surgar = readNonIntInput("Enter Default Sugar level (%):");
                int ice = readNonIntInput("Enter Default Ice level (%): ");
                MenuItem newDrink = new Drink(DrinkId, DrinkName, DrinkPrice, DrinkDesc, size, surgar, ice);
                menuService.createMenu(newDrink);
                System.out.println("[OK] Drink created successfully!");
                newLine();
            } catch (Exception ex) {
                System.out.println("[Failed] Not created Drink");
            }
        }
    }

    private void manageOrders() {
        while (true) {
            try {
                newLine();
                System.out.println("--- MANAGE ORDERS ---");
                System.out.println("1. Show All Orders");
                System.out.println("2. Update Order Status");
                System.out.println("0. Back to Panel");
                System.out.print("Enter choice: ");
                int choice = readIntInput();
                System.out.println("---------------------");

                switch (choice) {
                    case 1:

                        break;
                    case 2:
                        String orderId = readStringInput("Enter Order ID to update: ");
                        System.out.println("Choose new status:");
                        System.out.println("1. PREPARING (Đang chuẩn bị món)");
                        System.out.println("2. DELIVERED (Đã giao hàng thành công)");
                        System.out.println("3. CANCELLED (Hủy đơn hàng)");
                        System.out.print("Choose status (1-3): ");
                        int statusChoice = readIntInput();

                        OrderState newState;
                        if (statusChoice == 1)
                            newState = OrderState.PREPARING;
                        else if (statusChoice == 2)
                            newState = OrderState.DELIVERED;
                        else
                            newState = OrderState.CANCELLED;

                        orderService.updateStatus(orderId, newState);
                        System.out.println("[OK] Order status updated to " + newState.name());
                        break;
                    case 0:
                        return;
                    default:
                        break;
                }
            } catch (Exception ex) {
                System.out.println("[ERROR] " + ex.getMessage());
            }
        }
    }

    private void showAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders in system.");
        } else {
            for (Order o : orders) {
                System.out.printf("ID: %s | Khách: %s | Tổng: %,.0f VNĐ | Trạng thái: %s\n",
                        o.getOrderId(), o.getUser().getName(), o.getTotalPrice(), o.getState().name());
            }
        }
    }

    // ============================================
    private void handleCreateUser() {
        try {
            String id = readStringInput("User id: ");
            String name = readStringInput("User name: ");
            String phoneNumber = readStringInput("User Phone number: ");
            String address = readStringInput("User Address: ");
            User user = new User(id, name, phoneNumber, 100000, address);
            userService.registerUser(user);
            System.out.println("Register new User Successfully");
        } catch (Exception exception) {
            System.out.println("ERROR: " + exception.getMessage());
        }
        newLine();
    }

    // ================================================
    private int readIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private String readStringInput(String promt) {
        while (true) {
            System.out.print(promt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Invalid input! Try again!");
        }
    }

    private int readNonIntInput(String promt) {
        while (true) {
            System.out.print(promt);
            String raw = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(raw);
                if (value < 0) {
                    System.out.println("Value must be >= 0");
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Invalid interger! Try again!");
            }
        }
    }

    private double readNonDoubleInput(String promt) {
        while (true) {
            System.out.print(promt);
            String raw = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(raw);
                if (value < 0) {
                    System.out.println("Value must be >= 0");
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number! Try again!");
            }
        }
    }

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    private void newLine() {
        System.out.println(ANSI_GREEN + "====================================" + ANSI_RESET);
    }
}
