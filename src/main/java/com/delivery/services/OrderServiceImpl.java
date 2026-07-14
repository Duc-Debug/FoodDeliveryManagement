package com.delivery.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.delivery.exception.*;
import com.delivery.model.*;
import com.delivery.repository.IRepository;
import com.delivery.services.strategy.IDiscountStrategy;
import com.delivery.util.CsvMapper;
import com.delivery.util.FileHandler;

public class OrderServiceImpl implements IOrderService {
    private final IRepository<User, String> userRepository;
    private final IRepository<MenuItem, String> menuRepository;
    private final IRepository<Order, String> orderRepository;

    private final Path orderFilePath;
    private final Path userFilePath;

    public OrderServiceImpl(IRepository<User, String> userRepository, IRepository<MenuItem, String> menuRepository,
            IRepository<Order, String> orderRepository, Path orderPath, Path userPath) {
        this.userRepository = userRepository;
        this.menuRepository = menuRepository;
        this.orderRepository = orderRepository;
        this.orderFilePath = orderPath;
        this.userFilePath = userPath;
        loadDataFromCsv();
    }

    @Override
    public Order checkout(String orderId, String customerId, String merchantId, Cart cart,
            IDiscountStrategy discountStrategy, double shippingFee) {
        if (cart.getItems().isEmpty()) {
            throw new ValidationException("Không thể thanh toán! Giỏ hàng hiện tại đang trống rỗng.", "EMPTY_CART");
        }

        User customer = userRepository.readById(customerId);

        List<OrderItem> orderItems = new ArrayList<>(cart.getItems());
        Order order = new Order(orderId, customer, orderItems, shippingFee, 0.0);

        double discountAmount = 0.0;
        if (discountStrategy != null) {
            discountAmount = discountStrategy.calculateDiscount(order);
        }
        order.setDiscount(discountAmount);

        customer.deduct(order.getTotalPrice());
        merchant.deposit(order.getTotalPrice());

        orderRepository.create(order);
        userRepository.update(customerId, customer);
        userRepository.update(merchantId, merchant);

        syncOrderToFile(orderId);
        syncUsersToFile(customerId);

        cart.clearCart();

        return order;
    }

    @Override
    public void updateStatus(String orderId, OrderState newState) {
        try {
            Order order = orderRepository.readById(orderId);

            order.updateState(newState);

            orderRepository.update(orderId, order);
            syncOrderToFile(orderId);
        } catch (Exception e) {
            throw new ValidationException(" Update fail " + e);
        }
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.readAll();
    }

    @Override
    public Order getOrderById(String OrderId) {
        Order order = orderRepository.readById(OrderId);
        if (order == null)
            throw new NotFoundException("Not found the Order" + OrderId);
        return order;
    }

    @Override
    public void submitReview(String orderId, int rating, String comment) {
        try {
            Order order = orderRepository.readById(orderId);
            if (order == null)
                throw new NotFoundException("Not Found Order: " + orderId);
            order.submitReview(rating, comment);
            orderRepository.update(order.getOrderId(), order);
            userRepository.update(order.getMerchant().getId(), order.getMerchant());

            syncOrderToFile(orderId);
            syncUsersToFile(order.getMerchant().getId());
        } catch (Exception e) {
            throw new ValidationException("Not submit"+e);
        }
    }

    private void syncOrderToFile(String createdOrderId) {
        if (orderFilePath == null) {
            return;
        }
        try {
            FileHandler.writeToCsv(orderFilePath, orderRepository.readAll(), CsvMapper::toCsvRow);
        } catch (IOException ioException) {
            throw new ValidationException("Failed to persist user to file: " + ioException);
        }
    }

    private void syncUsersToFile(String createdUserId) {
        if (userFilePath == null)
            return;
        try {
            FileHandler.writeToCsv(userFilePath, userRepository.readAll(), CsvMapper::toCsvRow);
        } catch (Exception e) {
            throw new ValidationException("Faild to persist user to file: " + userFilePath + " " + e);
        }
    }

    private Order parseOrder(String line) {
        String[] parts = line.split(",");

        String orderId = parts[0];
        Customer customer = (Customer) userRepository.readById(parts[1]);
        Merchant merchant = (Merchant) userRepository.readById(parts[2]);

        String itemsCompressed = parts[3];
        List<OrderItem> orderItems = new ArrayList<>();

        String[] itemTokens = itemsCompressed.split("\\|");
        for (String token : itemTokens) {
            String[] itemParts = token.split(":");
            String menuItemId = itemParts[0];
            int quantity = Integer.parseInt(itemParts[1]);
            MenuItem menuItem = menuRepository.readById(menuItemId);
            orderItems.add(new OrderItem(menuItem, quantity));
        }

        double shippingFee = Double.parseDouble(parts[4]);
        double discount = Double.parseDouble(parts[5]);
        double totalPrice = Double.parseDouble(parts[6]);
        OrderState state = OrderState.valueOf(parts[7]);
        int rating = Integer.parseInt(parts[8]);
        String comment = parts[9].equals("NONE") ? "" : parts[9];

        return new Order(orderId, customer, merchant, orderItems, state, shippingFee, discount, totalPrice, rating,
                comment);
    }

    private void loadDataFromCsv() {
        try {
            List<Order> Orders = FileHandler.readFromTextFile(orderFilePath, this::parseOrder);
            for (Order order : Orders) {
                orderRepository.create(order);
            }
        } catch (Exception e) {
            System.out.println("Not found the Csv");
        }
    }
}