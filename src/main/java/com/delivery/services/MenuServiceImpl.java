package com.delivery.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.delivery.exception.NotFoundException;
import com.delivery.exception.ValidationException;
import com.delivery.model.MenuItem;
import com.delivery.repository.IRepository;
import com.delivery.util.CsvMapper;
import com.delivery.util.FileHandler;

public class MenuServiceImpl implements IMenuService {
    private final IRepository<MenuItem, String> menuRepository;
    private final Path filePath;

    public MenuServiceImpl(IRepository<MenuItem, String> menuRepository, Path menuFilePath) {
        this.menuRepository = menuRepository;
        this.filePath = menuFilePath;
        loadDataFromCsv();
    }

    @Override
    public void createMenu(MenuItem item) throws Exception {
        validateCanCreate(item);
        menuRepository.create(item);
        saveToFile(item.getId());
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
       menuRepository.update(menuId, item);
    }

    @Override
    public void deleteMenuItem(String menuId) throws Exception {
         MenuItem item = menuRepository.readById(menuId);
        if (item == null)
            throw new NotFoundException("Not Found this item " + menuId);
        menuRepository.delete(menuId);
    }

    private void validateCanCreate(MenuItem item) {
        if (item == null) {
            throw new ValidationException("Menu Item is required!");
        }
        if (item.getId() == null || item.getId().isBlank())
            throw new ValidationException("MenuItem id is required!");
        if (item.getName() == null || item.getName().isBlank())
            throw new ValidationException("MenuItem Name is required!");
        if (item.getMerchantId() == null || item.getMerchantId().isBlank())
            throw new ValidationException("MenuItem Merchant id is required!");
        if (item.getBasePrice() < 0)
            throw new ValidationException("MenuItem BasePrice is not minus!");
        if (item.getDescription() == null || item.getDescription().isBlank())
            throw new ValidationException("MenuItem Description is required!");

    }

    private void saveToFile(String createdItemId) throws Exception {
        if (filePath == null) {
            return;
        }
        try {
            FileHandler.writeToCsv(filePath, menuRepository.readAll(), CsvMapper::toCsvRow);
        } catch (IOException ioException) {
            menuRepository.delete(createdItemId);
            throw new IOException("Failed to persist user to file: " + filePath, ioException);
        }
    }
    private static MenuItem parseMenuItem(String line) {
        String[] parts = line.split(",");
        return new MenuItem(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]), parts[4]);
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
