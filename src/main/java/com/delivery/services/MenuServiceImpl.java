package com.delivery.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.delivery.exception.InvalidStateException;
import com.delivery.exception.NotFoundException;
import com.delivery.exception.ValidationException;
import com.delivery.model.Drink;
import com.delivery.model.Food;
import com.delivery.model.MenuItem;
import com.delivery.model.Order;
import com.delivery.model.OrderState;
import com.delivery.repository.IRepository;
import com.delivery.util.CsvMapper;
import com.delivery.util.FileHandler;

public class MenuServiceImpl implements IMenuService {
    private final IRepository<MenuItem, String> menuRepository;
    private final IRepository<Order, String> orderRepository;
    private final Path filePath;

    public MenuServiceImpl(IRepository<MenuItem, String> menuRepository, IRepository<Order, String> orderRepo,
            Path menuFilePath) {
        this.menuRepository = menuRepository;
        orderRepository = orderRepo;
        this.filePath = menuFilePath;
        loadDataFromCsv();
    }

    @Override
    public void createMenu(MenuItem item) throws Exception {
        validateCanCreate(item);
        menuRepository.create(item);
        try {
            saveToFile();
        } catch (IOException ex) {
            menuRepository.delete(item.getId());
            throw new IOException("Error save to file: " + ex.getMessage());
        }
    }

    @Override
    public List<MenuItem> getAllMenuItem() {
        return menuRepository.readAll();
    }

    @Override
    public MenuItem getMenuItemById(String menuId) throws Exception {
        MenuItem item = menuRepository.readById(menuId);
        if (item == null)
            throw new NotFoundException("Not Found this item " + menuId);
        return item;
    }

    @Override
    public void updateMenuItem(String menuId, MenuItem item) throws Exception {
        var menuOld = menuRepository.readById(menuId);
        if (menuOld == null) {
            throw new NotFoundException("Not found item");
        }
        MenuItem backup = null;
        if (menuOld instanceof Food f) {
            backup = new Food(f.getId(), f.getName(), f.getBasePrice(), f.getDescription(), f.getPortionSize(),
                    f.isVegetarian());
        } else if (menuOld instanceof Drink d) {
            backup = new Drink(d.getId(), d.getName(), d.getBasePrice(), d.getDescription(), d.getSize(),
                    d.getDefaultSurgarLevel(), d.getDefaultIceLevel());
        }
        menuRepository.update(menuId, item);
        try {
            saveToFile();
        } catch (IOException ex) {
            menuRepository.update(menuId, backup); // Khôi phục lại trạng thái cũ trên RAM
            throw new IOException("Cập nhật món ăn thất bại do lỗi ghi đĩa: " + ex.getMessage());
        }
    }

    @Override
    public void deleteMenuItem(String menuId) throws Exception {
        MenuItem item = menuRepository.readById(menuId);
        if (item == null)
            throw new NotFoundException("Not Found this item " + menuId);
        List<Order> activeOrders = orderRepository.readAll();
        for (Order order : activeOrders) {
            if (order.getState() == OrderState.CREATED || order.getState() == OrderState.PREPARING) {
                // Kiểm tra xem món ăn chuẩn bị xóa có nằm trong danh sách OrderItem của đơn
                // hàng không
                boolean isUsed = order.getItems().stream()
                        .anyMatch(orderItem -> orderItem.getMenuItem().getId().equals(menuId));
                if (isUsed) {
                    throw new InvalidStateException(
                            "Không thể xóa món ăn này vì đang nằm trong đơn hàng chưa hoàn thành của ngày hôm nay!",
                            "DELETE_RESTRICTED");
                }
            }
        }
        menuRepository.delete(menuId);
        try {
            saveToFile();
        } catch (IOException e) {
            menuRepository.create(item);
            throw new IOException("Not delete: " + e.getMessage());
        }
    }

    private void validateCanCreate(MenuItem item) {
        if (item == null) {
            throw new ValidationException("Menu Item is required!");
        }
        if (item.getId() == null || item.getId().isBlank())
            throw new ValidationException("MenuItem id is required!");
        var items = menuRepository.readAll();
        for (MenuItem item2 : items) {
            if (item.getId().equals(item2.getId()))
                throw new ValidationException("MenuItem id already exsiting!");
        }
        if (item.getName() == null || item.getName().isBlank())
            throw new ValidationException("MenuItem Name is required!");

        if (item.getBasePrice() <= 0)
            throw new ValidationException("MenuItem BasePrice is not minus!");
        if (item.getDescription() == null || item.getDescription().isBlank())
            throw new ValidationException("MenuItem Description is required!");

    }

    private void saveToFile() throws Exception {
        if (filePath == null) {
            return;
        }
        FileHandler.writeToCsv(filePath, menuRepository.readAll(), CsvMapper::toCsvRow);
    }

    private static MenuItem parseMenuItem(String line) {
        String[] parts = line.split(",");
        String type = parts[0].trim();
        String id = parts[1].trim();
        String name = parts[2].trim();
        double price = Double.parseDouble(parts[3].trim());
        String desc = parts[4].trim();

        if ("FOOD".equalsIgnoreCase(type)) {
            String portionSize = parts[5].trim();
            boolean isVegetarian = Boolean.parseBoolean(parts[6].trim());
            return new Food(id, name, price, desc, portionSize, isVegetarian);
        } else if ("DRINK".equalsIgnoreCase(type)) {
            String size = parts[5].trim();
            int sugar = Integer.parseInt(parts[6].trim());
            int ice = Integer.parseInt(parts[7].trim());
            return new Drink(id, name, price, desc, size, sugar, ice);
        }
        throw new IllegalArgumentException("Không xác định được loại thực phẩm: " + type);
    }


    private void loadDataFromCsv() {
        try {
            List<MenuItem> menu = FileHandler.readFromTextFile(filePath, MenuServiceImpl::parseMenuItem);
            for (MenuItem item : menu) {
                menuRepository.create(item);
            }
        } catch (Exception e) {
            System.out.println("Not found the Csv");
        }
    }
}
