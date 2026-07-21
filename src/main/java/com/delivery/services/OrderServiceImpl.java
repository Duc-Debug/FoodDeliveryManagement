package com.delivery.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.delivery.config.StoreConfig;
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
    public void createOrder(String orderId, String customerId, Cart cart, double shippingFee) {
        if (cart.getItems().isEmpty()) {
            throw new ValidationException("Checkout failed! Your current cart is empty.", "EMPTY_CART");
        }
        try {
            List<OrderItem> orderItems = new ArrayList<>(cart.getItems());
            Order order = new Order(orderId, customerId, orderItems, shippingFee, 0.0);
            orderRepository.create(order);
            syncOrderToFile();
            syncUsersToFile();
        } catch (ValidationException ex) {
            orderRepository.delete(orderId);
            throw new ValidationException(ex.getMessage());
        }
        cart.clearCart();
    }

    @Override
    public Order checkout(Order order, IDiscountStrategy discountStrategy) {
        if (order.isPaid() == true || order.getState().equals(OrderState.DELIVERED)
                || order.getState().equals(OrderState.CANCELLED)) {
            throw new ValidationException("This order already checkout");
        }
        double discountAmount = 0.0;
        if (discountStrategy != null) {
            discountAmount = discountStrategy.calculateDiscount(order);
        }
        User customer = userRepository.readById(order.getCustomerId());
        if (customer == null) {
            throw new NotFoundException("Not Found customer when checkout");
        }
        order.setDiscount(discountAmount);

        double totalPrice = order.getTotalPrice();

        try {
            customer.deduct(totalPrice);
            userRepository.update(order.getCustomerId(), customer);

            StoreConfig.addRevenue(totalPrice);
            StoreConfig.save();

            order.setPaid(true);
            orderRepository.update(order.getOrderId(), order);

            syncOrderToFile();
            syncUsersToFile();
        } catch (Exception ex) {
            customer.deposit(totalPrice);
            userRepository.update(order.getCustomerId(), customer);

            order.setPaid(false);
            orderRepository.update(order.getOrderId(), order);

            StoreConfig.addRevenue(-totalPrice);
            StoreConfig.save();
            try {
                syncOrderToFile();
            } catch (Exception ignored) {
            }

            throw new ValidationException("Checkout failed due to a system storage error",
                    "PERSISTENCE_ERROR");
        }

        return order;
    }

    @Override
    public void updateStatus(String orderId, OrderState newState) {
        Order order = orderRepository.readById(orderId);
        if (order == null) {
            throw new NotFoundException("Order not found: " + orderId);
        }
        if (newState.equals(OrderState.DELIVERED) && !order.isPaid()) {
            throw new ValidationException("This orders is not paid!!");
        }
        if (newState.equals(OrderState.CANCELLED) && order.isPaid()) {
            throw new ValidationException("This order is paid!! Not Cancelled");
        }
        OrderState oldState = order.getState();
        try {
            order.updateState(newState);
            orderRepository.update(orderId, order);
            syncOrderToFile();
        } catch (InvalidStateException e) {
            throw e;
        } catch (Exception e) {
            try {
                order.updateState(oldState);
                orderRepository.update(orderId, order);
            } catch (Exception ignored) {
            }
            throw new ValidationException("Status update failed: " + e.getMessage(), "UPDATE_FAILED");
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
        Order order = orderRepository.readById(orderId);
        if (order == null)
            throw new NotFoundException("Not Found Order: " + orderId);
        int oldRating = order.getRating();
        String oldComment = order.getComment();
        try {
            order.submitReview(rating, comment);
            orderRepository.update(order.getOrderId(), order);

            syncOrderToFile();
        } catch (InvalidStateException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            try {
                order.submitReview(oldRating, oldComment);
                orderRepository.update(orderId, order);
            } catch (Exception ignored) {
            }
            throw new ValidationException("Review submission failed due to a system error: " + e.getMessage(),
                    "REVIEW_FAILED");
        }
    }

    private void syncOrderToFile() {
        if (orderFilePath == null) {
            return;
        }
        try {
            FileHandler.writeToCsv(orderFilePath, orderRepository.readAll(), CsvMapper::toCsvRow);
        } catch (IOException ioException) {
            throw new ValidationException("Failed to persist user to file: " + ioException);
        }
    }

    private void syncUsersToFile() {
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
        String customerId = parts[1].trim();

        String itemsCompressed = parts[2].trim();
        List<OrderItem> orderItems = new ArrayList<>();

        if (!itemsCompressed.isBlank() && !"NONE".equals(itemsCompressed)) {
            String[] itemTokens = itemsCompressed.split("\\|");
            for (String token : itemTokens) {
                String[] itemParts = token.split(":");
                String menuItemId = itemParts[0];
                int quantity = Integer.parseInt(itemParts[1]);
                MenuItem menuItem = menuRepository.readById(menuItemId);
                orderItems.add(new OrderItem(menuItem, quantity));
            }
        }
        double shippingFee = Double.parseDouble(parts[3]);
        double discount = Double.parseDouble(parts[4]);
        double totalPrice = Double.parseDouble(parts[5]);
        OrderState state = OrderState.valueOf(parts[6]);
        int rating = Integer.parseInt(parts[7]);
        String comment = parts[8].equals("NONE") ? "" : parts[8];
        boolean isPaid = Boolean.parseBoolean(parts[9].trim());
        return new Order(orderId, customerId, orderItems, state, shippingFee, discount, totalPrice, rating,
                comment, isPaid);
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