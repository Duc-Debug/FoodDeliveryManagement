package com.delivery.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.delivery.exception.NotFoundException;
import com.delivery.exception.ValidationException;
import com.delivery.model.Customer;
import com.delivery.model.Merchant;
import com.delivery.model.User;
import com.delivery.repository.IRepository;
import com.delivery.util.FileHandler;
import com.delivery.util.CsvMapper;

public class UserServiceImpl implements IUserService {
    private final IRepository<User, String> userRepository;
    private final Path filePath;

    public UserServiceImpl(IRepository<User, String> uRepository, Path filePath) {
        userRepository = uRepository;
        this.filePath = filePath;
        loadDataFromCsv();
    }

    @Override
    public void registerUser(User user) throws Exception {
        validateCanRegister(user);
        userRepository.create(user);
        saveToFile(user.getId());
    }

    @Override
    public User getUserById(String id) {
        User user = userRepository.readById(id);
        if (user == null) {
            throw new NotFoundException("User Not found: " + id);
        }
        user.getDetails();
        return user;
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.readAll();
    }

    @Override
    public void depositMoney(String userId, double amount) throws Exception {
        User user = userRepository.readById(userId);
        if (user == null) {
            throw new NotFoundException("User Not found: " + userId);
        }
        user.deposit(amount);
        userRepository.update(userId, user);
        saveToFile(userId);
    }

    @Override
    public void updateUser(String userId, User user) throws Exception {
        userRepository.update(userId, user);
    }

    @Override
    public void deleteUser(String id) throws Exception {
        userRepository.delete(id);
    }

    private void validateCanRegister(User user) {
        if (user == null) {
            throw new ValidationException("User is required!", "INVALID_INPUT");
        }
        if (user.getId() == null || user.getId().isBlank()) {
            throw new ValidationException("User id is requied!", "INVALID_INPUT");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("User name is requied!", "INVALID_INPUT");
        }
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isBlank()) {
            throw new ValidationException("User PhoneNumber is requied!", "INVALID_INPUT");
        }
        if (user.getBalance() < 0) {
            throw new ValidationException("User balance is not minus!", "INVALID_INPUT");
        }
        if (user.getAddress() == null || user.getAddress().isBlank()) {
            throw new ValidationException("Customer Address is requied!", "INVALID_INPUT");
        }

    }

    private void saveToFile(String createdUserId) throws Exception {
        if (filePath == null) {
            return;
        }
        try {
            FileHandler.writeToCsv(filePath, userRepository.readAll(), CsvMapper::toCsvRow);
        } catch (IOException ioException) {
            userRepository.delete(createdUserId);
            throw new IOException("Failed to persist user to file: " + filePath, ioException);
        }
    }

    private static User parseUserCsv(String line) {
        String[] parts = line.split(",");
        String role = parts[0];
        if ("CUSTOMER".equalsIgnoreCase(role)) {
            return new User(parts[1], parts[2], parts[3], Double.parseDouble(parts[4]), parts[5]);
        } else if ("MERCHANT".equalsIgnoreCase(role)) {
            return new Merchant(parts[1], parts[2], parts[3], Double.parseDouble(parts[4]), parts[5],
                    Double.parseDouble(parts[6]), Integer.parseInt(parts[7]));
        }
        throw new IllegalArgumentException("Data role not accept in file" + role);
    }

    private void loadDataFromCsv() {
        try {
            List<User> users = FileHandler.readFromTextFile(filePath, UserServiceImpl::parseUserCsv);
            for (User user : users) {
                userRepository.create(user);
            }
        } catch (Exception e) {
            System.out.println("Not found the Csv" + e.getMessage());
        }
    }

}
