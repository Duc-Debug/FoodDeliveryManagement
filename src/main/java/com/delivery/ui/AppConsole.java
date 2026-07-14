package com.delivery.ui;

import java.util.List;
import java.util.Scanner;

import com.delivery.exception.NotFoundException;
import com.delivery.exception.ValidationException;
import com.delivery.model.Cart;
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
            System.out.println("   HỆ THỐNG QUẢN LÝ GIAO ĐỒ ĂN  ");
            System.out.println("=========================================");
            System.out.println("1. Vào phân hệ KHÁCH HÀNG (Customer)");
            System.out.println("2. Vào phân hệ Admin cửa hàng");
            System.out.println("3. Tạo tài khoản mới.");
            System.out.println("0. Thoát hệ thống");
            System.out.println("-----------------------------------------");

            int choice = readIntInput();

            switch (choice) {
                case 1:
                    handleCustomer();
                    break;
                case 2:
                    // TODO: handleAdmin
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
                            user.getDetails();
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
                System.out.println("5. Lịch sử đơn hàng");
                System.out.println("6. Đăng xuất (Quay lại màn hình chính)");
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
                        start();
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
            System.out.println("Chưa có món nào");
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
            System.out.printf("Tạm tính tiền hàng: %,.0f VNĐ | Phí giao hàng: %,.0f VNĐ\n", subTotal, shippingFee);

            String confirm = readStringInput(
                    "Bạn có muốn chốt đơn thanh toán không? (Y/Yes or select any key to continue): ");
            if (!"Y".equalsIgnoreCase(confirm.toUpperCase()) || !"YES".equalsIgnoreCase(confirm.toUpperCase())) {
                System.out.println("Đã hủy quy trình thanh toán. Giỏ hàng được giữ nguyên.");
                return;
            }

            String orderId = "ORD" + (System.currentTimeMillis() % 100000);

            // TODO: handle to apply discount
            var promotion = new PercentageDiscount(0.1, 10000);
            Order order = orderService.checkout(orderId, currentUser.getId(), currentUser.getCart(), promotion,
                    shippingFee);

            System.out.println("\n=========================================");
            System.out.println("   🎉 ĐẶT ĐỒ ĂN THÀNH CÔNG (CHECKOUT OK)  ");
            System.out.println("=========================================");
            System.out.println("Mã đơn hàng của bạn: " + order.getOrderId());
            System.out.printf("Số tiền được giảm giá:  -%,.0f VNĐ\n", order.getDiscount());
            System.out.printf("TỔNG TIỀN ĐÃ KHẤU TRỪ VÍ: %,.0f VNĐ\n", order.getTotalPrice());
            System.out.println("Trạng thái hiện tại:     " + order.getState().name());
            System.out.println("=========================================");

        } catch (ValidationException e) {
            System.out.println("\n[GIAO DỊCH THẤT BẠI] Lỗi xử lý từ hệ thống: " + e.getMessage());
        }
    }

    private void reviewOrder() {
        System.out.println("---ĐÁNH GIÁ ĐƠN HÀNG ---");
        String orderId = readStringInput("Enter Order id input to review: ");
        try {
            Order order = orderService.getOrderById(orderId);
            if (order.getState() != OrderState.DELIVERED) {
                System.out.println("[LỖI] Đơn hàng chưa được giao thành công, không thể để lại đánh giá!");
                return;
            }
            int rating = readNonIntInput("Chấm điểm uy tín (Nhập từ 1 đến 5 Sao): ");
            while (rating > 5 || rating <= 0) {
                System.out.println("Nhập số sao sai!");
                rating = readNonIntInput("Nhập điểm(1--5): ");
            }
            String comment = readStringInput("Viết nội dung bình luận nhận xét: ");
            orderService.submitReview(orderId, rating, comment);
            System.out.println("[OK] Cảm ơn ý kiến đóng góp của bạn! Điểm uy tín của quán đã được cập nhật.");
        } catch (NotFoundException e) {
            System.out.println("[LỖI] Không tìm thấy mã đơn hàng này trên hệ thống!");
        } catch (ValidationException e) {
            System.out.println("[LỖI] Đánh giá thất bại: " + e.getMessage());
        }
    }

    private void viewOrderHistory() {
        System.out.println("\n--- LỊCH SỬ ĐƠN HÀNG CỦA BẠN ---");
        var allOrders = orderService.getAllOrders();
        boolean hasOrder = false;

        for (Order order : allOrders) {
            // Lọc đúng mã ID của khách hiện tại
            if (order.getCustomer().getId().equals(currentUser.getId())) {
                hasOrder = true;
                System.out.printf("Đơn: %-8s | Tổng thanh toán: %, -10.0f VNĐ | Trạng thái: %-10s | Đánh giá: %d Sao\n",
                        order.getOrderId(), order.getTotalPrice(), order.getState().name(), order.getRating());
            }
        }
        if (!hasOrder) {
            System.out.println("Bạn chưa thực hiện đơn đặt hàng nào trên hệ thống.");
        }
    }

    // ============================================
    private void handleCreateUser() {
        try {
            String id = readStringInput("User input: ");
            String name = readStringInput("User name: ");
            String phoneNumber = readStringInput("User Phone number: ");
            String address = readStringInput("User Address");
            User user = new User(id, name, phoneNumber, 100000, address);
            userService.registerUser(user);
            System.out.println("Register new User Successfully");
        } catch (Exception exception) {
            System.out.println("ERROR: " + exception.getMessage());
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
