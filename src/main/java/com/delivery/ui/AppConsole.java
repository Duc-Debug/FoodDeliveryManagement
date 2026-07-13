package com.delivery.ui;

import java.util.List;
import java.util.Scanner;

import com.delivery.exception.ValidationException;
import com.delivery.model.Cart;
import com.delivery.model.Customer;
import com.delivery.model.MenuItem;
import com.delivery.model.Merchant;
import com.delivery.model.User;
import com.delivery.services.IMenuService;
import com.delivery.services.IOrderService;
import com.delivery.services.IUserService;

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
            System.out.println("   HỆ THỐNG QUẢN LÝ GIAO ĐỒ ĂN  ");
            System.out.println("=========================================");
            System.out.println("1. Vào phân hệ KHÁCH HÀNG (Customer)");
            System.out.println("2. Vào phân hệ ĐỐI TÁC / CHỦ QUÁN (Merchant)");
            System.out.println("3. Tạo tài khoản mới.");
            System.out.println("0. Thoát hệ thống");
            System.out.println("-----------------------------------------");

            int choice = readIntInput();

            switch (choice) {
                case 1:
                    handleCustomer();
                    break;
                case 2:
                    // MerchantConsole merchantConsole = new MerchantConsole(input, orderService);
                    // merchantConsole.displayMenu();
                    break;
                case 3:
                    handleCreateUser();
                case 0:
                    System.out.println("\n[HỆ THỐNG] Tạm biệt!");
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
                System.out.println("Please login:");
                System.out.println("1. Get all Customer");
                System.out.println("2. Login");
                int input = readIntInput();
                switch (input) {
                    case 1:
                        List<User> users = userService.getAllUser();
                        for (User user : users) {
                            if (user instanceof Customer) {
                                user.getDetails();
                            }
                            continue;
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
                System.out.println("1. Nạp thêm tiền vào ví");
                System.out.println("2. Thêm món ăn vào Giỏ hàng nháp");
                System.out.println("3. Xem giỏ hàng & Tiến hành thanh toán (Checkout)");
                System.out.println("4. Viết đánh giá / Chấm sao đơn hàng");
                System.out.println("5. Đăng xuất (Quay lại màn hình chính)");
                System.out.println("====================================");
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
                        checkoutCart();
                        break;
                    default:
                        break;
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void addFoodToCart() {
        var users = userService.getAllUser();
        for(User user : users){
            if(user instanceof Merchant){
                System.out.println(user.getDetails());
            }
        }
        String merchant = readStringInput("Select merchant");
        System.out.println("======MENU======");
        var allItems = menuService.getAllMenuItem();
        if(allItems.isEmpty()){
            System.out.println("Chưa có món nào");
            return;
        }
        for(MenuItem item: allItems){
            if(item.getMerchantId()!=merchant){
                continue;
            }
            System.out.println(item.getDetailDescription());
        }
        boolean addFood = true;
        Cart cart = new Cart();
        while (addFood) {
           try {
            String endOrder = readStringInput("Complete order(Yes to break)?");
            if(endOrder.toLowerCase()=="yes"||endOrder.toLowerCase()=="y"){
                addFood =false;
                ((Customer)currentUser).setCart(cart);
                continue;
            }
            String itemId = readStringInput("Enter menu item id to add: ");
            MenuItem item = menuService.getMenuItemById(itemId);
            int quantity = readNonIntInput("Enter quantity: ");
            
            cart.addItem(item, quantity);
           } catch (Exception e) {
            System.out.println(e.getMessage());
           }
                        
        }
    }

    private void checkoutCart(){
        try {
            String orderId = readStringInput("Enter order id: ");


        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    // ============================================
    private void handleCreateUser() {
        try {
            User user = readUserFromInput();
            userService.registerUser(user);
            System.out.println("Register new User Successfully");
        } catch (Exception exception) {
            System.out.println("ERROR: " + exception.getMessage());
        }
    }

    private User readUserFromInput() {
        System.out
                .println("Select type user create:\n1.Customer(Khách hàng)\n2.Merchant(Chủ quán/Đối tác)\n0.Quay lại");
        System.out.println("Type: ");
        int typeChoice = readIntInput();

        String id = readStringInput("User input: ");
        String name = readStringInput("User name: ");
        String phoneNumber = readStringInput("User Phone number: ");
        switch (typeChoice) {
            case 1:
                String address = readStringInput("User Address");
                return new Customer(id, name, phoneNumber, 100000, address);
            case 2:
                String storeName = readStringInput("User address: ");
                return new Merchant(id, name, phoneNumber, storeName);
            default:
                throw new IllegalArgumentException("Unsupport this User, Try Again!");
        }
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
            System.out.println(promt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Invalid input! Try again!");
        }
    }

    private int readNonIntInput(String promt) {
        while (true) {
            System.out.println(promt);
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
            System.out.println(promt);
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
}
